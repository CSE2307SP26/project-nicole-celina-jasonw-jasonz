#!/bin/bash

cd "$(dirname "$0")"

# Compile
echo "=== Cleaning ==="
rm -rf out
mkdir -p out

echo "=== Compiling ==="
javac -d out -cp "lib/*:test-lib/*" src/main/*.java src/test/*.java

if [ $? -ne 0 ]; then
  echo "Compilation failed."
  exit 1
fi

# Run unit tests
echo "=== Running Unit Tests ==="
java -jar lib/junit-platform-console-standalone-*.jar \
  -cp out --scan-classpath

# Run the app
echo "=== Running App ==="
java -cp out main.MainMenu