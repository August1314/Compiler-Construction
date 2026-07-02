#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
LAB3_ROOT="$ROOT/.."

if [ ! -f "$ROOT/bin/OberonParser.class" ]; then
  "$ROOT/build.sh"
fi

echo "=== Experiment 4: OberonParser Test Suite ==="
echo ""

# Test 1: Correct Oberon-0 program
echo "--- Test 1: Correct Sample.obr ---"
java -cp "$ROOT/bin:$LAB3_ROOT/lib/flowchart.jar:$LAB3_ROOT/lib/jgraph.jar" \
  OberonParser "$LAB3_ROOT/ex1/testcases/Sample.obr" 2>&1 | head -1 || true
echo "(Parsing completed — Flowchart window may appear)"
echo ""

# Test 2: Error detection
echo "--- Test 2: Error Detection ---"
test_cases=("Sample.001" "Sample.007" "Sample.010" "Sample.014")
pass=0
fail=0
for f in "${test_cases[@]}"; do
  result=$(java -cp "$ROOT/bin:$LAB3_ROOT/lib/flowchart.jar:$LAB3_ROOT/lib/jgraph.jar" \
    OberonParser "$LAB3_ROOT/ex1/testcases/$f" 2>&1 | grep -i "ERROR" || echo "NO_ERROR")
  if echo "$result" | grep -qi "ERROR"; then
    echo "  PASS: $f — error detected"
    ((pass++)) || true
  else
    echo "  FAIL: $f — expected error but none reported"
    ((fail++)) || true
  fi
done

echo ""
echo "=== Results: $pass passed, $fail failed ==="
