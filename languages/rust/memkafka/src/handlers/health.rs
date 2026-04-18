use axum::Json;

use crate::models::health::HealthResponse;

pub async fn health() -> Json<HealthResponse> {
    Json(HealthResponse { status: "ok" })
}