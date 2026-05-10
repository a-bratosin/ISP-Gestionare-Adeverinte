#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR=$(cd "$(dirname "$0")" && pwd)
LIB_DIR="$ROOT_DIR/lib"
SRC_DIR="$ROOT_DIR/src"
# find any test folders named teste_*
TEST_DIRS=()
while IFS= read -r -d '' d; do
  TEST_DIRS+=("$d")
done < <(find "$ROOT_DIR" -maxdepth 1 -type d -name "teste_*" -print0)
BIN_DIR="$ROOT_DIR/bin"

if [ ! -d "$LIB_DIR" ]; then
  echo "Missing lib/ directory. Place junit and hamcrest jars in lib/."
  exit 1
fi

JAR_GLOB=("$LIB_DIR"/*.jar)
if [ ! -e "${JAR_GLOB[0]}" ]; then
  echo "No jars found in lib/. Please download junit-4.13.2.jar and hamcrest-core-1.3.jar into lib/."
  exit 1
fi

mkdir -p "$BIN_DIR"

echo "Compiling source and test classes..."
SRC_FILES=$(find "$SRC_DIR" -name "*.java" 2>/dev/null || true)
TEST_FILES=""
if [ ${#TEST_DIRS[@]} -gt 0 ]; then
  TEST_FILES=$(find "${TEST_DIRS[@]}" -name "*.java" 2>/dev/null || true)
fi

if [ -z "$SRC_FILES" ] && [ -z "$TEST_FILES" ]; then
  echo "No .java files found under src/ or TEST/."
  exit 1
fi

TMP_LIST=$(mktemp)
trap 'rm -f "$TMP_LIST"' EXIT

if [ -n "$SRC_FILES" ]; then
  find "$SRC_DIR" -name "*.java" > "$TMP_LIST"
else
  : > "$TMP_LIST"
fi

if [ -n "$TEST_FILES" ]; then
  find "${TEST_DIRS[@]}" -name "*.java" >> "$TMP_LIST"
fi

javac -cp "$LIB_DIR/*" -d "$BIN_DIR" @"$TMP_LIST"

echo "Discovering test classes..."
if [ ${#TEST_DIRS[@]} -gt 0 ]; then
  mapfile -t TEST_CLASS_FILES < <(find "${TEST_DIRS[@]}" -name "*.java")
else
  TEST_CLASS_FILES=()
fi
TEST_CLASSES=()
for f in "${TEST_CLASS_FILES[@]}"; do
  # assume default package or top-level class name
  TEST_CLASSES+=("$(basename "$f" .java)")
done

if [ ${#TEST_CLASSES[@]} -eq 0 ]; then
  echo "No test classes found in TEST/."
  exit 0
fi

echo "Running JUnit tests: ${TEST_CLASSES[*]}"
java -cp "$BIN_DIR:$LIB_DIR/*" org.junit.runner.JUnitCore "${TEST_CLASSES[@]}"
