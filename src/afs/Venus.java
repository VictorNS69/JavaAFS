package afs;

import java.net.MalformedURLException;
import java.rmi.*; 

public class Venus {
	public String REGISTRY_HOST = System.getenv("REGISTRY_HOST");
	public String REGISTRY_PORT = System.getenv("REGISTRY_PORT");
	public int BLOCKSIZE = Integer.valueOf(System.getenv("BLOCKSIZE"));
	public Vice vice = (Vice) Naming.lookup("//" + REGISTRY_HOST + ":" + REGISTRY_PORT + "/AFS");
	
    public Venus() throws MalformedURLException, RemoteException, NotBoundException {
    	System.out.println("Host:" + REGISTRY_HOST);
    	System.out.println("Port:" + REGISTRY_PORT);
    	System.out.println("Bloq:" + BLOCKSIZE);
    	System.out.println("Vice:" + vice.toString());
    	
    }
}

