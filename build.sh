#!/bin/bash

echo "Building Chess Game..."

# Create build directory if it doesn't exist
mkdir -p build/classes

# Compile all Java files
find src -name "*.java" -print0 | xargs -0 javac -d build/classes

if [ $? -eq 0 ]; then
    echo "Build successful!"
    echo "Running Chess Game..."
    java -cp build/classes main.Main
else
    echo "Build failed!"
    exit 1
fi
