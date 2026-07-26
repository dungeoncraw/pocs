#!/usr/bin/env bash
set -Eeuo pipefail

source "$(dirname "$0")/common.sh"

require_command minikube

cat <<'EOF'
The Service exposes two ports:
  Django API: service port 80 / NodePort 30080
  Scala API:  service port 9000 / NodePort 30090

Depending on the Minikube driver, this command may keep a tunnel open.
The URLs are printed in the same order as the Service ports.
EOF

minikube service django-scala \
  --namespace "$NAMESPACE" \
  --url
