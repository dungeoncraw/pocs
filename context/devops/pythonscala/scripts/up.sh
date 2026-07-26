#!/usr/bin/env bash
set -Eeuo pipefail

script_dir="$(cd "$(dirname "$0")" && pwd)"

"$script_dir/start-minikube.sh"
"$script_dir/build-images.sh"
"$script_dir/deploy.sh"

cat <<'EOF'

Application is ready.

Check status:
  make status

Display API URLs:
  make url

Test both implementations against the shared CSV:
  make test-apis

Display the CSV:
  make show-csv

Django logs:
  make logs-django

Scala API logs:
  make logs-scala-api

Scala cron logs:
  make logs-scala-cron

Run the Scala update now:
  make run-scala
EOF
