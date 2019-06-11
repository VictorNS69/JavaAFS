// Implementación de la interfaz de servidor que define los métodos remotos
// para iniciar la carga y descarga de ficheros
package afs;
import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.*;
import java.rmi.server.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ViceImpl extends UnicastRemoteObject implements Vice {
    private LockManager lm;
    private HashMap<String, ArrayList<VenusCB>> callbacks;
    
	public ViceImpl() throws RemoteException {
    	this.lm = new LockManager();
    	this.callbacks = new HashMap<String, ArrayList<VenusCB>>();
    }
    
    public ViceReader download(String fileName, String mode, VenusCB vcb)
          throws RemoteException, FileNotFoundException {
    	File f = new File("./AFSDir/" + fileName);
    	if (!f.exists() && mode.equals("rw")) {
    		addCallback(fileName, vcb);
    		return null;
    	}
    	ViceReaderImpl vri = new ViceReaderImpl(fileName, mode, this.lm);
    	addCallback(fileName, vcb);
        return vri;
     }
    
    public ViceWriter upload(String fileName, String mode, VenusCB vcb)
          throws RemoteException, FileNotFoundException {
    	for (VenusCB v: this.callbacks.get(fileName)) {
    		if (!v.equals(vcb)) {
    			v.invalidate(fileName);
    		} 
		}
        return new ViceWriterImpl(fileName, "rw", this.lm);
    }
    
    public void addCallback(String fileName, VenusCB vcb) throws RemoteException {
    	if (!this.callbacks.containsKey(fileName)) {
    		this.callbacks.put(fileName, new ArrayList<VenusCB>());
    	}
    	this.callbacks.get(fileName).add(vcb);
    }
}
