pub mod event;
pub mod order;

pub fn start_consumers() {
    tokio::spawn(event::consume_event());
    tokio::spawn(order::consume_order_payment());
    tokio::spawn(order::consume_order_notification());
}