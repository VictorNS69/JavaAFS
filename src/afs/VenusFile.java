// Clase de cliente que define la interfaz a las aplicaciones.
// Proporciona la misma API que RandomAccessFile.
package afs;

import java.rmi.*; 
import java.io.*; 

public class VenusFile {
    public static final String cacheDir = "Cache/";
	private RandomAccessFile raf;
    private Venus venus;
    private String mode;
    private String filename;
    
    @SuppressWarnings("static-access")
	public VenusFile(Venus venus, String fileName, String mode) throws RemoteException, IOException, FileNotFoundException {
    	this.venus = venus;
    	this.mode = mode;
    	this.filename = fileName;
    	File file_cache = new File("./" + cacheDir + fileName);
    	if (!file_cache.exists()) // && this.mode.equals("r")) 
    		downloadFile(fileName, mode);
    	
    	this.raf = new RandomAccessFile("./" + this.cacheDir + fileName, mode);
    }
    
    public int read(byte[] b) throws RemoteException, IOException {
        return this.raf.read(b);
    }
    
    public void write(byte[] b) throws RemoteException, IOException {
        this.raf.write(b);
    }
    
    public void seek(long p) throws RemoteException, IOException {
        this.raf.seek(p);
    }
    
    public void setLength(long l) throws RemoteException, IOException {
        this.raf.setLength(l);
    }
    
    public void close() throws RemoteException, IOException {
        if(this.mode.equals("rw")) {
        	this.raf.seek(0);
        	byte[] bytes = new byte[(int)this.raf.length()];
        	read(bytes);
        	uploadFile(bytes);
        }
        this.raf.close();
    }
    
    public void downloadFile(String filename, String mode) throws IOException {
    	ViceReader vr = (ViceReader) this.venus.vice.download(filename, mode);
    	RandomAccessFile raf = new RandomAccessFile("./" + cacheDir + filename, "rw");
    	byte[] bytes;
    	long size = vr.getSize();
    	while(true) {
    		bytes = vr.read(this.venus.BLOCKSIZE);
    		if (bytes == null) {
    			break;
    		}
    		for(int i = 0; i < bytes.length && size-- > 0; i++) {
    			raf.write(bytes[i]);
    			System.out.println(new String(bytes));
    		}
    	}
    	vr.close();
    	raf.close();
    }
    
    public void uploadFile(byte[] bytes) throws RemoteException, IOException{
    	System.out.println("UPLOAD!");
    	ViceWriter vw = this.venus.vice.upload(this.filename, "rw");
    	System.out.println("Fin UPLOAD");
    	
    	vw.write(bytes);
    	vw.close();
    }
}

