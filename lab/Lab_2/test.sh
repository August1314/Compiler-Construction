#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"

if [ ! -f "$ROOT/bin/parser/Calculator.class" ]; then
  "$ROOT/build.sh"
fi

if [ "${1-}" != "" ]; then
  java -cp "$ROOT/bin" test.ExprEvalTest "$ROOT/$1"
  exit 0
fi

echo "Running simple test suite..."
java -cp "$ROOT/bin" test.ExprEvalTest "$ROOT/testcases/simple.xml"
echo
echo "Running standard test suite..."
java -cp "$ROOT/bin" test.ExprEvalTest "$ROOT/testcases/standard.xml"
