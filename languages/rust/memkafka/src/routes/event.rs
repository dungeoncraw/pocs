use axum::{routing::post, Router};
use crate::handlers::event::event;
use crate::state::AppState;

pub fn router() -> Router<AppState> {
    Router::new().route("/events", post(event))
}
