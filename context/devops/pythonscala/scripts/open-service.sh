#!/usr/bin/env bash
set -Eeuo pipefail

source "$(dirname "$0")/common.sh"

require_command minikube

echo "Opening the Django Service."
echo "With some drivers, this command keeps a tunnel open; keep the terminal running."
minikube service django   --namespace "$NAMESPACE"   --url
