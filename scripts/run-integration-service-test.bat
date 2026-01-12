@echo off
setlocal
echo Running integration-service tests...

:: Navigate to project root
cd /d "%~dp0.."
call mvn clean test -pl integration-service -am -Dspring.profiles.active=test

pause
