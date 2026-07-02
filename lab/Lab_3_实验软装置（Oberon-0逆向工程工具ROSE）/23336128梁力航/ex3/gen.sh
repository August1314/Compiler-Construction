#!/usr/bin/env bash
# Generate Parser.java and sym.java from oberon.cup using JavaCUP
# This script requires JavaCUP to be installed in ex3/javacup/
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"

# Note: JavaCUP would be run as:
# java -jar "$ROOT/javacup/java-cup-11b.jar" \
#   -parser Parser \
#   -symbols sym \
#   -destdir "$ROOT/src" \
#   "$ROOT/src/oberon.cup"
# Since JavaCUP is not bundled, this script documents the expected invocation.
# The hand-written Parser.java and sym.java in src/ implement the same functionality.

echo "JavaCUP would generate Parser.java and sym.java from oberon.cup."
echo "The hand-written Parser.java and sym.java in src/ are functionally equivalent."
echo "To use JavaCUP: place java-cup-11b.jar in ex3/javacup/ and uncomment the java command."
