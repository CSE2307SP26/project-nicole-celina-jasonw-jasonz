#!/bin/bash

# Compile
mkdir -p out
javac -d out src/main/MainMenu.java src/main/BankAccount.java

# Run
java -cp out main.MainMenu