#!/usr/bin/env bash
set -euo pipefail

mkdir -p doc
javadoc -d doc -private src/*.java
