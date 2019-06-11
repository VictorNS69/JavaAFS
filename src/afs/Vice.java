// Interfaz de servidor que define los métodos remotos para iniciar
// la carga y descarga de ficheros
package afs;
import java.io.FileNotFoundException;
import java.rmi.*;

public interface Vice extends Remote {
    public ViceReader download(String fileName, String mode, VenusCB vcb)
          throws RemoteException, FileNotFoundException;
    public ViceWriter upload(String fileName, String mode, VenusCB vcb)
          throws RemoteException, FileNotFoundException;
    public void addCallback(String fileName, VenusCB vcb) throws RemoteException;

    /* añada los métodos remotos que requiera */
}
       

