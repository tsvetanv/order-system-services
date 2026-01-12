@echo off
echo Starting local PostgreSQL for Order Processing System...

docker compose -f docker\docker-compose.db.yml up -d
