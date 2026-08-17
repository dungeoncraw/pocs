use axum::{
    extract::{Path, Query},
    http::StatusCode,
    response::{IntoResponse, Response},
    routing::get,
    Json, Router,
};
use serde::{Deserialize, Serialize};

use crate::error::MessageError;
use crate::message::{get_message, Mood};

#[derive(Debug, Serialize, Deserialize, PartialEq, Eq)]
pub struct MessageResponse {
    pub message: String,
}

#[derive(Debug, Serialize, Deserialize, PartialEq, Eq)]
pub struct ErrorResponse {
    pub error: String,
}

#[derive(Debug, Deserialize)]
pub struct MessageQuery {
    pub mood: String,
    pub day: u8,
}

impl IntoResponse for MessageError {
    fn into_response(self) -> Response {
        let (status, error_message) = match self {
            MessageError::InvalidMood => (StatusCode::BAD_REQUEST, self.to_string()),
            MessageError::InvalidDay => (StatusCode::BAD_REQUEST, self.to_string()),
            MessageError::NotFound => (StatusCode::NOT_FOUND, self.to_string()),
            MessageError::DbError(_) => (StatusCode::INTERNAL_SERVER_ERROR, self.to_string()),
        };

        (status, Json(ErrorResponse { error: error_message })).into_response()
    }
}

pub async fn health_handler() -> &'static str {
    "OK"
}

pub async fn get_message_path_handler(
    Path((mood, day)): Path<(String, u8)>,
) -> Result<Json<MessageResponse>, MessageError> {

    let mood = mood.parse::<Mood>()?;
    let message = get_message(&mood, day)?;
    Ok(Json(MessageResponse { message }))
}

pub async fn get_message_query_handler(
    Query(query): Query<MessageQuery>,
) -> Result<Json<MessageResponse>, MessageError> {
    let mood = query.mood.parse::<Mood>()?;
    let message = get_message(&mood, query.day)?;
    Ok(Json(MessageResponse { message }))
}

pub fn create_router() -> Router {
    Router::new()
        .route("/health", get(health_handler))
        .route("/message", get(get_message_query_handler))
        .route("/message/{mood}/{day}", get(get_message_path_handler))
}

pub async fn serve(listener: tokio::net::TcpListener) -> Result<(), std::io::Error> {
    let app = create_router();
    axum::serve(listener, app).await
}

#[cfg(test)]
mod tests {
    use super::*;
    use axum::{
        body::to_bytes,
        http::{Request, StatusCode},
    };
    use tower::ServiceExt;

    #[tokio::test]
    async fn test_health_check() {
        let app = create_router();

        let response = app
            .oneshot(
                Request::builder()
                    .uri("/health")
                    .body(axum::body::Body::empty())
                    .unwrap(),
            )
            .await
            .unwrap();

        assert_eq!(response.status(), StatusCode::OK);

        let body = to_bytes(response.into_body(), usize::MAX).await.unwrap();
        assert_eq!(&body[..], b"OK");
    }

    #[tokio::test]
    async fn test_get_message_path_success() {
        let app = create_router();

        let response = app
            .oneshot(
                Request::builder()
                    .uri("/message/happy/3")
                    .body(axum::body::Body::empty())
                    .unwrap(),
            )
            .await
            .unwrap();

        assert_eq!(response.status(), StatusCode::OK);

        let body = to_bytes(response.into_body(), usize::MAX).await.unwrap();
        let resp_json: MessageResponse = serde_json::from_slice(&body).unwrap();
        assert_eq!(
            resp_json,
            MessageResponse {
                message: "The road ahead looks strangely quiet.".to_string(),
            }
        );
    }

    #[tokio::test]
    async fn test_get_message_path_invalid_mood() {
        let app = create_router();

        let response = app
            .oneshot(
                Request::builder()
                    .uri("/message/excited/3")
                    .body(axum::body::Body::empty())
                    .unwrap(),
            )
            .await
            .unwrap();

        assert_eq!(response.status(), StatusCode::BAD_REQUEST);

        let body = to_bytes(response.into_body(), usize::MAX).await.unwrap();
        let resp_json: ErrorResponse = serde_json::from_slice(&body).unwrap();
        assert_eq!(
            resp_json,
            ErrorResponse {
                error: "invalid mood".to_string(),
            }
        );
    }

    #[tokio::test]
    async fn test_get_message_path_invalid_day() {
        let app = create_router();

        let response = app
            .oneshot(
                Request::builder()
                    .uri("/message/happy/10")
                    .body(axum::body::Body::empty())
                    .unwrap(),
            )
            .await
            .unwrap();

        assert_eq!(response.status(), StatusCode::BAD_REQUEST);

        let body = to_bytes(response.into_body(), usize::MAX).await.unwrap();
        let resp_json: ErrorResponse = serde_json::from_slice(&body).unwrap();
        assert_eq!(
            resp_json,
            ErrorResponse {
                error: "invalid day".to_string(),
            }
        );
    }

    #[tokio::test]
    async fn test_get_message_query_success() {
        let app = create_router();

        let response = app
            .oneshot(
                Request::builder()
                    .uri("/message?mood=sad&day=5")
                    .body(axum::body::Body::empty())
                    .unwrap(),
            )
            .await
            .unwrap();

        assert_eq!(response.status(), StatusCode::OK);

        let body = to_bytes(response.into_body(), usize::MAX).await.unwrap();
        let resp_json: MessageResponse = serde_json::from_slice(&body).unwrap();
        assert_eq!(
            resp_json,
            MessageResponse {
                message: "The road ahead looks strangely quiet.".to_string(),
            }
        );
    }

    #[tokio::test]
    async fn test_get_message_query_invalid_mood() {
        let app = create_router();

        let response = app
            .oneshot(
                Request::builder()
                    .uri("/message?mood=confused&day=1")
                    .body(axum::body::Body::empty())
                    .unwrap(),
            )
            .await
            .unwrap();

        assert_eq!(response.status(), StatusCode::BAD_REQUEST);

        let body = to_bytes(response.into_body(), usize::MAX).await.unwrap();
        let resp_json: ErrorResponse = serde_json::from_slice(&body).unwrap();
        assert_eq!(
            resp_json,
            ErrorResponse {
                error: "invalid mood".to_string(),
            }
        );
    }

    #[tokio::test]
    async fn test_get_message_query_invalid_day() {
        let app = create_router();

        let response = app
            .oneshot(
                Request::builder()
                    .uri("/message?mood=happy&day=0")
                    .body(axum::body::Body::empty())
                    .unwrap(),
            )
            .await
            .unwrap();

        assert_eq!(response.status(), StatusCode::BAD_REQUEST);

        let body = to_bytes(response.into_body(), usize::MAX).await.unwrap();
        let resp_json: ErrorResponse = serde_json::from_slice(&body).unwrap();
        assert_eq!(
            resp_json,
            ErrorResponse {
                error: "invalid day".to_string(),
            }
        );
    }

    #[tokio::test]
    async fn test_route_not_found() {
        let app = create_router();

        let response = app
            .oneshot(
                Request::builder()
                    .uri("/nonexistent")
                    .body(axum::body::Body::empty())
                    .unwrap(),
            )
            .await
            .unwrap();

        assert_eq!(response.status(), StatusCode::NOT_FOUND);
    }
}
