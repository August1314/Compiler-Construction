#!/usr/bin/env bash
set -euo pipefail

mkdir -p bin
javac -d bin src/*.java
