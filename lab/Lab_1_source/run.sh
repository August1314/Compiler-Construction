#!/usr/bin/env bash
set -euo pipefail

if [ ! -d bin ]; then
  ./build.sh
fi

java -cp bin Postfix "$@"
