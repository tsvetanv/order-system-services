#!/bin/bash
# Run from the root of the project (order-system-services)
cd ..
mvn clean test -pl order-database -am
