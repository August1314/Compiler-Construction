#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
LAB3_ROOT="$ROOT/.."

if [ ! -f "$ROOT/bin/OberonParser.class" ]; then
  "$ROOT/build.sh"
fi

if [ "${1-}" = "" ]; then
  echo "Usage: ./run.sh <source-file>"
  exit 1
fi

java -cp "$ROOT/bin:$LAB3_ROOT/lib/flowchart.jar:$LAB3_ROOT/lib/jgraph.jar" \
  OberonParser "$1"
