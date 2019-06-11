package afs;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.rmi.*;
import java.rmi.server.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@SuppressWarnings("serial")
public class ViceReaderImpl extends UnicastRemoteObject implements ViceReader {
    private static final String AFSDir = "AFSDir/";
    private RandomAccessFile raf;
    private ReentrantReadWriteLock lock;
    private LockManager lm;
    private String filename;
    
    @SuppressWarnings("static-access")
	public ViceReaderImpl(String fileName, String mode, LockManager lm)
		    throws RemoteException, FileNotFoundException {
    	this.filename = fileName;
    	this.lm = lm;
    	this.lock = this.lm.bind(fileName);
    	this.raf = new RandomAccessFile(this.AFSDir + fileName, mode);
    }
    
    public byte[] read(int tam) throws IOException {
    	this.lock.readLock().lock();
        byte[] bytes = new byte[tam];
        byte[] value = this.raf.read(bytes) < 0 ? null: bytes;
        this.lock.readLock().unlock();
    	return value;
    }
    
    public void close() throws IOException {
        this.raf.close();
    	this.lm.unbind(this.filename);
    }
    
    public long getSize() throws IOException {
    	return this.raf.length();
    }
}       

