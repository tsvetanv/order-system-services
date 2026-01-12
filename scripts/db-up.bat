@echo off
setlocal
echo Starting local PostgreSQL for Order Processing System via Rancher Desktop...

:: Reset Docker context
call docker context use default >nul 2>&1

:: Point to Rancher Desktop's default socket location
set DOCKER_HOST=npipe:////./pipe/docker_engine

:: Navigate to project root
cd /d "%~dp0.."
docker compose -f docker/docker-compose.db.yml up -d

pause
