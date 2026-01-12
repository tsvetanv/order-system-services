@echo off
echo Stopping local PostgreSQL...

docker compose -f docker\docker-compose.db.yml down
