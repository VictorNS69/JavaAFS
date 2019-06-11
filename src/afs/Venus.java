package afs;

import java.net.MalformedURLException;
import java.rmi.*; 

public class Venus {
	public String REGISTRY_HOST = System.getenv("REGISTRY_HOST");
	public String REGISTRY_PORT = System.getenv("REGISTRY_PORT");
	public int BLOCKSIZE = Integer.valueOf(System.getenv("BLOCKSIZE"));
	public Vice vice = (Vice) Naming.lookup("//" + REGISTRY_HOST + ":" + REGISTRY_PORT + "/AFS");
	public static VenusCB vcb;
	
    public Venus() throws MalformedURLException, RemoteException, NotBoundException {
    	this.vcb = new VenusCBImpl();
    }
}

