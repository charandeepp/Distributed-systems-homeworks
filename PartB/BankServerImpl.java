import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
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
 * class which has the Implementation details of an RMI Server
 * 
 * @author rkandur
 *
 */
public class BankServerImpl extends UnicastRemoteObject implements IBankServer {

	private static final long serialVersionUID = 1L;
	private static Integer DEFAULT_PORT_NUMBER = 1099;

	// store which maintains all the accounts for this bank
	private static Hashtable<Integer, Account> accountsStore_ =  new Hashtable<Integer, Account>();
	private Logger logger_ = ServerLogger.logger(); 
	
	protected BankServerImpl() throws RemoteException {
		super();
	}
	
	private void updateStore(Account account) {
		synchronized (this) {
			accountsStore_.put(account.getAccountID(), account);
		}
	}

	private Account getFromStore(Integer accountID) {
		synchronized (this) {
			return accountsStore_.get(accountID);
		}
	}

	public ResponseObject handleRequest(IRequestObject reqObject)
			throws RemoteException {

		RequestType type = (RequestType)reqObject.reqType();
		ResponseObject response = new ResponseObject(Boolean.FALSE, "INVALID_REQUEST");
		
		switch(type) {
		
			case newaccount: {
				response = handleNewAccountRequest(reqObject);
				break;
			}
			case deposit: {
				response = handleDepositRequest(reqObject);
				break;
			}
			case withdraw: {
				response = handleWithdrawRequest(reqObject);
				break;
			}
			case transfer: {
				response = handleTransferRequest(reqObject);
				break;
			}
			case balance: {
				response = handleBalanceRequest(reqObject);
				break;
			}
			default: {
				break;
			}
		}
		
		return response;
		
	}
	
	// Handles new Balance Enquiry Request
	private ResponseObject handleBalanceRequest(IRequestObject reqObject) {
		BalanceRequestObject bro = (BalanceRequestObject) reqObject;
		BalanceRequest breq = new BalanceRequest(getFromStore(bro.accountID()));
		RequestResponse response = breq.execute();
		logger_.info(bro.reqType().name() + " -> " + response.getResponse());
		return new ResponseObject(response.getStatus(), response.getResponse());
	}

	// Handles Balance Transfer Request
	private ResponseObject handleTransferRequest(IRequestObject reqObject) {
		TransferRequestObject tro = (TransferRequestObject) reqObject;
		TransferRequest treq = new TransferRequest(getFromStore(tro.sourceID()), getFromStore(tro.destinationID()), tro.amount());
		RequestResponse response = null;
		synchronized (this) {
			response = treq.execute();
			if(response.getStatus()) {
				AccountPair res = (AccountPair)response.getResult();
				updateStore(res.getSource());
				updateStore(res.getDestination());
			}
		}
		logger_.info(tro.reqType().name() + " -> " + response.getResponse());
		return new ResponseObject(response.getStatus(), response.getResponse());
	}

	// Handles Money Withdraw Request
	private ResponseObject handleWithdrawRequest(IRequestObject reqObject) {
		WithdrawRequestObject wro = (WithdrawRequestObject) reqObject;
		WithdrawRequest wreq = new WithdrawRequest(getFromStore(wro.accountID()), wro.amount());
		RequestResponse response = wreq.execute();
		if(response.getStatus()) {
			updateStore((Account)response.getResult());
		}
		logger_.info(wro.reqType().name() + " -> " + response.getResponse());
		return new ResponseObject(response.getStatus(), response.getResponse());
	}

	// Handles money Deposit Request
	private ResponseObject handleDepositRequest(IRequestObject reqObject) {
		DepositRequestObject dro = (DepositRequestObject) reqObject;
		DepositRequest dreq = new DepositRequest(getFromStore(dro.accountID()), dro.amount());
		RequestResponse response = dreq.execute();
		if(response.getStatus()) {
			updateStore((Account)response.getResult());
		}
		logger_.info(dro.reqType().name() + " -> " + response.getResponse());
		return new ResponseObject(response.getStatus(), response.getResponse());
	}

	// Handles new Account Creation Request
	private ResponseObject handleNewAccountRequest(IRequestObject reqObject) {
		NewAccountRequestObject aro = (NewAccountRequestObject) reqObject;
		NewAccountRequest areq = new NewAccountRequest(aro.firstName(), aro.lastName(), aro.address());
		RequestResponse response = areq.execute();
		if(response.getStatus()) {
			updateStore((Account)response.getResult());
		}
		logger_.info(aro.reqType().name() + " -> " + response.getResponse());
		return new ResponseObject(response.getStatus(), response.getResponse());
	}
	
	public static void main(String[] args) {
		
		System.setSecurityManager(new RMISecurityManager());
		try {
			BankServerImpl server = new BankServerImpl();
			String hostname = new String();
			try {
			    hostname = java.net.InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
			    e.printStackTrace();
			}
			Integer port = (args.length == 1) ? Integer.parseInt(args[0]) : DEFAULT_PORT_NUMBER;
			Registry localRegistry = LocateRegistry.getRegistry(hostname, port);
			localRegistry.bind("BankServer", server);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (AlreadyBoundException e) {
			e.printStackTrace();
		}
	}



}
