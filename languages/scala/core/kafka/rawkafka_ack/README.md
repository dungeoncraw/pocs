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

    sbt "run orders localhost:9092 orders-consumer"

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

## Update Topic Partitions

If you created a topic with one partition and now need to update it to 5 partitions:

    bin/kafka-topics.sh \
      --alter \
      --topic test-topic \
      --bootstrap-server localhost:9092 \
      --partitions 5

To verify the change:

    bin/kafka-topics.sh \
      --describe \
      --topic test-topic \
      --bootstrap-server localhost:9092

## Simulating Multiple Consumers (Consumer Group)

To see how five consumers for the same group receive messages, open five separate terminals and run the consumer in each:

    # Terminal 1 to 5
    sbt "run test-topic localhost:9092 my-group"

When you send messages using the producer, they will be distributed among the consumers. Since there are five partitions and five consumers, each consumer should be assigned to exactly one partition.

If you have more consumers than partitions, some consumers will be idle. If you have fewer consumers than partitions, some consumers will read from multiple partitions.

## Scenario: 5 Partitions, 2 Consumers, 100 Messages

To simulate a scenario with 5 partitions and only 2 consumers:

1.  **Ensure you have 5 partitions** for `test-topic` (see "Update Topic Partitions" above).
2.  **Open two terminals** and run the consumer in each using the same group ID:

    ```bash
    # Terminal 1 and 2
    sbt "run test-topic localhost:9092 group-scenario-2"
    ```

3.  **Send 100 messages** using a bash loop in a third terminal:

    ```bash
    cd ~/kafka_2.13-4.2.0
    for i in {1..100}; do
      echo "message-$i" | bin/kafka-console-producer.sh \
        --topic test-topic \
        --bootstrap-server localhost:9092
    done
    ```

    **Alternatively, send messages in parallel** using the provided script:

    ```bash
    ./send_messages_parallel.sh
    ```

**Observation:**
Since there are 5 partitions and only 2 consumers, Kafka will distribute the 5 partitions between the 2 consumers. Typically, one consumer will handle 3 partitions and the other will handle 2 partitions. You will see both consumers receiving messages from their assigned partitions.

    ## Scenario: Crash Before Offset Commit

    This scenario demonstrates Kafka's **at-least-once delivery** behavior.

    The idea is:

    1. Start the Scala consumer with manual ACK enabled.
    2. Publish many messages.
    3. Kill the consumer while it is still processing messages.
    4. Restart the consumer with the same group ID.
    5. Observe that some messages may be consumed again because their offsets were not committed before the crash.

    ### 1. Start Kafka

    Make sure Kafka is running:

    ```bash
    cd ~/kafka_2.13-4.2.0
    bin/kafka-server-start.sh config/server.properties
    ```

    ### 2. Ensure the topic exists

    If needed, create the topic:

    ```bash
    cd ~/kafka_2.13-4.2.0

    bin/kafka-topics.sh \
      --create \
      --topic test-topic \
      --bootstrap-server localhost:9092 \
      --partitions 5 \
      --replication-factor 1
    ```

    If the topic already exists, you can verify it:

    ```bash
    bin/kafka-topics.sh \
      --describe \
      --topic test-topic \
      --bootstrap-server localhost:9092
    ```

    ### 3. Start the Scala consumer

    From this project directory, start the consumer with a fresh group ID:

    ```bash
    sbt "run test-topic localhost:9092 crash-test-group sync"
    ```

    You can also test async commits:

    ```bash
    sbt "run test-topic localhost:9092 crash-test-group-async async"
    ```

    Keep this terminal open.

    ### 4. Run the crash simulation script

    In another terminal, from this project directory:

    ```bash
    chmod +x simulate_crash_message.sh
    ./simulate_crash_message.sh
    ```

    The script sends many messages and then kills the running consumer while messages are still being processed.

    ### 5. Restart the consumer with the same group ID

    After the consumer is killed, start it again with the same group ID used before:

    ```bash
    sbt "run test-topic localhost:9092 crash-test-group sync"
    ```

    If you tested async mode, restart with the async group ID:

    ```bash
    sbt "run test-topic localhost:9092 crash-test-group-async async"
    ```

    ### Expected result

    You should see some messages consumed again.

    This happens because the consumer processed some records but crashed before committing their offsets.

    In Kafka, a committed offset means:

    > The consumer group has successfully processed messages before this offset.

    If the application crashes before committing, Kafka will deliver those records again after restart.

    This is normal and expected for manual offset commits.

    ### Important notes

    Use the **same consumer group ID** before and after the crash:

    ```bash
    crash-test-group
    ```

    If you change the group ID, Kafka treats it as a new consumer group and the test will not show the same redelivery behavior.

    Also, with manual ACK, the usual behavior is **at-least-once delivery**:

    - A message will not be lost after a crash before commit.
    - A message may be processed more than once.
    - Your processing logic should be idempotent if duplicates are dangerous.

    ## Stop Kafka

    In the terminal where Kafka is running, press:

        Ctrl+C