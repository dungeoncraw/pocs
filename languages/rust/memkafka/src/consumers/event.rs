use rdkafka::consumer::{Consumer, StreamConsumer};
use rdkafka::ClientConfig;
use rdkafka::Message;
use tokio_stream::StreamExt;

pub async fn consume_event() {
    let bootstrap_servers =
        std::env::var("KAFKA_BOOTSTRAP_SERVERS").unwrap_or_else(|_| "localhost:9092".to_string());

    let consumer: StreamConsumer = ClientConfig::new()
        .set("group.id", "kafka-group")
        .set("bootstrap.servers", &bootstrap_servers)
        .set("enable.auto.commit", "false")
        .set("auto.offset.reset", "earliest")
        .create()
        .expect("Consumer creation failed");

    consumer.subscribe(&["events"]).expect("Failed to subscribe to topic");

    tracing::info!("Subscribed to topic 'events' on {}", bootstrap_servers);

    let mut stream = consumer.stream();

    while let Some(message) = stream.next().await {
        match message {
            Ok(msg) => {
                if let Some(payload) = msg.payload_view::<str>().transpose().unwrap_or(None) {
                    tracing::info!("Received: {payload}");
                }

                if let Err(err) = consumer.commit_message(&msg, rdkafka::consumer::CommitMode::Async) {
                    tracing::error!("commit failed: {err}");
                }
            }
            Err(err) => {
                tracing::error!("Kafka error: {err}");
            }
        }
    }
}