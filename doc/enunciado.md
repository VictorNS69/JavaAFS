# Objetivo de la práctica
La práctica consiste en desarrollar un sistema de ficheros distribuido en un entorno Java que permita que el alumno llegue a conocer de forma práctica el tipo de técnicas que se usan en estos sistemas, tal como se estudió en la parte teórica de la asignatura.

Con respecto al sistema que se va a desarrollar, se trata de un SFD basado en el modelo de carga/descarga, con una semántica de sesión, con caché en los clientes almacenada en disco e invalidación iniciada por el servidor. Es, por tanto, un sistema con unas características similares al AFS estudiado en la parte teórica de la asignatura Se recomienda, por tanto, que el alumno revise el sistema de ficheros AFS en la documentación de la asignatura antes de afrontar la práctica.

Evidentemente, dadas las limitaciones de este trabajo, el sistema a desarrollar presenta enormes simplificaciones con respecto a las funcionalidades presentes en un sistema AFS real, entre ellas:
- Hay un único servidor.
- No hay directorios. Concretamente, el servidor (vice; directorio servidor) exportará solo los ficheros que se encuentran en el directorio AFSdir (o sea, que si un cliente solicita el fichero f, el servidor enviará el fichero AFSdir/f). Nótese que los ficheros pueden ser de texto o binarios.
- Se trata de un sistema monousuario, no existiendo el concepto de permisos de acceso a un fichero (o sea, todos los ficheros son accesibles).
- Cada nodo "cliente" (_venus_; directorios cliente1, cliente2...) se corresponde con una JVM, pudiendo estas estar ejecutando en distintas máquinas.
- Las copias de los ficheros en el cliente se almacenan en un directorio llamado Cache dentro de cada nodo "cliente", y tendrán el mismo nombre que el del fichero.
- Se asume que las aplicaciones no son concurrentes y que no va a haber sesiones de escritura simultáneas sobre un mismo fichero aunque sí puede haber múltiples sesiones de lectura mientras que se produce una de escritura.
- A las aplicaciones se les ofrece un API similar a la clase RandomAccessFile de Java, pero limitándose, para simplificar el trabajo, a las operaciones de lectura (read) y escritura (write) usando vectores de bytes, así como de modificación de la posición del puntero (seek) y de cambio de tamaño de un fichero (setLength). Se limita también los modos de apertura posibles de un fichero a solo dos, manteniendo la misma semántica que en RandomAccessFile:
  - Modo r: si el fichero existe se abre en modo lectura; en caso contrario, se genera la excepción FileNotFoundException.
  - Modo rw: si el fichero existe se abre en modo lectura/escritura; en caso contrario, también se crea previamente.
- Asimismo, se usa la clase RandomAccessFile para el acceso interno, tanto local como remoto, a los datos del fichero.

Para completar esta sección introductoria, se incluyen, a continuación, dos fragmentos de código que permiten apreciar las diferencias entre el API de acceso de lectura/escritura a un fichero local usando la clase RandomAccessFile, en la que se inspira el API planteado, y el correspondiente al acceso remoto utilizando el servicio que se pretende desarrollar:
```jv
// acceso local
try {
RandomAccessFile f = new RandomAccessFile("fich", "rw");
byte[] b = new byte[1024];
leido = f.read(b);
f.seek(0);
f.write(b);
f.setLength(512);
f.close();
}
catch (FileNotFoundException e) {
e.printStackTrace();
}
catch (IOException e) {
e.printStackTrace();
}
```
```jv
// acceso remoto (en negrilla aparecen los cambios respecto a un acceso local)
try {
// iniciación de la parte cliente
Venus venus = new Venus();

// apertura de un fichero;
// el modo de apertura solo puede ser "r" o "rw"
// y tiene el mismo comportamiento que en RandomAccessFile.
// Si el fichero no está en la caché, se descarga del servidor
// y se almacena en el directorio Cache.
// Finalmente, se abre el fichero en Cache como un RandomAccessFile.
VenusFile f = new VenusFile(venus, "fich", "rw"); 

// resto de las operaciones igual que en local;
// de hecho, se realizan sobre la copia local
byte[] b = new byte[1024];
leido = f.read(b);
f.seek(0);
f.write(b);
f.setLength(512);

// si el fichero se ha modificado, se vuelca al servidor
f.close();
}
catch (FileNotFoundException e) {
e.printStackTrace();
}
catch (IOException e) {
e.printStackTrace();
}
catch (Exception e) {
e.printStackTrace();
}
```
