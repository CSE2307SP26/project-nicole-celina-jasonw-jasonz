#!/bin/bash

cd "$(dirname "$0")"

rm -rf out
mkdir out

javac -d out -cp "lib/*" src/main/*.java src/test/*.java
java -jar lib/junit-platform-console-standalone-1.13.0-M3.jar -cp out --scan-classpath