#!/bin/bash
cd "$(dirname "$0")/.."
mvn clean test -pl integration-service -am -Dspring.profiles.active=test
