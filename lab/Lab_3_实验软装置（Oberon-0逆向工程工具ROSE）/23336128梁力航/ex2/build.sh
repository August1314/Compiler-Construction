#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"

# Build exceptions
mkdir -p "$ROOT/bin"
javac -d "$ROOT/bin" -sourcepath "$ROOT/src" "$ROOT/src/exceptions/"*.java

# Build scanner
javac -d "$ROOT/bin" -classpath "$ROOT/bin" -sourcepath "$ROOT/src" "$ROOT/src/OberonScanner.java"

echo "Build complete."
