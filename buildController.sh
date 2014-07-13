#!/bin/bash

echo "Borrando carpeta de distribución ...";
rm -rf ./dist2;

echo "Creando carpeta de distribución ...";
mkdir ./dist2;

echo "Creando carpeta temporal de compilación ...";
mkdir ./tmp;

echo "Compilando archivos fuente ...";
javac -d ./tmp src/com/neogroup/utils/*.java src/com/neogroup/controller/*.java src/com/neogroup/controller/processors/*.java 

echo "Creando archivo jar ...";
jar cfe ./dist2/NeoGroupController.jar com.neogroup.controller.Application -C tmp/ .

echo "Borrando carpeta temporal de compilación ...";
rm -rf ./tmp;
