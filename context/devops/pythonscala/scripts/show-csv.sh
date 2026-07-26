#!/usr/bin/env bash
set -Eeuo pipefail

source "$(dirname "$0")/common.sh"

require_command kubectl

pod="$(current_pod)"

if [[ -z "$pod" ]]; then
  echo "No running Pod was found." >&2
  exit 1
fi

kubectl exec \
  --namespace "$NAMESPACE" \
  "$pod" \
  --container django \
  -- sh -c 'if [ -f "${CSV_PATH:-/data/records.csv}" ]; then cat "${CSV_PATH:-/data/records.csv}"; else echo "CSV file does not exist yet."; fi'
