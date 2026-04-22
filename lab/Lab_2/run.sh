#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"

if [ ! -f "$ROOT/bin/parser/Calculator.class" ]; then
  "$ROOT/build.sh"
fi

(cd "$ROOT/bin" && java ExprEval "$@")
