#!/bin/sh
set -eu

mode="${APP_MODE:-server}"

case "$mode" in
  server)
    echo "[entrypoint] Starting the Scala CSV API."
    # JAVA_OPTS intentionally expands into multiple JVM arguments.
    # shellcheck disable=SC2086
    exec java ${JAVA_OPTS:-} -jar /opt/scala-job/app.jar server
    ;;

  scheduler)
    crontab_file="${CRONTAB_FILE:-/etc/supercronic/crontab}"

    if [ ! -r "$crontab_file" ]; then
      echo "[entrypoint] Crontab not found or not readable: $crontab_file" >&2
      exit 1
    fi

    echo "[entrypoint] Validating the crontab."
    supercronic -test "$crontab_file"

    if [ "${RUN_ON_START:-true}" = "true" ]; then
      echo "[entrypoint] Running the initial Scala CSV update."
      /opt/scala-job/run-job.sh || {
        echo "[entrypoint] The initial job failed; the scheduler will remain active." >&2
      }
    fi

    echo "[entrypoint] Starting Supercronic with $crontab_file"
    exec supercronic -split-logs -inotify "$crontab_file"
    ;;

  *)
    echo "[entrypoint] Unknown APP_MODE: $mode. Use server or scheduler." >&2
    exit 2
    ;;
esac
