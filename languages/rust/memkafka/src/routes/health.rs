use axum::routing::get;
use axum::Router;

use crate::handlers::health::health;
use crate::state::AppState;

pub fn router() -> Router<AppState> {
    Router::new().route("/health", get(health))
}