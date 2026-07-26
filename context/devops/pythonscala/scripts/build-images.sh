#!/usr/bin/env bash
set -Eeuo pipefail

source "$(dirname "$0")/common.sh"

require_command minikube

readonly DJANGO_IMAGE="django-scala/django:dev"
readonly SCALA_IMAGE="django-scala/scala-app:dev"

image_exists() {
  local image="$1"
  minikube image ls | grep --fixed-strings --quiet "$image"
}

build_image() {
  local image="$1"
  local context="$2"
  local description="$3"

  echo "Building $description inside Minikube."

  if ! minikube image build --tag "$image" "$context"; then
    echo "Error: image build command failed for $image." >&2
    exit 1
  fi

  if ! image_exists "$image"; then
    echo "Error: $image is not present in the Minikube image store after the build." >&2
    exit 1
  fi

  echo "Image is available: $image"
}

if ! minikube status >/dev/null 2>&1; then
  echo "Minikube is not running. Run ./scripts/start-minikube.sh." >&2
  exit 1
fi

build_image \
  "$DJANGO_IMAGE" \
  "$PROJECT_ROOT/django-app" \
  "the Django image"

build_image \
  "$SCALA_IMAGE" \
  "$PROJECT_ROOT/scala-job" \
  "the Scala API + scheduler image"

echo "Available project images:"
minikube image ls | grep --fixed-strings 'django-scala/'
