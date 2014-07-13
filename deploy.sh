#!/bin/bash

echo "Creando archivo jar ...";
mkdir ./dist;
jar cfe ./dist/NeoGroupController.jar com.neogroup.controller.Application -C ./build/ .
