#!/usr/bin/env bash
set -euo pipefail

if [ ! -d bin ]; then
  ./build.sh
fi

if [ ! -f lib/junit-4.13.2.jar ] || [ ! -f lib/hamcrest-core-1.3.jar ]; then
  echo "Missing junit jars in lib/. Please download junit-4.13.2.jar and hamcrest-core-1.3.jar."
  exit 1
fi

mkdir -p test-classes
javac -cp "lib/junit-4.13.2.jar:lib/hamcrest-core-1.3.jar:bin" -d test-classes test/PostfixTest.java

echo "Running JUnit tests..."
java -cp "test-classes:bin:lib/junit-4.13.2.jar:lib/hamcrest-core-1.3.jar" org.junit.runner.JUnitCore PostfixTest

echo
echo "Running sample testcases (quiet mode)..."
mkdir -p tmp
java -cp bin Postfix --quiet < testcases/tc-001.infix > tmp/tc-001.out 2>/dev/null
java -cp bin Postfix --quiet < testcases/tc-002.infix > tmp/tc-002.out 2>/dev/null
java -cp bin Postfix --quiet < testcases/tc-003.infix > tmp/tc-003.out 2>/dev/null
java -cp bin Postfix --quiet < testcases/tc-004.infix > tmp/tc-004.out 2>/dev/null

tr -d $'\\r' < testcases/tc-001.postfix > tmp/tc-001.expected
tr -d $'\\r' < testcases/tc-002.postfix > tmp/tc-002.expected
tr -d $'\\r' < testcases/tc-003.postfix > tmp/tc-003.expected
tr -d $'\\r' < testcases/tc-004.postfix > tmp/tc-004.expected

diff -u tmp/tc-001.out tmp/tc-001.expected
diff -u tmp/tc-002.out tmp/tc-002.expected
diff -u tmp/tc-003.out tmp/tc-003.expected
diff -u tmp/tc-004.out tmp/tc-004.expected
