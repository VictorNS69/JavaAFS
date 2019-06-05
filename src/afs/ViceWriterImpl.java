// Implementación de la interfaz de servidor que define los métodos remotos
// para completar la carga de un fichero
package afs;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.rmi.*;
import java.rmi.server.*;

public class ViceWriterImpl extends UnicastRemoteObject implements ViceWriter {
    private static final String AFSDir = "AFSDir/";
    private RandomAccessFile raf;
    
    @SuppressWarnings("static-access")
	public ViceWriterImpl(String fileName, String mode)
		    throws RemoteException, FileNotFoundException {
    	this.raf = new RandomAccessFile(this.AFSDir + fileName, mode);
    }
    
    public void write(byte [] b) throws IOException {
        this.raf.setLength(0);
        this.raf.write(b);
    }
    
    public void close() throws IOException {
        this.raf.close();
    }
}       

