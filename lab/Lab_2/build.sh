#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"

mkdir -p "$ROOT/bin"
rm -f "$ROOT"/bin/parser/*.class
javac -d "$ROOT/bin" -classpath "$ROOT/bin" "$ROOT"/src/parser/*.java
