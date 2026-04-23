#!/bin/bash

cd "$(dirname "$0")" || exit 1

echo "=== Cleaning ==="
rm -rf bin
mkdir -p bin

echo "=== Compiling ==="
javac -d bin -cp "lib/*:test-lib/*" src/main/main/*.java src/test/test/*.java

if [ $? -ne 0 ]; then
  echo "Compilation failed."
  exit 1
fi

echo "=== Running Unit Tests ==="
java -jar lib/junit-platform-console-standalone-1.13.0-M3.jar \
  --class-path "bin:lib/gson-2.13.2.jar" \
  --scan-class-path

if [ $? -ne 0 ]; then
  echo "Tests failed."
  exit 1
fi

echo "=== Running App ==="
java -cp "bin:lib/*" main.MainMenu