@echo off
setlocal
echo Stopping local PostgreSQL...

:: Navigate to project root (one level up from scripts folder)
cd /d "%~dp0.."
docker compose -f docker/docker-compose.db.yml down

pause
