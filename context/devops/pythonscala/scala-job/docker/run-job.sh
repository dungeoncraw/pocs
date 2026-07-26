#!/bin/sh
set -eu

echo "[run-job] Running the Scala CSV update function."

# JAVA_OPTS intentionally expands into multiple JVM arguments.
# shellcheck disable=SC2086
exec java ${JAVA_OPTS:-} -jar /opt/scala-job/app.jar cron
