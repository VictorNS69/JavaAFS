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
    private boolean modified;
    
    @SuppressWarnings("static-access")
	public VenusFile(Venus venus, String fileName, String mode) throws RemoteException, IOException, FileNotFoundException {
    	this.venus = venus;
    	this.mode = mode;
    	this.filename = fileName;
    	this.modified = false;
    	File file_cache = new File("./" + cacheDir + fileName);
    	if (!file_cache.exists()) 
    		downloadFile(fileName, mode);
    	
    	this.raf = new RandomAccessFile("./" + this.cacheDir + fileName, mode);
    }
    
    public int read(byte[] b) throws RemoteException, IOException {
        return this.raf.read(b);
    }
    
    public void write(byte[] b) throws RemoteException, IOException {
    	this.modified = true;
        this.raf.write(b);
    }
    
    public void seek(long p) throws RemoteException, IOException {
        this.raf.seek(p);
    }
    
    public void setLength(long l) throws RemoteException, IOException {
    	this.modified = true;
        this.raf.setLength(l);
    }
    
    public void close() throws RemoteException, IOException {
        if(this.mode.equals("rw") && this.modified)
        	uploadFile(this.filename);
        this.raf.close();
    }
    
    public void downloadFile(String filename, String mode) throws IOException {
    	ViceReader vr = (ViceReader) this.venus.vice.download(filename, mode, Venus.vcb);
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
    		}
    	}
    	vr.close();
    	raf.close();
    }
    
    public void uploadFile(String filename) throws RemoteException, IOException{
    	ViceWriter vw = this.venus.vice.upload(filename, this.mode, Venus.vcb);
    	vw.adjust(this.raf.length());
    	this.raf.seek(0);
    	int n_blocks = (int) this.raf.length() / this.venus.BLOCKSIZE;
    	byte[] bytes = new byte[this.venus.BLOCKSIZE];
    	for (int i = 0; i < n_blocks; i++) {
    		read(bytes);
    		vw.write(bytes);
    	}
    	if ((this.raf.length() % this.venus.BLOCKSIZE) > 0) {
    		bytes = new byte[(int)this.raf.length() % this.venus.BLOCKSIZE];
    		read(bytes);
    		vw.write(bytes);
    	}
    	vw.close();
    }
}

