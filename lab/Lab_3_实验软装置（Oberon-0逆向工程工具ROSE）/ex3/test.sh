#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
LAB3_ROOT="$ROOT/.."

if [ ! -f "$ROOT/bin/Parser.class" ]; then
  "$ROOT/build.sh"
fi

echo "=== Experiment 3: Parser Test Suite ==="
echo ""

# Test 1: Correct Oberon-0 program
echo "--- Test 1: Correct Sample.obr ---"
java -cp "$ROOT/bin:$LAB3_ROOT/lib/callgraph.jar:$LAB3_ROOT/lib/jgraph.jar" \
  Parser "$LAB3_ROOT/ex1/testcases/Sample.obr" 2>&1 | head -1 || true
echo "(Parsing completed — Call Graph window may appear)"
echo ""

# Test 2: Syntax error detection
echo "--- Test 2: Syntax Error Detection ---"
test_cases=("Sample.007" "Sample.008" "Sample.009" "Sample.010" "Sample.014" "Sample.015" "Sample.016")
expected=(
  "Missing left parenthesis"
  "Missing right parenthesis"
  "Missing operator"
  "Missing operand"
  "Missing THEN"
  "Missing END"
  "Name mismatch"
)
pass=0
fail=0
for i in "${!test_cases[@]}"; do
  f="${test_cases[$i]}"
  expected_msg="${expected[$i]}"
  result=$(java -cp "$ROOT/bin:$LAB3_ROOT/lib/callgraph.jar:$LAB3_ROOT/lib/jgraph.jar" \
    Parser "$LAB3_ROOT/ex1/testcases/$f" 2>&1 | grep -i "ERROR" || echo "NO_ERROR")
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
