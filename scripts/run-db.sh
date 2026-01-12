#!/bin/bash
# Description: Core database test runner supporting profiles
PROFILE=${1:-test}
# Navigate to project root relative to script location
cd "$(dirname "$0")/.."
echo "Running order-database tests with profile: $PROFILE"
mvn clean test -pl order-database -am -Dspring.profiles.active=$PROFILE
