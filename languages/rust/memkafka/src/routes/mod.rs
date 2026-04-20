pub mod health;
pub mod event;
pub mod order;

use axum::Router;
use crate::state::AppState;

pub fn router() -> Router<AppState> {
    Router::new()
        .merge(health::router())
        .merge(event::router())
        .merge(order::order_routes())
}