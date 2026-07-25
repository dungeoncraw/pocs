#!/usr/bin/env bash
set -Eeuo pipefail

source "$(dirname "$0")/common.sh"

require_command kubectl

echo "Pods:"
kubectl get pods --namespace "$NAMESPACE" --output wide

echo
echo "Deployment:"
kubectl get deployment "$DEPLOYMENT" --namespace "$NAMESPACE"

echo
echo "Service:"
kubectl get service django --namespace "$NAMESPACE"

echo
echo "Pod containers:"
pod="$(current_pod)"
kubectl get pod "$pod"   --namespace "$NAMESPACE"   --output jsonpath='{range .spec.containers[*]}{.name}{" -> "}{.image}{"\n"}{end}'
