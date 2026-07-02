#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"

if [ ! -f "$ROOT/bin/OberonScanner.class" ]; then
  "$ROOT/build.sh"
fi

if [ "${1-}" = "" ]; then
  echo "Usage: ./run.sh <source-file>"
  exit 1
fi

java -cp "$ROOT/bin" oberon.OberonScanner "$1"
