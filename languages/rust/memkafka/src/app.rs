use axum::Router;
use tower_http::trace::TraceLayer;

use crate::{routes, state::AppState};

pub fn build_app() -> Router {
    let state = AppState::new();

    Router::new()
        .merge(routes::router())
        .layer(TraceLayer::new_for_http())
        .with_state(state)
}