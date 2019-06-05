package afs;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.rmi.*;
import java.rmi.server.*;

@SuppressWarnings("serial")
public class ViceReaderImpl extends UnicastRemoteObject implements ViceReader {
    private static final String AFSDir = "AFSDir/";
    private RandomAccessFile raf;
    
    @SuppressWarnings("static-access")
	public ViceReaderImpl(String fileName, String mode)
		    throws RemoteException, FileNotFoundException {
    	this.raf = new RandomAccessFile(this.AFSDir + fileName, mode);
    }
    
    public byte[] read(int tam) throws IOException {
        byte[] bytes = new byte[tam];
    	return (this.raf.read(bytes) < 0) ? null: bytes;
    }
    
    public void close() throws IOException {
        this.raf.close();
    }
    
    public long getSize() throws IOException {
    	return this.raf.length();
    }
}       

