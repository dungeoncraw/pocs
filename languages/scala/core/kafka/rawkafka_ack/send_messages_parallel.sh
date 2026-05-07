#!/bin/bash

# Kafka directory (adjust if necessary)
KAFKA_DIR=~/Downloads/kafka_2.13-4.2.0
TOPIC="test-topic"
BOOTSTRAP_SERVER="localhost:9092"
MSG_COUNT=100

echo "Sending $MSG_COUNT messages in parallel to topic $TOPIC..."

cd "$KAFKA_DIR" || { echo "Kafka directory not found"; exit 1; }

for i in $(seq 1 $MSG_COUNT); do
  (
    echo "parallel-message-$i" | bin/kafka-console-producer.sh \
      --topic "$TOPIC" \
      --bootstrap-server "$BOOTSTRAP_SERVER" > /dev/null 2>&1
  ) &
done

wait
echo "All $MSG_COUNT messages sent."
