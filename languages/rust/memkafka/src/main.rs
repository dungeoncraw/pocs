mod app;
mod error;
mod handlers;
mod models;
mod routes;
mod state;
pub mod consumers;

#[tokio::main]
async fn main() {
    tracing_subscriber::fmt()
        .with_env_filter(tracing_subscriber::EnvFilter::from_default_env())
        .init();
    // start Kafka consumers
    consumers::start_consumers();

    let app = app::build_app();

    let addr = std::env::var("APP_ADDR").unwrap_or_else(|_| "127.0.0.1:3000".to_string());
    let listener = tokio::net::TcpListener::bind(&addr)
        .await
        .expect("failed to bind address");

    println!("Listening on http://{}", addr);
    tracing::info!("Listening on http://{}", addr);
    axum::serve(listener, app)
        .await
        .expect("server error");
}