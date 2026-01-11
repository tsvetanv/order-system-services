@echo off
REM Run from the root of the project (order-system-services)
cd ..
call mvn clean test -pl order-database -am
pause
