@echo off
setlocal
set PROFILE=%1
if "%PROFILE%"=="" set PROFILE=test

:: Navigate to project root
cd /d "%~dp0.."

echo Running order-database tests with profile: %PROFILE%
call mvn clean test -pl order-database -am -Dspring.profiles.active=%PROFILE%

pause
