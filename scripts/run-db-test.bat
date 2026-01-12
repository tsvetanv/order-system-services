@echo off
setlocal
echo Launching Testcontainers database tests...
call "%~dp0run-db.bat" test
