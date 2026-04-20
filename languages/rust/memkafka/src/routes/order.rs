use axum::{routing::post, Router};
use crate::handlers::order::create_order;
use crate::state::AppState;

pub fn order_routes() -> Router<AppState> {
    Router::new().route("/orders", post(create_order))
}
