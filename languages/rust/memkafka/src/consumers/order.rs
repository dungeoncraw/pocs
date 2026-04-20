use rdkafka::consumer::{Consumer, StreamConsumer};
use rdkafka::ClientConfig;
use rdkafka::Message;
use tokio_stream::StreamExt;

pub async fn consume_order_payment() {
    consume_orders("payment-group", "Payment Service").await;
}

pub async fn consume_order_notification() {
    consume_orders("notification-group", "Notification Service").await;
}

async fn consume_orders(group_id: &str, service_name: &str) {
    let bootstrap_servers =
        std::env::var("KAFKA_BOOTSTRAP_SERVERS").unwrap_or_else(|_| "localhost:9092".to_string());

    let consumer: StreamConsumer = ClientConfig::new()
        .set("group.id", group_id)
        .set("bootstrap.servers", &bootstrap_servers)
        .set("enable.auto.commit", "false")
        .set("auto.offset.reset", "earliest")
        .create()
        .expect("Consumer creation failed");

    let topic = "order.created";
    consumer.subscribe(&[topic]).expect("Failed to subscribe to topic");

    tracing::info!("[{}] Subscribed to topic '{}' on {}", service_name, topic, bootstrap_servers);

    let mut stream = consumer.stream();

    while let Some(message) = stream.next().await {
        match message {
            Ok(msg) => {
                if let Some(payload) = msg.payload_view::<str>().transpose().unwrap_or(None) {
                    tracing::info!("[{}] Received order: {}", service_name, payload);
                    // nothing for now
                }

                if let Err(err) = consumer.commit_message(&msg, rdkafka::consumer::CommitMode::Async) {
                    tracing::error!("[{}] commit failed: {err}", service_name);
                }
            }
            Err(err) => {
                tracing::error!("[{}] Kafka error: {err}", service_name);
            }
        }
    }
}
