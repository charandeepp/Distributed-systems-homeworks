

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface which is used to build an RMI Server {@link BankServerImpl}
 * 
 * @author rkandur
 *
 */
public interface IBankServer extends Remote {

	ResponseObject handleRequest(IRequestObject reqObject) throws RemoteException;
	
}
