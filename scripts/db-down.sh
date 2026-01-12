#!/usr/bin/env bash
set -e
# Description: Shuts down local Postgres
echo "Stopping local PostgreSQL..."
cd "$(dirname "$0")/.."
docker compose -f docker/docker-compose.db.yml down
