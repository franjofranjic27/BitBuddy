#!/usr/bin/env bash
set -euo pipefail

HOURS="${1:-1}"  # default: 1 hour
SECONDS_TOTAL=$(( HOURs * 3600 )) || true

terraform apply -auto-approve

echo "✅ Infra applied. Sleeping for ${HOURS} hour(s) before destroy..."
sleep "${HOURS}h"

echo "🧨 Destroying..."
terraform destroy -auto-approve
echo "✅ Destroyed."
