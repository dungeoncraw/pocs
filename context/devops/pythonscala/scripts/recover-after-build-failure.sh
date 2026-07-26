#!/usr/bin/env bash
set -Eeuo pipefail

source "$(dirname "$0")/common.sh"

require_command kubectl
require_command minikube

cat <<'EOF'
Rebuilding the project images. An existing failed Deployment can remain;
it will recover after the images are built and the Deployment is restarted.
EOF

"$PROJECT_ROOT/scripts/start-minikube.sh"
"$PROJECT_ROOT/scripts/build-images.sh"
"$PROJECT_ROOT/scripts/deploy.sh"
