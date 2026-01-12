#!/usr/bin/env bash
set -e

echo "Stopping local PostgreSQL..."

docker compose -f docker/docker-compose.db.yml down
