// Clase de cliente que define la interfaz a las aplicaciones.
// Proporciona la misma API que RandomAccessFile.
package afs;

import java.rmi.*; 
import java.io.*; 

public class VenusFile {
    public static final String cacheDir = "Cache/";
    @SuppressWarnings("unused")
	private RandomAccessFile raf;
    private Venus venus;
    
    @SuppressWarnings("static-access")
	public VenusFile(Venus venus, String fileName, String mode) throws RemoteException, IOException, FileNotFoundException {
    	this.venus = venus;
    	File file_cache = new File("./" + cacheDir + fileName);
    	boolean exist_cache = file_cache.exists();
    	if (!exist_cache) {
    		System.out.println("en descarga");
    		downloadFile(fileName, mode);
    		System.out.println("fin descarga");
    	}
    	System.out.println("l22");
    	this.raf = new RandomAccessFile("./" + this.cacheDir + fileName, mode);
    	System.out.println("l24");
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
        return;
    }
    
    public void downloadFile(String filename, String mode) throws IOException {
    	ViceReader vri = (ViceReader) this.venus.vice.download(filename, mode);
    	RandomAccessFile raf = new RandomAccessFile("./" + cacheDir + filename, "rw");
    	byte[] bytes;
    	long size = vri.getSize();
    	while(true) {
    		bytes = vri.read(this.venus.BLOCKSIZE);
    		if (bytes == null) {
    			System.out.println("BREAK l51");
    			break;
    		}
    		for(int i = 0; i < bytes.length && size-- > 0; i++) {
    			raf.write(bytes[i]);
    			System.out.println(new String(bytes));
    		}
    	}
    	vri.close();
    	raf.close();
    }
}

