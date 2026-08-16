use message_of_day::http::serve;
use tokio::net::TcpListener;

#[tokio::main]
async fn main() -> anyhow::Result<()> {
    let addr = "0.0.0.0:3000";
    let listener = TcpListener::bind(addr).await?;
    println!("Server running on http://{addr}");
    serve(listener).await?;
    Ok(())
}
