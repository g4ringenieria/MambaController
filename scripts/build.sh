#!/bin/bash

echo "Compilando archivos fuente ...";
mkdir ../build;
javac -d ../build ../src/com/neogroup/utils/*.java ../src/com/neogroup/controller/*.java ../src/com/neogroup/controller/processors/*.java 
