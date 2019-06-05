#!/bin/bash

# Move to one client (cliente1)
cd cliente1

# Compile the test
./compila_test.sh

# Export all needed enviroment variables
export REGISTRY_HOST=localhost
export REGISTRY_PORT=10048
export BLOCKSIZE=5 # You can chose this value

# Run the test
./ejecuta_test.sh
