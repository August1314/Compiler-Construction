#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"

mkdir -p "$ROOT/doc"
javadoc -private -author -version -d "$ROOT/doc" -classpath "$ROOT/bin" "$ROOT"/src/parser/*.java
