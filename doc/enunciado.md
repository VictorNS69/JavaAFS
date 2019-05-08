# Objetivo de la práctica
La práctica consiste en desarrollar un sistema de ficheros distribuido en un entorno Java que permita que el alumno llegue a conocer de forma práctica el tipo de técnicas que se usan en estos sistemas, tal como se estudió en la parte teórica de la asignatura.

Con respecto al sistema que se va a desarrollar, se trata de un SFD basado en el modelo de carga/descarga, con una semántica de sesión, con caché en los clientes almacenada en disco e invalidación iniciada por el servidor. Es, por tanto, un sistema con unas características similares al AFS estudiado en la parte teórica de la asignatura Se recomienda, por tanto, que el alumno revise el sistema de ficheros AFS en la documentación de la asignatura antes de afrontar la práctica.

Evidentemente, dadas las limitaciones de este trabajo, el sistema a desarrollar presenta enormes simplificaciones con respecto a las funcionalidades presentes en un sistema AFS real, entre ellas:
- Hay un único servidor.
- No hay directorios. Concretamente, el servidor (vice; directorio servidor) exportará solo los ficheros que se encuentran en el directorio AFSdir (o sea, que si un cliente solicita el fichero f, el servidor enviará el fichero _AFSdir/f_). Nótese que los ficheros pueden ser de texto o binarios.
- Se trata de un sistema monousuario, no existiendo el concepto de permisos de acceso a un fichero (o sea, todos los ficheros son accesibles).
- Cada nodo "cliente" (_venus_; directorios cliente1, cliente2...) se corresponde con una JVM, pudiendo estas estar ejecutando en distintas máquinas.
- Las copias de los ficheros en el cliente se almacenan en un directorio llamado Cache dentro de cada nodo "cliente", y tendrán el mismo nombre que el del fichero.
- Se asume que las aplicaciones no son concurrentes y que no va a haber sesiones de escritura simultáneas sobre un mismo fichero aunque sí puede haber múltiples sesiones de lectura mientras que se produce una de escritura.
- A las aplicaciones se les ofrece un API similar a la clase RandomAccessFile de Java, pero limitándose, para simplificar el trabajo, a las operaciones de lectura (_read_) y escritura (_write_) usando vectores de bytes, así como de modificación de la posición del puntero (_seek_) y de cambio de tamaño de un fichero (_setLength_). Se limita también los modos de apertura posibles de un fichero a solo dos, manteniendo la misma semántica que en RandomAccessFile:
  - Modo _r_: si el fichero existe se abre en modo lectura; en caso contrario, se genera la excepción `FileNotFoundException`.
  - Modo _rw_: si el fichero existe se abre en modo lectura/escritura; en caso contrario, también se crea previamente.
- Asimismo, se usa la clase RandomAccessFile para el acceso interno, tanto local como remoto, a los datos del fichero.

Para completar esta sección introductoria, se incluyen, a continuación, dos fragmentos de código que permiten apreciar las diferencias entre el API de acceso de lectura/escritura a un fichero local usando la clase RandomAccessFile, en la que se inspira el API planteado, y el correspondiente al acceso remoto utilizando el servicio que se pretende desarrollar:
```java
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
```java
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
Para afrontar el trabajo de manera progresiva, se propone un desarrollo incremental en varias fases. Por cada fase, se indicará qué funcionalidad desarrollar como parte de la misma y qué pruebas concretas realizar para verificar el comportamiento correcto del código desarrollado.
- Acceso de lectura a un fichero remoto sin tener en cuenta aspectos de coherencia (valoración de **3 puntos**).
- Acceso de escritura a un fichero remoto sin tener en cuenta aspectos de coherencia (valoración de **3 puntos**).
- Incorporación de un modelo de coherencia asumiendo que solo puede haber una sesión de escritura, con múltiples de lectura, en cada momento (valoración de **4 puntos**).
## Arquitectura del software del sistema
Antes de pasar a presentar cada una de las fases, se especifica en esta sección qué distintos componentes hay en este sistema.
En primer lugar, hay que resaltar que la práctica está diseñada para no permitir la definición de nuevas clases (a no ser que se trate de clases anidadas), estando todas ya presentes, aunque mayoritariamente vacías, en el material de apoyo.

No todas ellas serán necesarias en las primeras fases de la práctica, como se irá explicando en esta misma sección y a lo largo del documento. Por tanto, no es necesario que entienda el objetivo de cada clase en este punto, ya que se irá descubriendo a lo largo de las sucesivas fases.

El software de la práctica está organizado en tres directorios: _cliente_ (realmente, hay varios directorios cliente para asegurarse de que cada JVM tiene su propia caché si se ejecutan varios "nodos cliente" en la misma máquina real), _servidor_ y _afs_. Empecemos por este último, que contiene las clases que proporcionan la funcionalidad del servicio a desarrollar, que estarán incluidas en el paquete afs. A continuación, se comenta brevemente el objetivo de cada una, que será posteriormente explicado en detalle en las secciones del documento dedicadas a presentar progresivamente la funcionalidad requerida.
- `Venus`: Clase de cliente que proporciona acceso al servicio realizando la iniciación de la parte cliente. La aplicación deberá instanciar un objeto de esta clase antes de interaccionar con el servicio desarrollado. Esta clase se requerirá desde la primera fase de la práctica.
- `VenusFile`: Clase de cliente que proporciona el API del servicio AFS, existiendo un objeto de esta clase por cada uno de los ficheros a los que está accediendo la aplicación. La aplicación deberá instanciar un objeto de esta clase para acceder a un fichero. Esta clase se requerirá desde la primera fase de la práctica.
- `Vice` y `ViceImpl`: Interfaz remota, y clase que la implementa, que ofrece en el servidor el servicio AFS proporcionando métodos remotos para iniciar la carga y descarga de ficheros. Al arrancar el servicio, el servidor, que ya está programado, instancia y registra un objeto de esta clase. Estas clases se requerirán desde la primera fase de la práctica.
- `ViceReader` y `ViceReaderImpl`: Interfaz remota, y clase que la implementa, que ofrece en el servidor los servicios para completar la descarga de un fichero. Se creará una clase de este tipo en el servidor cada vez que se produce una operación de descarga. Estas clases se requerirán desde la primera fase de la práctica.
- `ViceWriter` y `ViceWriterImpl`: Interfaz remota, y clase que la implementa, que ofrece en el servidor los servicios para completar la carga de un fichero. Se creará una clase de este tipo en el servidor cada vez que se produce una operación de carga. Estas clases se requerirán a partir de la segunda fase de la práctica.
- `VenusCB` y `VenusCBImpl`: Interfaz remota, y clase que la implementa, que ofrece en el cliente el servicio de _callback_ requerido para implementar el protocolo de coherencia. Se instanciará un objeto de esta clase en la parte cliente al iniciarse la interacción con el servicio. Estas clases se requerirán solo en la tercera fase de la práctica.
- `LockManager`: Clase de servidor que gestiona cerrojos de lectura/escritura para sincronizar el acceso a un fichero. Ya está implementada y **no debe modificarse**. Se requerirá en la tercera fase de la práctica.

Con respecto a los directorios cliente, en los mismos se encuentra la clase _Test_, que es un programa interactivo que sirve para probar la funcionalidad de la práctica, permitiendo que el usuario pueda realizar operaciones de lectura, escritura, posicionamiento y cambio de tamaño sobre ficheros. Asimismo, este programa recibirá como variables de entorno la siguiente información:
- en qué máquina y por qué puerto está dando servicio el proceso _rmiregistry_ en las variables _REGISTRY_HOST_ y _REGISTRY_PORT_, respectivamente.
- el tamaño de bloque usado en las transferencias entre los clientes y el servidor: _BLOCKSIZE_.

Mediante el uso de enlaces simbólicos, los directorios de cliente comparten todos los ficheros (excepto, evidentemente, el directorio Cache), no habiendo que realizar ningún desarrollo de código en los mismos a no ser que uno quiera preparar sus propios programas de prueba.
En cuanto al directorio servidor, donde tampoco hay que hacer ningún desarrollo, este incluye la clase ServidorAFS que inicia el servicio dándole de alta en el _rmiregistry_ con el nombre AFS (instancia un objeto de la clase ViceImpl y lo registra). Este programa recibe como argumento el número de puerto por el que escucha el proceso _rmiregistry_ previamente activado. Este directorio contiene un subdirectorio denominado AFSDir que será donde se ubiquen los ficheros del servidor.

Además de las diversas clases, en los distintos directorios se incluyen _scripts_ para facilitar la compilación de las clases y la ejecución de los programas, así como la distribución de las clases requeridas por el cliente y el servidor, en forma de ficheros JAR, teniendo en cuenta que estos pueden residir en distintas máquinas.
## Ejecución de la práctica
Aunque para toda la gestión del ciclo de desarrollo del código de la práctica se puede usar el IDE que se considere oportuno, para aquellos que prefieran no utilizar una herramienta de este tipo, se proporcionan una serie de _scripts_ que permiten realizar toda la labor requerida. En esta sección, se explica cómo trabajar con estos scripts.

Para probar la práctica, debería, en primer lugar, compilar todo el código desarrollado que se encuentra en el directorio, y paquete, afs, generando los ficheros JAR requeridos por el cliente y el servidor.
```sh
cd afs
./compila_y_construye_JARS.sh
```
A continuación, hay que compilar y ejecutar el servidor, activando previamente _rmiregistry_.
```sh
cd servidor
./compila_servidor.sh
./arranca_rmiregistry 12345 &
./ejecuta_servidor.sh 12345
```
Por último, hay que compilar y ejecutar el cliente de prueba.
```sh
cd cliente1
./compila_test.sh
export REGISTRY_HOST=triqui3.fi.upm.es
export REGISTRY_PORT=12345
export BLOCKSIZE=... # el tamaño que considere oportuno
./ejecuta_test.sh
```
Nótese que el servidor y el cliente pueden ejecutarse en distintas máquinas. Además, tenga en cuenta que, si ejecuta varios clientes en la misma máquina, debería hacerlo en diferente directorio de cliente (cliente1, cliente2...).
