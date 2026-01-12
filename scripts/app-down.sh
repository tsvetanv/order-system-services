#!/bin/bash
set -e
cd "$(dirname "$0")/.."

echo "Stopping Full Order Processing System and all related containers..."

# 1. Stop full app stack first
docker compose -f docker/docker-compose.yml down --remove-orphans

# 2. Safety check: Also stop the standalone DB configuration
# This ensures the 'docker_default' network is fully released
docker compose -f docker/docker-compose.db.yml down --remove-orphans

echo "Cleanup complete."
