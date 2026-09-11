use std::{future::Future, io, net::SocketAddr, time::Duration};
use tokio::{
    io::AsyncWriteExt,
    net::{TcpListener, TcpStream},
    task::{JoinError, JoinSet},
    time::{sleep, timeout},
};

const DEFAULT_WORK_DURATION: Duration = Duration::from_secs(10);
const DEFAULT_TIMEOUT_DURATION: Duration = Duration::from_secs(15);

#[tokio::main]
async fn main() -> io::Result<()> {
    let listener = TcpListener::bind("127.0.0.1:8080").await?;
    println!("Listening on 127.0.0.1:8080");
    println!("Press Ctrl+C to start graceful shutdown");

    let shutdown = tokio::signal::ctrl_c();
    run_server(
        listener,
        shutdown,
        DEFAULT_WORK_DURATION,
        DEFAULT_TIMEOUT_DURATION,
    )
    .await
}

async fn run_server<F>(
    listener: TcpListener,
    shutdown: F,
    work_duration: Duration,
    timeout_duration: Duration,
) -> io::Result<()>
where
    F: Future<Output = io::Result<()>>,
{
    let mut connections = JoinSet::new();

    tokio::pin!(shutdown);

    let result = loop {
        tokio::select! {
            // instead of picking up whichever finish first,
            // keep the defined order as also a priority, so if both branches finish at the same time,
            // the first defined here is going to be picked up
            biased;

            signal = &mut shutdown => {
                println!("Shutdown requested");
                break signal;
            }

            completed = connections.join_next(), if !connections.is_empty() => {
                if let Some(completed) = completed {
                    report(completed);
                }
            }

            accepted = listener.accept() => {
                match accepted {
                    Ok((stream, address)) => {
                        println!("Accepted {address}");

                        connections.spawn(async move {
                            match timeout(
                                timeout_duration,
                                handle_connection(stream, address, work_duration),
                            ).await {
                                Ok(result) => result,
                                Err(_) => Err(io::Error::new(
                                    io::ErrorKind::TimedOut,
                                    format!("Connection {address} timed out"),
                                )),
                            }
                        });
                    }
                    Err(error) => break Err(error),
                }
            }
        }
    };

    drop(listener);

    println!("Listener closed; no new connections will be accepted");
    println!("Waiting for {} connection(s)", connections.len());

    while let Some(completed) = connections.join_next().await {
        report(completed);
    }

    println!("Server stopped");

    result
}

async fn handle_connection(
    mut stream: TcpStream,
    address: SocketAddr,
    work_duration: Duration,
) -> io::Result<()> {
    println!("Processing {address}");

    sleep(work_duration).await;

    stream.write_all(b"Work completed successfully\n").await?;
    stream.shutdown().await?;

    println!("Finished {address}");

    Ok(())
}

fn format_report(result: &Result<io::Result<()>, JoinError>) -> Option<String> {
    match result {
        Ok(Ok(())) => None,
        Ok(Err(error)) => Some(format!("Connection error: {error}")),
        Err(error) => Some(format!("Task failed: {error}")),
    }
}

fn report(result: Result<io::Result<()>, JoinError>) {
    if let Some(message) = format_report(&result) {
        eprintln!("{message}");
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use tokio::io::AsyncReadExt;
    use tokio::sync::oneshot;

    #[tokio::test]
    async fn test_format_report_success() {
        let success: Result<io::Result<()>, JoinError> = Ok(Ok(()));
        assert_eq!(format_report(&success), None);
        report(success);
    }

    #[tokio::test]
    async fn test_format_report_connection_error() {
        let err: Result<io::Result<()>, JoinError> = Ok(Err(io::Error::new(
            io::ErrorKind::TimedOut,
            "Connection 127.0.0.1:8080 timed out",
        )));
        assert_eq!(
            format_report(&err),
            Some("Connection error: Connection 127.0.0.1:8080 timed out".to_string())
        );
        report(err);
    }

    #[tokio::test]
    async fn test_format_report_task_failure() {
        let mut set = JoinSet::new();
        set.spawn(async {
            panic!("task panicked intentional for test");
        });

        let completed = set.join_next().await.expect("task completed");
        let formatted = format_report(&completed);
        assert!(formatted.is_some());
        assert!(formatted.unwrap().starts_with("Task failed: "));
        report(completed);
    }

    #[tokio::test]
    async fn test_handle_connection_success() {
        let listener = TcpListener::bind("127.0.0.1:0").await.unwrap();
        let addr = listener.local_addr().unwrap();

        let server_task = tokio::spawn(async move {
            let (stream, client_addr) = listener.accept().await.unwrap();
            handle_connection(stream, client_addr, Duration::from_millis(50)).await
        });

        let mut client_stream = TcpStream::connect(addr).await.unwrap();
        let mut buffer = String::new();
        client_stream.read_to_string(&mut buffer).await.unwrap();

        assert_eq!(buffer, "Work completed successfully\n");

        let server_result = server_task.await.unwrap();
        assert!(server_result.is_ok());
    }

    #[tokio::test]
    async fn test_run_server_immediate_shutdown() {
        let listener = TcpListener::bind("127.0.0.1:0").await.unwrap();
        let shutdown_future = async { Ok(()) };

        let result = run_server(
            listener,
            shutdown_future,
            Duration::from_millis(10),
            Duration::from_millis(50),
        )
        .await;

        assert!(result.is_ok());
    }

    #[tokio::test]
    async fn test_run_server_graceful_shutdown_drains_connections() {
        let listener = TcpListener::bind("127.0.0.1:0").await.unwrap();
        let addr = listener.local_addr().unwrap();

        let (shutdown_tx, shutdown_rx) = oneshot::channel();
        let shutdown_future = async move {
            let _ = shutdown_rx.await;
            Ok(())
        };

        let server_handle = tokio::spawn(run_server(
            listener,
            shutdown_future,
            Duration::from_millis(100),
            Duration::from_millis(500),
        ));

        // Connect a client before shutdown signal
        let mut client_stream = TcpStream::connect(addr).await.unwrap();

        // Small pause to allow server to accept connection
        tokio::time::sleep(Duration::from_millis(20)).await;

        // Trigger shutdown signal while request is in flight
        let _ = shutdown_tx.send(());

        // The in-flight client connection must still complete successfully
        let mut buffer = String::new();
        client_stream.read_to_string(&mut buffer).await.unwrap();
        assert_eq!(buffer, "Work completed successfully\n");

        // Server should shut down cleanly
        let server_result = server_handle.await.unwrap();
        assert!(server_result.is_ok());
    }

    #[tokio::test]
    async fn test_run_server_timeout_handling() {
        let listener = TcpListener::bind("127.0.0.1:0").await.unwrap();
        let addr = listener.local_addr().unwrap();

        let (shutdown_tx, shutdown_rx) = oneshot::channel();
        let shutdown_future = async move {
            let _ = shutdown_rx.await;
            Ok(())
        };

        // Work takes 200ms, but timeout is 50ms
        let server_handle = tokio::spawn(run_server(
            listener,
            shutdown_future,
            Duration::from_millis(200),
            Duration::from_millis(50),
        ));

        let mut client_stream = TcpStream::connect(addr).await.unwrap();

        // Wait until connection times out
        tokio::time::sleep(Duration::from_millis(100)).await;

        // Client shouldn't receive completed work payload
        let mut buffer = String::new();
        client_stream.read_to_string(&mut buffer).await.unwrap();
        assert!(buffer.is_empty());

        // Shutdown server
        let _ = shutdown_tx.send(());
        let server_result = server_handle.await.unwrap();
        assert!(server_result.is_ok());
    }
}