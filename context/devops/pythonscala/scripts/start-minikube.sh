#!/usr/bin/env bash
set -Eeuo pipefail

source "$(dirname "$0")/common.sh"

require_command minikube
require_command kubectl

if minikube status >/dev/null 2>&1; then
  echo "Minikube is already running."
else
  driver="${MINIKUBE_DRIVER:-docker}"
  cpus="${MINIKUBE_CPUS:-4}"
  memory="${MINIKUBE_MEMORY:-6144}"

  echo "Starting Minikube with driver=$driver, cpus=$cpus, memory=${memory}MB."
  minikube start     --driver "$driver"     --cpus "$cpus"     --memory "$memory"
fi

kubectl config use-context minikube >/dev/null
echo "Current kubectl context: $(kubectl config current-context)"
