#!/usr/bin/env bash
set -Eeuo pipefail

source "$(dirname "$0")/common.sh"

require_command minikube

if ! minikube status >/dev/null 2>&1; then
  echo "Minikube is not running. Run ./scripts/start-minikube.sh." >&2
  exit 1
fi

echo "Building the Django image inside Minikube."
minikube image build   --tag django-scala/django:dev   "$PROJECT_ROOT/django-app"

echo "Building the Scala 3 + Supercronic image inside Minikube."
minikube image build   --tag django-scala/scala-cron:dev   "$PROJECT_ROOT/scala-job"

echo "Available images:"
minikube image ls | grep 'django-scala/' || true
