// Implementación de la interfaz de servidor que define los métodos remotos
// para completar la carga de un fichero
package afs;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.rmi.*;
import java.rmi.server.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;


@SuppressWarnings("serial")
public class ViceWriterImpl extends UnicastRemoteObject implements ViceWriter {
    private static final String AFSDir = "AFSDir/";
    private RandomAccessFile raf;
    private ReentrantReadWriteLock lock;
    private LockManager lm;
    private String filename;
    
    @SuppressWarnings("static-access")
	public ViceWriterImpl(String fileName, String mode, LockManager lm)
		    throws RemoteException, FileNotFoundException {
    	this.filename = fileName;
    	this.lm = lm;
    	this.lock = this.lm.bind(fileName);
    	this.raf = new RandomAccessFile(this.AFSDir + fileName, mode);
    }
    
    public void write(byte [] b) throws IOException {
    	this.lock.writeLock().lock();
        this.raf.write(b);
        this.lock.writeLock().unlock();
    }
    
    public void close() throws IOException {
        this.raf.close();
    	this.lm.unbind(filename);
    }
    
    public void adjust(long l) throws IOException{
    	this.lock.writeLock().lock();
    	this.raf.setLength(l);
    	this.lock.writeLock().unlock();
    }
}       

