#!/usr/bin/env bash
set -Eeuo pipefail

source "$(dirname "$0")/common.sh"

require_command minikube
require_command kubectl

driver="${MINIKUBE_DRIVER:-docker}"
cpus="${MINIKUBE_CPUS:-4}"
memory="${MINIKUBE_MEMORY:-6144}"

start_cluster() {
  echo "Starting Minikube with driver=$driver, cpus=$cpus, memory=${memory}MB."
  minikube start \
    --driver "$driver" \
    --cpus "$cpus" \
    --memory "$memory"
}

refresh_context() {
  echo "Refreshing the Minikube kubeconfig endpoint."
  minikube update-context
  kubectl config use-context minikube >/dev/null
}

wait_for_api() {
  local attempts="${1:-30}"
  local delay_seconds="${2:-2}"
  local attempt

  for ((attempt = 1; attempt <= attempts; attempt++)); do
    if kubectl \
      --context minikube \
      --request-timeout=5s \
      get --raw='/readyz' >/dev/null 2>&1; then
      echo "Kubernetes API server is ready."
      return 0
    fi

    echo "Waiting for the Kubernetes API server ($attempt/$attempts)."
    sleep "$delay_seconds"
  done

  return 1
}

if minikube status >/dev/null 2>&1; then
  echo "Minikube reports that the cluster is running."
else
  start_cluster
fi

# Docker Desktop or Minikube restarts may change the API-server port.
# update-context repairs the address stored in ~/.kube/config.
refresh_context

if ! wait_for_api 15 2; then
  echo "The API server is still unreachable. Restarting the Minikube profile."
  minikube stop || true
  start_cluster
  refresh_context

  if ! wait_for_api 30 2; then
    cat >&2 <<'MSG'

Error: the Minikube Kubernetes API server is not reachable.

Inspect the cluster with:
  minikube status
  minikube logs --problems
  kubectl config view --minify

As a last resort, recreate the local cluster:
  minikube delete
  make up

Warning: deleting Minikube removes workloads and data stored in that local cluster.
MSG
    exit 1
  fi
fi

echo "Current kubectl context: $(kubectl config current-context)"
kubectl --context minikube get nodes --output wide
