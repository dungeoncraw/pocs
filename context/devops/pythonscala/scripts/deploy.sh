#!/usr/bin/env bash
set -Eeuo pipefail

source "$(dirname "$0")/common.sh"

require_command kubectl
require_command minikube

kubectl config use-context minikube >/dev/null

echo "Applying Kubernetes manifests."
kubectl apply --filename "$PROJECT_ROOT/kubernetes"

echo "Restarting the Deployment to use updated ConfigMaps and rebuilt images."
kubectl rollout restart   "deployment/$DEPLOYMENT"   --namespace "$NAMESPACE"

echo "Waiting for the rollout."
kubectl rollout status   "deployment/$DEPLOYMENT"   --namespace "$NAMESPACE"   --timeout=240s

kubectl get pods,service   --namespace "$NAMESPACE"   --output wide
