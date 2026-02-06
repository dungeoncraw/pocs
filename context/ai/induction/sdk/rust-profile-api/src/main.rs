use axum::{
    extract::Path,
    http::StatusCode,
    response::IntoResponse,
    routing::get,
    Json, Router,
};
use serde::{Deserialize, Serialize};
use std::net::SocketAddr;

#[derive(Serialize, Deserialize, Debug, Clone)]
#[serde(rename_all = "PascalCase")]
enum Action {
    CallReal,
    MockWhole,
    Mutate,
    Exception,
    Delay,
}

#[derive(Serialize, Deserialize, Debug, Clone)]
struct Transformation {
    field: String,
    value: String,
}

#[derive(Serialize, Deserialize, Debug, Clone)]
struct Profile {
    id: String,
    action: Action,
    #[serde(skip_serializing_if = "Option::is_none")]
    mockResponse: Option<String>,
    #[serde(default)]
    mutations: Vec<Transformation>,
    #[serde(skip_serializing_if = "Option::is_none")]
    exceptionMessage: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    delayMs: Option<u64>,
}

#[tokio::main]
async fn main() {
    // Initialize tracing
    tracing_subscriber::fmt::init();

    let app = Router::new()
        .route("/profiles/ping", get(ping))
        .route("/profiles/:id", get(get_profile));

    let addr = SocketAddr::from(([0, 0, 0, 0], 3000));
    tracing::info!("listening on {}", addr);
    let listener = tokio::net::TcpListener::bind(addr).await.unwrap();
    axum::serve(listener, app).await.unwrap();
}

async fn ping() -> &'static str {
    "pong"
}

async fn get_profile(Path(id): Path<String>) -> impl IntoResponse {
    tracing::info!("Fetching profile for id: {}", id);
    
    if id == "user-not-paid-profile" {
        let profile = Profile {
            id,
            action: Action::MockWhole,
            mockResponse: Some(r#"{"status": "error", "message": "Payment Required from Rust API"}"#.to_string()),
            mutations: vec![],
            exceptionMessage: None,
            delayMs: None,
        };
        (StatusCode::OK, Json(profile)).into_response()
    } else if id == "rust-exception-profile" {
        let profile = Profile {
            id,
            action: Action::Exception,
            mockResponse: None,
            mutations: vec![],
            exceptionMessage: Some("Critical Error Induced by Rust API".to_string()),
            delayMs: None,
        };
        (StatusCode::OK, Json(profile)).into_response()
    } else {
        (StatusCode::NOT_FOUND, "Profile not found").into_response()
    }
}
