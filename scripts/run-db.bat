@echo off
set PROFILE=%1
if "%PROFILE%"=="" set PROFILE=test
cd ..
call mvn clean test -pl order-database -am -Dspring.profiles.active=%PROFILE%
pause
