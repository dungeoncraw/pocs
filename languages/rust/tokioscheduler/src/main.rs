use std::time::Duration;
use tokio::time::sleep;

async fn process_order(order_id: u32) -> String {
    println!("Order {order_id}: received");

    sleep(Duration::from_secs(2)).await;

    println!("Order {order_id}: payment confirmed");

    sleep(Duration::from_secs(1)).await;

    println!("Order {order_id}: shipped");

    format!("Order {order_id} completed")
}

#[tokio::main]
async fn main() {
    let order_1 = tokio::spawn(process_order(101));
    let order_2 = tokio::spawn(process_order(102));
    let order_3 = tokio::spawn(process_order(103));

    let result_1 = order_1.await.unwrap();
    let result_2 = order_2.await.unwrap();
    let result_3 = order_3.await.unwrap();

    println!("{result_1}");
    println!("{result_2}");
    println!("{result_3}");
}