#!/bin/bash

# Kill the procces wich is working in 10048 port
# 10048 is a random port I have choosen
fuser -k 10048/tcp

# move to the afs directory
cd afs

# and compile JARS
./compila_y_construye_JARS.sh 

# Copy all the JARS in all the clients and create Cache directory
cd ../cliente1
ln -sf ../afs/afs_* .
mkdir -p Cache

cd ../cliente2
ln -sf ../afs/afs_* .
mkdir -p Cache

cd ../cliente3
ln -sf ../afs/afs_* .
mkdir -p Cache

cd ../cliente4
ln -sf ../afs/afs_* .
mkdir -p Cache

# Also in the server
cd ../servidor
ln -sf ../afs/afs_* .

# Add some files to test
rm -rf AFSDir
mkdir -p AFSDir
cd AFSDir
echo "Prueba 1" >> p1
echo "Prueba 2" >> p2
echo "12345 67890" >> pn
echo "Lineas con
saltos" >> ps

# Compile the server and run it
cd ../../servidor
./compila_servidor.sh
./arranca_rmiregistry 10048 &
./ejecuta_servidor.sh 10048 

