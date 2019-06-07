# JavaAFS
Sistema de ficheros distribuido en un entorno Java que permita que el alumno llegue a conocer de forma práctica el tipo de técnicas que se usan en estos sistemas.
## Objetivo
La práctica consiste en desarrollar un sistema de ficheros distribuido en un entorno Java que permita que el alumno llegue a conocer de forma práctica el tipo de técnicas que se usan en estos sistemas, tal como se estudió en la parte teórica de la asignatura.

Con respecto al sistema que se va a desarrollar, se trata de un SFD basado en el modelo de carga/descarga, con una semántica de sesión, con caché en los clientes almacenada en disco e invalidación iniciada por el servidor. Es, por tanto, un sistema con unas características similares al AFS estudiado en la parte teórica de la asignatura Se recomienda, por tanto, que el alumno revise el sistema de ficheros AFS en la documentación de la asignatura antes de afrontar la práctica.

Evidentemente, dadas las limitaciones de este trabajo, el sistema a desarrollar presenta enormes simplificaciones con respecto a las funcionalidades presentes en un sistema AFS real.

## Enunciado
Para mayor información, consulte el enunciado de la practica [aquí](/doc/enunciado.md).

## Ejecución
Debemos estar situados en el directorio `src`
```sh
cd src
```
Luego hay que ejecutar el servidor en un terminal
```sh
./run_server.sh
```
Y en otro terminal distinto tenemos que ejecutar el cliente
```sh
./run_client.sh
```
Esto ejecutará un único cliente (_cliente1_ por defecto), con el cual se podrá interactuar.

## Directorios importantes
Los ficheros del **servidor** se guardarán en el directorio `src/servidor/AFSDir`. Ahí tienen que estar todos los archivos que quieras que se mantengan en el servidor. Además, ahí es donde se guardarán los ficheros nuevos y las modificaciones de los archivos ya existentes.

Los ficheros en la **caché del cliente** se encontrarán en la carpeta _Cache_ de cada cliente, por ejemplo para el _cliente1_, su carpeta caché estará en `src/cliente1/Cache`. 

**NOTA:** Ambas carpetas se borrarán cada vez que se ejecute el servidor. Si deseas que se mantengan los archivos de `src/servidor/AFSDir`,
## Autor
[Víctor Nieves Sánchez](https://twitter.com/VictorNS69)

## Licencia
[Licencia](/LICENSE).
