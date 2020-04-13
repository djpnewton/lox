#!/bin/bash

set -e

echo :: generate ast..
python generate_ast.py .
echo :: compile..
kotlinc *.kt -include-runtime -d lox.jar
echo :: run..
java -jar lox.jar $1
