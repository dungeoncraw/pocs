use axum::{extract::State, response::IntoResponse, Json};
use crate::error::AppError;
use crate::models::event::{Event, EventResponse};
use crate::state::AppState;
use rdkafka::producer::FutureRecord;
use std::time::Duration;
use axum::http::StatusCode;

pub async fn event(
    State(state): State<AppState>,
    Json(payload): Json<Event>,
) -> Result<impl IntoResponse, AppError> {
    let payload_json = serde_json::to_string(&payload)
        .map_err(|e| AppError::new(StatusCode::BAD_REQUEST, format!("Serialization error: {}", e)))?;

    let record = FutureRecord::to("events")
        .payload(&payload_json)
        .key(&payload.sub_id);

    match state.producer.send(record, Duration::from_secs(1)).await {
        Ok(_) => Ok(Json(EventResponse { status: "ok" })),
        Err((e, _)) => {
            tracing::error!("Failed to send message to Kafka: {:?}", e);
            Err(AppError::new(StatusCode::INTERNAL_SERVER_ERROR, format!("Kafka error: {}", e)))
        }
    }
}