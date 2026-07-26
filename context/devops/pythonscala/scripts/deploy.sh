#!/usr/bin/env bash
set -Eeuo pipefail

source "$(dirname "$0")/common.sh"

require_command kubectl
require_command minikube

readonly DJANGO_IMAGE="django-scala/django:dev"
readonly SCALA_IMAGE="django-scala/scala-app:dev"

require_local_image() {
  local image="$1"

  if ! minikube image ls | grep --fixed-strings --quiet "$image"; then
    echo "Error: required Minikube image is missing: $image" >&2
    echo "Run make build before deploying." >&2
    exit 1
  fi
}

echo "Refreshing the Minikube kubeconfig endpoint."
minikube update-context
kubectl config use-context minikube >/dev/null

if ! kubectl \
  --context minikube \
  --request-timeout=5s \
  get --raw='/readyz' >/dev/null 2>&1; then
  echo "Error: the Kubernetes API server is not ready." >&2
  echo "Run ./scripts/start-minikube.sh and try again." >&2
  exit 1
fi

require_local_image "$DJANGO_IMAGE"
require_local_image "$SCALA_IMAGE"

echo "Applying Kubernetes manifests."
kubectl \
  --context minikube \
  apply \
  --filename "$PROJECT_ROOT/kubernetes"

echo "Restarting the Deployment to use updated ConfigMaps and rebuilt images."
kubectl \
  --context minikube \
  rollout restart \
  "deployment/$DEPLOYMENT" \
  --namespace "$NAMESPACE"

echo "Waiting for the rollout."

if ! kubectl \
  --context minikube \
  rollout status \
  "deployment/$DEPLOYMENT" \
  --namespace "$NAMESPACE" \
  --timeout=300s; then
  echo >&2
  echo "Rollout failed. Current Pod status:" >&2
  kubectl get pods --namespace "$NAMESPACE" --output wide >&2 || true

  echo >&2
  echo "Recent Pod events:" >&2
  kubectl get events \
    --namespace "$NAMESPACE" \
    --sort-by='.lastTimestamp' \
    | tail -n 40 >&2 || true

  echo >&2
  echo "Container states:" >&2
  kubectl get pods \
    --namespace "$NAMESPACE" \
    --selector app.kubernetes.io/name=django-scala \
    --output jsonpath='{range .items[*]}Pod: {.metadata.name}{"\n"}{range .status.containerStatuses[*]}  {.name}: waiting={.state.waiting.reason} terminated={.state.terminated.reason} ready={.ready}{"\n"}{end}{end}' >&2 || true
  echo >&2

  exit 1
fi

kubectl \
  --context minikube \
  get pods,pvc,service \
  --namespace "$NAMESPACE" \
  --output wide
