#!/usr/bin/env bash
set -Eeuo pipefail

source "$(dirname "$0")/common.sh"

require_command kubectl
require_command curl

log_file="$(mktemp)"
port_forward_pid=""

cleanup() {
  if [[ -n "$port_forward_pid" ]]; then
    kill "$port_forward_pid" >/dev/null 2>&1 || true
    wait "$port_forward_pid" >/dev/null 2>&1 || true
  fi
  rm -f "$log_file"
}
trap cleanup EXIT

echo "Starting port-forward for both APIs."
kubectl port-forward \
  --namespace "$NAMESPACE" \
  service/django-scala \
  18080:80 \
  19000:9000 \
  >"$log_file" 2>&1 &
port_forward_pid="$!"

for attempt in {1..30}; do
  if curl --fail --silent http://127.0.0.1:18080/health/ready/ >/dev/null \
    && curl --fail --silent http://127.0.0.1:19000/health/ready >/dev/null; then
    break
  fi

  if ! kill -0 "$port_forward_pid" >/dev/null 2>&1; then
    cat "$log_file" >&2
    exit 1
  fi

  if [[ "$attempt" -eq 30 ]]; then
    echo "The APIs did not become ready." >&2
    cat "$log_file" >&2
    exit 1
  fi

  sleep 1
done

echo
echo "1. Create a record through Django/Python:"
curl --fail-with-body --silent --show-error \
  --request POST \
  --header 'Content-Type: application/json' \
  --data '{"id":"shared-demo","name":"Created by Django","value":"python value"}' \
  http://127.0.0.1:18080/api/csv/upsert/
echo

echo
echo "2. Read the same CSV through Scala:"
curl --fail-with-body --silent --show-error \
  http://127.0.0.1:19000/api/csv/records
echo

echo
echo "3. Update the same record through Scala:"
curl --fail-with-body --silent --show-error \
  --request POST \
  --header 'Content-Type: application/json' \
  --data '{"id":"shared-demo","name":"Updated by Scala","value":"scala value"}' \
  http://127.0.0.1:19000/api/csv/upsert
echo

echo
echo "4. Read the updated CSV through Django/Python:"
curl --fail-with-body --silent --show-error \
  http://127.0.0.1:18080/api/csv/records/
echo
