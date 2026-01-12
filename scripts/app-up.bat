@echo off
setlocal
cd /d "%~dp0.."

echo Stopping any existing standalone database containers...
call docker compose -f docker/docker-compose.db.yml down

echo Building Monolith Modules...
call mvn clean package -DskipTests

echo Starting Full Order Processing System...
call docker compose -f docker/docker-compose.yml up --build -d --remove-orphans
pause
