# Local Kafka Setup

This guide shows how to run Kafka locally in KRaft standalone mode and test it with a topic, producer, and consumer.

## Kafka Directory

Kafka is installed/extracted at:

    ~/kafka_2.13-4.2.0

Go to the Kafka directory:

    cd ~/kafka_2.13-4.2.0

## Format Kafka Storage

Generate a Kafka cluster ID:

    KAFKA_CLUSTER_ID="$(bin/kafka-storage.sh random-uuid)"

Format storage in standalone mode:

    bin/kafka-storage.sh format \
      --standalone \
      -t "$KAFKA_CLUSTER_ID" \
      -c config/server.properties

If Kafka was already formatted and you need to reset local data, check the log directory:

    grep log.dirs config/server.properties

Then remove the local Kafka logs if needed, for example:

    rm -rf /tmp/kraft-combined-logs

Then run the format command again.

## Start Kafka

    bin/kafka-server-start.sh config/server.properties

Keep this terminal open while using Kafka.

## Create a Topic

Open a new terminal:

    cd ~/kafka_2.13-4.2.0

Create a topic named `test-topic`:

    bin/kafka-topics.sh \
      --create \
      --topic test-topic \
      --bootstrap-server localhost:9092 \
      --partitions 1 \
      --replication-factor 1

List topics:

    bin/kafka-topics.sh \
      --list \
      --bootstrap-server localhost:9092

## Run a Consumer

    bin/kafka-console-consumer.sh \
      --topic test-topic \
      --bootstrap-server localhost:9092 \
      --from-beginning

Run the Scala consumer from this project with sbt:

    sbt "runMain KafkaConsumer test-topic localhost:9092 scala-consumer"

## Run a Producer

Open another terminal:

    cd ~/kafka_2.13-4.2.0

Start the producer:

    bin/kafka-console-producer.sh \
      --topic test-topic \
      --bootstrap-server localhost:9092

Type a message and press Enter:

    hello kafka

The message should appear in the consumer terminal.

## Stop Kafka

In the terminal where Kafka is running, press:

    Ctrl+C
