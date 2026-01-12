@echo off
setlocal
cd /d "%~dp0.."

echo Stopping Full Order Processing System and all related containers...

:: Stop both potential configurations to release shared networks
call docker compose -f docker/docker-compose.yml down --remove-orphans
call docker compose -f docker/docker-compose.db.yml down --remove-orphans

echo Cleanup complete.
pause
