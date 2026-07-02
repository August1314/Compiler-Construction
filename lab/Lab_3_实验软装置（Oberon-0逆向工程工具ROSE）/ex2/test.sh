#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"

if [ ! -f "$ROOT/bin/OberonScanner.class" ]; then
  "$ROOT/build.sh"
fi

echo "=== Testing correct Oberon-0 source ==="
java -cp "$ROOT/bin" oberon.OberonScanner "$ROOT/../ex1/testcases/Sample.obr" 2>&1 || true

echo ""
echo "=== Testing mutation programs (errors expected) ==="
for f in "$ROOT/../ex1/testcases"/Sample.0*; do
  echo "--- $(basename "$f") ---"
  java -cp "$ROOT/bin" oberon.OberonScanner "$f" 2>&1 || true
  echo ""
done
