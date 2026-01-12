#!/bin/bash
# Description: Runs integration-service context tests
cd "$(dirname "$0")/.."
echo "Running integration-service tests..."
mvn clean test -pl integration-service -am -Dspring.profiles.active=test
