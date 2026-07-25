#!/bin/sh
set -eu

echo "[run-job] Running the Scala 3 fat JAR."

# JAVA_OPTS is controlled by the environment ConfigMap.
# Intentional expansion allows multiple JVM options.
# shellcheck disable=SC2086
exec java ${JAVA_OPTS:-} -jar /opt/scala-job/app.jar
