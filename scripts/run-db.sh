#!/bin/bash
PROFILE=${1:-test}
cd ..
mvn clean test -pl order-database -am -Dspring.profiles.active=$PROFILE
