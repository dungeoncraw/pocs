# MemKafka

A Rust-based event processor using Axum and Apache Kafka.

## Overview

This project implements a simple HTTP API that receives events and produces them to an Apache Kafka topic. It is designed to be highly performant and easy to deploy using Docker.

## Project Structure

- `src/main.rs`: Entry point of the application.
- `src/app.rs`: Axum application builder.
- `src/state.rs`: Shared application state, including the Kafka producer.
- `src/handlers/event.rs`: Logic for handling incoming events and sending them to Kafka.
- `src/models/event.rs`: Data structures for events.
- `src/routes/`: API route definitions.
- `Dockerfile`: Multi-stage build for a lightweight production image.
- `docker-compose.yaml`: Orchestration for the App and Kafka broker.

## How it Works

1. **Kafka Producer**: The application uses `rdkafka` with a `FutureProducer` stored in the `AppState`. It initializes using the `KAFKA_BOOTSTRAP_SERVERS` environment variable.
2. **Event Endpoint**: A POST request to `/events` accepts a JSON payload.
3. **Asynchronous Processing**: The handler serializes the JSON and sends it to the `events` topic asynchronously. The `sub_id` is used as the message key to ensure order within partitions for the same subscriber.

## How to Run

### Prerequisites
- Docker and Docker Compose installed.

### Start the Stack
From the project root, run:
```bash
docker-compose up --build
```
This command builds the Rust application and starts both the Kafka broker and the app.

### Test the API
You can send a test event using `curl`:

```bash
curl -X POST http://localhost:3000/events \
  -H "Content-Type: application/json" \
  -d '{
    "sub_id": "user_123",
    "name": "login_event",
    "description": "User logged into the system",
    "date": "2024-04-18T20:00:00Z"
  }'
```

### Verify Kafka Messages
To check if the message was successfully produced to Kafka, run:
```bash
docker exec -it broker kafka-console-consumer --bootstrap-server localhost:9092 --topic events --from-beginning
```

## Local Development
If you prefer to run the app locally (outside Docker):
1. Ensure a Kafka broker is running at `localhost:9092`.
2. Run `cargo run`.

The server will be available at `http://127.0.0.1:3000`.
