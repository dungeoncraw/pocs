use axum::{extract::State, response::IntoResponse, Json};
use crate::error::AppError;
use crate::models::order::{Order, OrderResponse};
use crate::state::AppState;
use rdkafka::producer::FutureRecord;
use std::time::Duration;
use axum::http::StatusCode;

pub async fn create_order(
    State(state): State<AppState>,
    Json(payload): Json<Order>,
) -> Result<impl IntoResponse, AppError> {
    let payload_json = serde_json::to_string(&payload)
        .map_err(|e| AppError::new(StatusCode::BAD_REQUEST, format!("Serialization error: {}", e)))?;

    let order_id = payload.order_id.clone();
    let record = FutureRecord::to("order.created")
        .payload(&payload_json)
        .key(&order_id);

    match state.producer.send(record, Duration::from_secs(1)).await {
        Ok(_) => {
            tracing::info!("Order {} produced to order.created", order_id);
            Ok(Json(OrderResponse { 
                status: "ok",
                order_id,
            }))
        },
        Err((e, _)) => {
            tracing::error!("Failed to send order to Kafka: {:?}", e);
            Err(AppError::new(StatusCode::INTERNAL_SERVER_ERROR, format!("Kafka error: {}", e)))
        }
    }
}
