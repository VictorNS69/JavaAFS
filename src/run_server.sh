cd afs

./compila_y_construye_JARS.sh 
cp afs_* ../cliente1/ 
cp afs_* ../cliente2/
cp afs_* ../cliente3/
cp afs_* ../cliente4/

cp afs_* ../servidor/

cp afs_* ../

cd ../servidor
./compila_servidor.sh
./arranca_rmiregistry 10048 &
./ejecuta_servidor.sh 10048 



