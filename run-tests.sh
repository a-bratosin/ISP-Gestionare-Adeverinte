#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR=$(cd "$(dirname "$0")" && pwd)
LIB_DIR="$ROOT_DIR/lib"
SRC_DIR="$ROOT_DIR/src"
TEST_DIR="$ROOT_DIR/teste_Patrascu"
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
TEST_FILES=$(find "$TEST_DIR" -name "*.java" 2>/dev/null || true)

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
  find "$TEST_DIR" -name "*.java" >> "$TMP_LIST"
fi

javac -cp "$LIB_DIR/*" -d "$BIN_DIR" @"$TMP_LIST"

echo "Discovering test classes..."
mapfile -t TEST_CLASS_FILES < <(find "$TEST_DIR" -name "*.java")
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
