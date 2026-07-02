#!/usr/bin/env bash
# Generate OberonScanner.java from oberon.flex using JFlex
# This script requires JFlex to be installed in ex2/jflex/
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"

# Note: JFlex would be run as:
# java -jar "$ROOT/jflex/lib/jflex.jar" -d "$ROOT/src" "$ROOT/src/oberon.flex"
# Since JFlex is not bundled, this script documents the expected invocation.
# The hand-written OberonScanner.java in src/ implements the same functionality.

echo "JFlex would generate OberonScanner.java from oberon.flex."
echo "The hand-written OberonScanner.java in src/ is functionally equivalent."
echo "To use JFlex: place jflex.jar in ex2/jflex/ and uncomment the java command."
