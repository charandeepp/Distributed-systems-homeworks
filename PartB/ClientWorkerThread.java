

import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;

/**
 * A Client thread which does the random picking of any two account holders and
 * transfers amount from one to other for the number of times specified
 * 
 * @author rkandur
 *
 */
public class ClientWorkerThread extends Thread {

	private String serverHost_;			// RMI Server host
	private Integer serverPort_;		// RMI Server port
	private Integer numOfOperations_;	// number of times the transfer should be done
	private Integer[] accountIDs_;		// set of current Account ID's
	
	public ClientWorkerThread(String serverHost, Integer serverPort,
			Integer numOfOperations, Integer[] accountIDs) {
		this.serverHost_ = serverHost;
		this.serverPort_ = serverPort;
		this.numOfOperations_ = numOfOperations;
		this.accountIDs_ = accountIDs;
	}

	private Integer getRandomAccount() {
		int rnd = new Random().nextInt(accountIDs_.length);
        return accountIDs_[rnd];
	}
	
	public void run() {

		super.run();
		
		System.setSecurityManager(new RMISecurityManager());
		IBankServer impl = null;
		try {

			Registry localRegistry = LocateRegistry.getRegistry(serverHost_, serverPort_);
			impl = (IBankServer) localRegistry.lookup("BankServer");
			for(int i = 0; i < numOfOperations_; ++i) {
				Integer sourceID = getRandomAccount();
				Integer destinationID = getRandomAccount();
				ClientUtils.transferAmount(impl, sourceID, destinationID, 10);
			}
			
		} catch (RemoteException e) {
			ClientLogger.logger().severe(e.getMessage());
			return;
		} catch (NotBoundException e) {
			ClientLogger.logger().severe(e.getMessage());
			return;
		}
		
	}
	
}
