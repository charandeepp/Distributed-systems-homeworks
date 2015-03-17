import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.logging.Logger;

/** CSci5105 Spring 2015
 * Assignment# 2
 * name: <Ravali Kandur>, <Charandeep Parisineti>
 * student id: <5084769>, <5103173>
 * x500 id: <id1>, <id2 (optional)>
 * CSELABS machine: <kh1262-08.cselabs.umn.edu, kh1262-09.cselabs.umn.edu, 
 * 					kh1262-10.cselabs.umn.edu, kh1262-11.cselabs.umn.edu>
 */

/**
 * RMI Client which is used for testing the server client interaction in a
 * multi threaded environment
 * 
 * @author rkandur
 *
 */
public class MultiBankClient {

	public static void main(String[] args) {
		
		/**
		 * Check if all the arguments are passed correctly
		 */
		if (args.length != 4) {
			throw new RuntimeException("SingleBankClient <server_host> <port> <number_of_threads> <no_of_operations>");
		}
		
		String serverHost = args[0];
		Integer serverPort = Integer.parseInt(args[1]);
		Integer numOfThreads = Integer.parseInt(args[2]);
		Integer numOfOperations = Integer.parseInt(args[3]);
		Logger logger = ClientLogger.logger();
		
		System.setSecurityManager(new RMISecurityManager());
		IBankServer impl = null;
		try {
			Registry localRegistry = LocateRegistry.getRegistry(serverHost, serverPort);
			impl = (IBankServer) localRegistry.lookup("BankServer");
		} catch (RemoteException e) {
			e.printStackTrace();
			return;
		} catch (NotBoundException e) {
			e.printStackTrace();
			return;
		}
		
		/**
		 * #1: sequentially create 100 accounts on the server
		 **/
		
		Integer[] accountIDs = new Integer[100];
		for(Integer i = 0; i < 100; ++i) {
			try {
				Integer a = ClientUtils.createAccount(impl, "firstname"+i.toString(), "lastname"+i.toString(), "address"+i.toString());
				if(a == -1) {
					i = i-1;	// this is to make sure that we have 100 proper accounts
					continue;
				}
				accountIDs[i]=a;
			} catch (RemoteException e) {
				i = i-1;	// this is to make sure that we have 100 proper accounts
			}
		}
		
		/**
		 * #2: sequentially deposit 100 in each of these accounts. 
		 **/
		for(int i = 0; i < 100; ++i) {
			try {
				ClientUtils.depositToAccount(impl, accountIDs[i], 100);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * #3: sequentially execute getBalance on each account, and print sum of
		 * the total balance of all accounts. This value should be 10,000
		 **/
		Integer sumOfBal = new Integer(0);
		for(int i = 0; i < 100; ++i) {
			try {
				sumOfBal += ClientUtils.balanceEnquiry(impl, accountIDs[i]);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		logger.info("Accumulated balance of all accounts = " + sumOfBal);
		System.out.println("Accumulated balance of all accounts = " + sumOfBal);
		
		/**
		 * #4: create the specified number of client threads, and each thread
		 * will perform certain sequence of operations
		 */
		ArrayList<ClientWorkerThread> threads = new ArrayList<ClientWorkerThread>(); 
		for(int i = 0; i < numOfThreads; ++i) {
			ClientWorkerThread t = new ClientWorkerThread(serverHost, serverPort, numOfOperations, accountIDs);
			t.start();
			threads.add(t);
		}
		
		/**
		 * #5: wait for the completion of all of the client threads 
		 */
		for(ClientWorkerThread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * #6: sequentially execute getBalance on each account, and print sum of
		 * the total balance of all accounts. This value should be 10,000
		 **/
		Integer newSumOfBal = new Integer(0);
		for(int i = 0; i < 100; ++i) {
			try {
				newSumOfBal += ClientUtils.balanceEnquiry(impl, accountIDs[i]);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		logger.info("Accumulated balance of all accounts = " + newSumOfBal);
		System.out.println("Accumulated balance of all accounts = " + newSumOfBal);
		
		
	}
	
}
