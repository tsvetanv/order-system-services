#!/bin/bash
# Description: Runs database tests using Testcontainers
SCRIPT_DIR="$(dirname "$0")"
"$SCRIPT_DIR/run-db.sh" test
