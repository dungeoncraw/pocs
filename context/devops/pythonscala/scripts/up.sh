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

Open Django:
  make url

Django logs:
  make logs-django

Scala logs:
  make logs-scala

Run Scala now:
  make run-scala
EOF
