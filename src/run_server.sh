#!/bin/bash

# Kill the procces wich is working in 10048 port
# 10048 is a random port I have choosen
fuser -k 10048/tcp

# move to the afs directory
cd afs

# and compile JARS
./compila_y_construye_JARS.sh 

# Copy all the JARS in all the clients
cp afs_* ../cliente1/ 
cp afs_* ../cliente2/
cp afs_* ../cliente3/
cp afs_* ../cliente4/

# Also in the server
cp afs_* ../servidor/

# Also in src in case it is needed
cp afs_* ../

# Compile the server and run it
cd ../servidor
./compila_servidor.sh
./arranca_rmiregistry 10048 &
./ejecuta_servidor.sh 10048 



