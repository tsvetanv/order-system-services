@echo off
cd %~dp0\..
call mvn clean test -pl integration-service -am -Dspring.profiles.active=test
pause
