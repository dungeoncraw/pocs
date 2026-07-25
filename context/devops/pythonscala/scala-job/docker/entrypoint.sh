#!/bin/sh
set -eu

CRONTAB_FILE="${CRONTAB_FILE:-/etc/supercronic/crontab}"
READY_URL="${DJANGO_BASE_URL:-http://127.0.0.1:8000}/health/ready/"

if [ ! -r "$CRONTAB_FILE" ]; then
  echo "[entrypoint] Crontab not found or not readable: $CRONTAB_FILE" >&2
  exit 1
fi

echo "[entrypoint] Validating the crontab."
supercronic -test "$CRONTAB_FILE"

if [ "${RUN_ON_START:-true}" = "true" ]; then
  echo "[entrypoint] Waiting for Django at $READY_URL"

  attempt=1
  while [ "$attempt" -le 60 ]; do
    if curl --fail --silent --show-error --max-time 3 "$READY_URL" >/dev/null; then
      echo "[entrypoint] Django is ready. Running the initial job."
      /opt/scala-job/run-job.sh || {
        echo "[entrypoint] The initial job failed; the scheduler will remain active." >&2
      }
      break
    fi

    echo "[entrypoint] Django is not ready yet; attempt $attempt/60."
    attempt=$((attempt + 1))
    sleep 2
  done

  if [ "$attempt" -gt 60 ]; then
    echo "[entrypoint] Django did not become ready within the wait period; starting only the scheduler." >&2
  fi
fi

echo "[entrypoint] Starting Supercronic with $CRONTAB_FILE"
exec supercronic -split-logs -inotify "$CRONTAB_FILE"
