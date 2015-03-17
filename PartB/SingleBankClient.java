

import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

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
 * single threaded environment
 * 
 * @author rkandur
 *
 */
public class SingleBankClient {

	public static void main(String[] args) {
	
		if (args.length != 2) {
			System.out.println("SingleBankClient <server_host> <port>");
			return;
		}
		
		System.setSecurityManager(new RMISecurityManager());
		try {
			Registry localRegistry = LocateRegistry.getRegistry(args[0], Integer.parseInt(args[1]));
			IBankServer impl = (IBankServer) localRegistry.lookup("BankServer");
			
			// #1
			System.out.println("Creating First Account *****");
			Integer accountID1 = ClientUtils.createAccount(impl, "Ravali", "Kandur", "708 University Ave");
			System.out.println("Creating Second Account *****");
			Integer accountID2 = ClientUtils.createAccount(impl, "Charandeep", "Parisineti", "600 7th Ave");
			
			// #2
			System.out.println("Depositing to First Account *****");
			ClientUtils.depositToAccount(impl, accountID1, 100);
			System.out.println("Depositing to Second Account *****");
			ClientUtils.depositToAccount(impl, accountID2, 100);
			
			// #3
			System.out.println("Getting Balance from First Account *****");
			ClientUtils.balanceEnquiry(impl, accountID1);
			System.out.println("Getting Balance from Second Account *****");
			ClientUtils.balanceEnquiry(impl, accountID2);
			
			// #4
			System.out.println("Transferring 100 from account1 to account 2 *****");
			ClientUtils.transferAmount(impl, accountID1, accountID2, 100);
			
			// #5
			System.out.println("Getting Balance After Transfer from First Account *****");
			ClientUtils.balanceEnquiry(impl, accountID1);
			System.out.println("Getting Balance After Transfer from Second Account *****");
			ClientUtils.balanceEnquiry(impl, accountID2);
			
			// #6
			System.out.println("Withdrawing 100 from First Account, This should fail *****");
			ClientUtils.withdrawFromAccount(impl, accountID1, 100);
			System.out.println("Withdrawing 100 from Second Account *****");
			ClientUtils.withdrawFromAccount(impl, accountID2, 100);
			
			// #7
			System.out.println("Transferring 100 from account1 to account 2 *****");
			ClientUtils.transferAmount(impl, accountID1, accountID2, 100);
			
			// #8
			System.out.println("Getting Balance from First Account *****");
			ClientUtils.balanceEnquiry(impl, accountID1);
			System.out.println("Getting Balance from Second Account *****");
			ClientUtils.balanceEnquiry(impl, accountID2);
			
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
		
	}

}
