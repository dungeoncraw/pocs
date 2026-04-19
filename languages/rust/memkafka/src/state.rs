use rdkafka::producer::FutureProducer;

#[derive(Clone)]
pub struct AppState {
    pub producer: FutureProducer,
}

impl AppState {
    pub fn new() -> Self {
        let bootstrap_servers =
            std::env::var("KAFKA_BOOTSTRAP_SERVERS").unwrap_or_else(|_| "localhost:9092".to_string());

        let producer: FutureProducer = rdkafka::config::ClientConfig::new()
            .set("bootstrap.servers", &bootstrap_servers)
            .set("message.timeout.ms", "5000")
            .create()
            .expect("Producer creation error");

        Self { producer }
    }
}