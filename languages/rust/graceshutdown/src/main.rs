use std::{io, net::SocketAddr, time::Duration};
use tokio::{
    io::AsyncWriteExt,
    net::{TcpListener, TcpStream},
    task::{JoinError, JoinSet},
    time::{sleep, timeout},
};

#[tokio::main]
async fn main() -> io::Result<()> {
    let listener = TcpListener::bind("127.0.0.1:8080").await?;
    let mut connections = JoinSet::new();

    let shutdown = tokio::signal::ctrl_c();
    tokio::pin!(shutdown);

    println!("Listening on 127.0.0.1:8080");
    println!("Press Ctrl+C to start graceful shutdown");

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
                                Duration::from_secs(15),
                                handle_connection(stream, address),
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
) -> io::Result<()> {
    println!("Processing {address}");

    sleep(Duration::from_secs(10)).await;

    stream.write_all(b"Work completed successfully\n").await?;
    stream.shutdown().await?;

    println!("Finished {address}");

    Ok(())
}

fn report(result: Result<io::Result<()>, JoinError>) {
    match result {
        Ok(Ok(())) => {}
        Ok(Err(error)) => eprintln!("Connection error: {error}"),
        Err(error) => eprintln!("Task failed: {error}"),
    }
}