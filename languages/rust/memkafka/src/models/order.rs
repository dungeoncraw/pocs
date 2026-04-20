use serde::{Deserialize, Serialize};

#[derive(Debug, Deserialize, Serialize, Clone)]
pub struct Order {
    pub order_id: String,
    pub user_id: String,
    pub items: Vec<OrderItem>,
    pub total_amount: f64,
}

#[derive(Debug, Deserialize, Serialize, Clone)]
pub struct OrderItem {
    pub product_id: String,
    pub quantity: u32,
    pub price: f64,
}

#[derive(Serialize)]
pub struct OrderResponse {
    pub status: &'static str,
    pub order_id: String,
}
