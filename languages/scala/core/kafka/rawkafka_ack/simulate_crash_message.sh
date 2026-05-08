#!/bin/bash

set -euo pipefail

KAFKA_DIR="${KAFKA_DIR:-$HOME/Downloads/kafka_2.13-4.2.0}"
TOPIC="${TOPIC:-test-topic}"
BOOTSTRAP_SERVER="${BOOTSTRAP_SERVER:-localhost:9092}"

TOTAL_MESSAGES="${TOTAL_MESSAGES:-100}"
CRASH_AT="${CRASH_AT:-50}"

NORMAL_PREFIX="${NORMAL_PREFIX:-message}"
CRASH_MESSAGE="${CRASH_MESSAGE:-crash-before-commit}"

echo "Kafka dir:         $KAFKA_DIR"
echo "Topic:             $TOPIC"
echo "Bootstrap server:  $BOOTSTRAP_SERVER"
echo "Total messages:    $TOTAL_MESSAGES"
echo "Crash at message:  $CRASH_AT"
echo "Crash message:     $CRASH_MESSAGE"
echo

if [ ! -d "$KAFKA_DIR" ]; then
  echo "Kafka directory not found: $KAFKA_DIR"
  exit 1
fi

cd "$KAFKA_DIR"

if [ "$CRASH_AT" -lt 1 ] || [ "$CRASH_AT" -gt "$TOTAL_MESSAGES" ]; then
  echo "CRASH_AT must be between 1 and TOTAL_MESSAGES"
  exit 1
fi

send_message() {
  local message="$1"

  echo "$message" | bin/kafka-console-producer.sh \
    --topic "$TOPIC" \
    --bootstrap-server "$BOOTSTRAP_SERVER" > /dev/null
}

echo "Sending messages..."

for i in $(seq 1 "$TOTAL_MESSAGES"); do
  if [ "$i" -eq "$CRASH_AT" ]; then
    message="$CRASH_MESSAGE"
    echo "[$i/$TOTAL_MESSAGES] Sending CRASH message: $message"
  else
    message="$NORMAL_PREFIX-$i"
    echo "[$i/$TOTAL_MESSAGES] Sending normal message: $message"
  fi

  send_message "$message"
done

echo
echo "Done."
echo
echo "Now run or restart your Scala consumer with the SAME consumer group id."
echo
echo "Example:"
echo "  sbt \"run $TOPIC $BOOTSTRAP_SERVER crash-test-group sync\""
echo
echo "Expected behavior:"
echo "  1. Consumer processes messages before '$CRASH_MESSAGE'."
echo "  2. Consumer receives '$CRASH_MESSAGE'."
echo "  3. Your Scala code should call Runtime.getRuntime.halt(1) before committing."
echo "  4. Restart the consumer with the same group id."
echo "  5. Kafka redelivers from the last committed offset."