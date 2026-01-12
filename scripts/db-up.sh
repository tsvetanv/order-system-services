#!/usr/bin/env bash
set -e

echo "Starting local PostgreSQL for Order Processing System via Rancher Desktop..."

# Reset Docker context to ensure it doesn't look for Docker Desktop pipes
docker context use default || true

# Point to Rancher Desktop's default socket location as a fallback
export DOCKER_HOST="npipe:////./pipe/docker_engine"

# Run from the project root
cd "$(dirname "$0")/.."
docker compose -f docker/docker-compose.db.yml up -d
