#!/usr/bin/env bash
set -Eeuo pipefail

source "$(dirname "$0")/common.sh"

require_command kubectl

pod="$(current_pod)"

if [[ -z "$pod" ]]; then
  echo "No running Pod was found." >&2
  exit 1
fi

echo "Running the Scala job in Pod: $pod"
kubectl exec   --namespace "$NAMESPACE"   "$pod"   --container scala-cron   -- /opt/scala-job/run-job.sh
