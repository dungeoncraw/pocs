#!/usr/bin/env bash
set -Eeuo pipefail

readonly PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
readonly NAMESPACE="django-scala-local"
readonly DEPLOYMENT="django-scala"

require_command() {
  local command_name="$1"

  if ! command -v "$command_name" >/dev/null 2>&1; then
    echo "Erro: comando obrigatório não encontrado: $command_name" >&2
    exit 1
  fi
}

current_pod() {
  kubectl get pods     --namespace "$NAMESPACE"     --selector app.kubernetes.io/name=django-scala     --field-selector status.phase=Running     --output jsonpath='{.items[0].metadata.name}'
}
