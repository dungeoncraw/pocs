mod app;
mod error;
mod handlers;
mod models;
mod routes;
mod state;

#[tokio::main]
async fn main() {
    tracing_subscriber::fmt()
        .with_env_filter(tracing_subscriber::EnvFilter::from_default_env())
        .init();

    let app = app::build_app();

    let listener = tokio::net::TcpListener::bind("127.0.0.1:3000")
        .await
        .expect("failed to bind address");

    println!("Listening on http://127.0.0.1:3000");
    axum::serve(listener, app)
        .await
        .expect("server error");
}