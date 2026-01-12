#!/bin/bash
# Description: Runs database tests against Rancher Desktop instance
SCRIPT_DIR="$(dirname "$0")"
"$SCRIPT_DIR/run-db.sh" local
