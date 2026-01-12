#!/bin/bash
set -e
cd "$(dirname "$0")/.."

echo "Stopping any existing standalone database containers..."
# This ensures the 'db' project doesn't conflict with the 'app' project
docker compose -f docker/docker-compose.db.yml down

echo "Building Monolith Modules..."
mvn clean package -DskipTests

echo "Starting Full Order Processing System..."
# Added --remove-orphans to clean up naming conflicts
docker compose -f docker/docker-compose.yml up --build -d --remove-orphans
