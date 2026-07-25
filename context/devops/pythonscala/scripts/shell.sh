#!/usr/bin/env bash
set -Eeuo pipefail

source "$(dirname "$0")/common.sh"

require_command kubectl

container="${1:-}"

case "$container" in
  django|scala-cron)
    ;;
  *)
    echo "Usage: $0 django|scala-cron" >&2
    exit 1
    ;;
esac

pod="$(current_pod)"

if [[ -z "$pod" ]]; then
  echo "No running Pod was found." >&2
  exit 1
fi

kubectl exec   --namespace "$NAMESPACE"   --stdin   --tty   "$pod"   --container "$container"   -- /bin/sh
