#!/bin/bash

# Clean and compile (ensures fresh build, no stale .class files)
rm -rf out
mkdir -p out
javac -d out src/main/MainMenu.java src/main/BankAccount.java

# Run
java -cp out main.MainMenu