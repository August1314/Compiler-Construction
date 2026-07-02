#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
LAB3_ROOT="$ROOT/.."

mkdir -p "$ROOT/bin"

# Build exceptions
javac -d "$ROOT/bin" \
  "$ROOT/../ex2/src/exceptions/OberonException.java" \
  "$ROOT/../ex2/src/exceptions/LexicalException.java" \
  "$ROOT/../ex2/src/exceptions/SyntacticException.java" \
  "$ROOT/../ex2/src/exceptions/SemanticException.java" \
  "$ROOT/../ex2/src/exceptions/IllegalSymbolException.java" \
  "$ROOT/../ex2/src/exceptions/IllegalIntegerException.java" \
  "$ROOT/../ex2/src/exceptions/IllegalIntegerRangeException.java" \
  "$ROOT/../ex2/src/exceptions/IllegalOctalException.java" \
  "$ROOT/../ex2/src/exceptions/IllegalIdentifierLengthException.java" \
  "$ROOT/../ex2/src/exceptions/MismatchedCommentException.java" \
  "$ROOT/../ex2/src/exceptions/MissingLeftParenthesisException.java" \
  "$ROOT/../ex2/src/exceptions/MissingRightParenthesisException.java" \
  "$ROOT/../ex2/src/exceptions/MissingOperatorException.java" \
  "$ROOT/../ex2/src/exceptions/MissingOperandException.java" \
  "$ROOT/../ex2/src/exceptions/TypeMismatchedException.java" \
  "$ROOT/../ex2/src/exceptions/ParameterMismatchedException.java"

# Build scanner
javac -d "$ROOT/bin" -classpath "$ROOT/bin" "$ROOT/../ex2/src/OberonScanner.java"

# Build OberonParser
javac -d "$ROOT/bin" -classpath "$ROOT/bin:$LAB3_ROOT/lib/flowchart.jar:$LAB3_ROOT/lib/jgraph.jar" \
  "$ROOT/src/OberonParser.java"

echo "Build complete."
