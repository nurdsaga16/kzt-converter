#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
rm -rf build && mkdir -p build
javac -d build src/*.java tests/*.java
java -cp build ServerTests
