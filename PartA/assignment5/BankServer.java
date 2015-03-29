package assignment5;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.PriorityQueue;
import java.util.logging.Logger;

/** CSci5105 Spring 2015
 * Assignment# 5
 * name: <Ravali Kandur>, <Charandeep Parisineti>
 * student id: <5084769>, <5103173>
 * x500 id: <id1>, <id2 (optional)>
 * TODO: need to change these machines later
 * CSELABS machine: <kh1262-08.cselabs.umn.edu, kh1262-09.cselabs.umn.edu, 
 * 					kh1262-10.cselabs.umn.edu, kh1262-11.cselabs.umn.edu>
 */

/**
 * TODO:
 * Need to check if implementation logic for handling HALT messages and killing
 * everyone gracefully is done correctly
 * 
 * Barely added synchronization logic.
 * Need to add locking mechanism etc for multi-threaded environment
 * 
 * we need to calculate the avergar performance time for all requests.
 * Should add this logic, for now just printing the values for just HALT request
 * 
 * Need to check the logging part
 * 
 */

/**
 * class which has the Implementation details of an Bank Server
 * 
 * @author rkandur
 *
 */
public class BankServer {
	
	private static String CONFIG_FILE_PATH = "serverconfig.txt";

	// store which maintains all the accounts for this bank
	private static Hashtable<Integer, Account> accountsStore_ =  new Hashtable<Integer, Account>();
 
	// TODO: should check lamport clock behavior i.e. incrementing for all the events [especially internal events]
	public LamportClock clock_ = new LamportClock();
	
	// map of peer server host and port on which this server can communicate
	private HashMap<String, Integer> peerServers_ = new HashMap<String, Integer>();
	
	// map to hold the requests which are made by client directly to this server
	// and hold a socket object on which response needs to be sent back
	private HashMap<ServerRequest, Socket> directRequestVsConnection_ = new HashMap<ServerRequest, Socket>();
	
	// map to hold the direct requests vs the Acknowledgements made by the other servers
	private HashMap<ServerRequest, HashSet<String>> reqVsAcks_ = new HashMap<ServerRequest, HashSet<String>>();

	//TODO: need to refactor logging as per the HW requirements
	Logger logger_ = ServerLogger.logger();

	private static int processId_;
	private static int serverReqPort_;
	private static int clientReqPort_;
	
	// lock variable to maintain the local queue in state machine model
	// TODO: need to work on synchronization part. use this when handling sync logic
	private Integer reqLock_;
	
	// queue which holds all the yet to be executed requests
	private PriorityQueue<ServerRequest> requests_ = new PriorityQueue<ServerRequest>(new Comparator<ServerRequest>() {
		@Override
		public int compare(ServerRequest r1, ServerRequest r2) {
			// prioritize based on lamport clock values as per total ordering rules in StateMachineModel
			if(r1.getClockValue() == r2.getClockValue()) {
				return r1.getSourceProcessId() - r2.getSourceProcessId() > 0 ? 1 : -1;
			}
			return (int) (r1.getClockValue() - r2.getClockValue()) > 0 ? 1 : -1;
		}
	});
	
	public BankServer(int procId) {
		loadFromConfig();
		processId_ = procId;
	}
	
	// TODO: should call this when we know that there are no other messages from
	// the other servers
	synchronized void execute() {
		
		if(requests_.isEmpty()) {
			return;
		}
		if(!okToProceed(requests_.peek())) {
			return;
		}
		ServerRequest r = requests_.poll();
		ResponseObject resp = serveRequest(r.getRequest());
		logger_.info(processId_ + " " + "PROCESS" + " " + System.currentTimeMillis() + " " + r.getClockValue());
		// if it is a direct request, we also need to send a response back to the client
		if(directRequestVsConnection_.containsKey(r)) {
			try {
				Socket cs = directRequestVsConnection_.remove(r);
				new ObjectOutputStream(cs.getOutputStream()).writeObject(resp);
				cs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if(r.getRequest() instanceof HaltRequestB) {
			// we should exit when we encounter a HALT request
			// TODO: I am not sure if this is the right way to do it. check if
			// we can do it in a better way
			System.exit(0);
		}
	}
	
	// this is to make sure that we have received all the acknowledgements.
	// TODO: this will anyways happen. But what if we miss some acknowledgement?
	// shouldn't we check other server Requests that have a higher timestamp?
	private boolean okToProceed(ServerRequest req) {
		if(reqVsAcks_.containsKey(req)) {
			if(reqVsAcks_.get(req).size() == peerServers_.size()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Helper method to pick up initial config information from cfg file
	 */
	private void loadFromConfig() {

		try {
			File file = new File(CONFIG_FILE_PATH);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				if (!line.startsWith("#")) {
					String[] toks = line.split(" ");
					if (toks.length != 4) {
						continue;
					}
					if(Integer.parseInt(toks[1].trim()) == processId_) {
						clientReqPort_ = Integer.parseInt(toks[2]);
						serverReqPort_ = Integer.parseInt(toks[3]);
					} else {
						peerServers_.put(toks[0].trim(), Integer.parseInt(toks[3].trim()));
					}
				}
			}
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void updateStore(Account account) {
		synchronized (this) {
			accountsStore_.put(account.getAccountID(), account);
		}
	}

	public Account getFromStore(Integer accountID) {
		synchronized (this) {
			return accountsStore_.get(accountID);
		}
	}

	/**
	 * method which is used to execute a request which is on the head of the queue
	 */
	private ResponseObject serveRequest(IRequest req) {
		//TODO: worst way of doing this. try if you can change it later??
		ResponseObject response = new ResponseObject(Boolean.FALSE, "INVALID REQUEST");
		if(req instanceof NewAccountRequestB) {
			response = serveNewAccountRequest(req);
		} else if(req instanceof DepositRequestB) {
			response = serveDepositRequest(req);
		} else if(req instanceof WithdrawRequestB) {
			response = serveWithdrawRequest(req);
		} else if(req instanceof TransferRequestB) {
			response = serveTransferRequest(req);
		} else if(req instanceof BalanceRequestB) {
			response = serveBalanceRequest(req);
		} else if(req instanceof HaltRequestB) {
			response = serveHaltRequest(req);
		}
		return response;
	}
	
	private ResponseObject serveHaltRequest(IRequest req) {
		
		RequestResponse response = (RequestResponse) req.execute();
		// TODO Auto-generated method stub
		logger_.info(RequestType.halt.name() + " -> " + response.getResponse());

		//print all account balances
		logger_.info("Account Balances ... ");
		for(Integer a : accountsStore_.keySet()) {
			logger_.info("Account { " + a + " } has balance = { " + accountsStore_.get(a).getBalance() + " }");
		}
		
		// print all pending requests
		logger_.info("Pending Requests ... ");
		while(!requests_.isEmpty()) {
			ServerRequest r = requests_.poll();
			System.out.println(r.getSourceProcessId()+"_"+r.getClockValue());
		}
		
		// print performance data
		logger_.info("Performance Measurement Data ... ");
		Long timeTaken = System.currentTimeMillis() - ((HaltRequestB)req).getStartTime();
		logger_.info("Time taken to process the request when number of servers = { "
				+ peerServers_.size() + " } is " + timeTaken.toString());
		
		// closing log files
		ServerLogger.closeLogFile();
		
		return new ResponseObject(response.getStatus(), response.getResponse());
	}

	// serves new Balance Inquiry Request
	private ResponseObject serveBalanceRequest(IRequest breq) {
		RequestResponse response = (RequestResponse) breq.execute();
		logger_.info(RequestType.balance.name() + " -> " + response.getResponse());
		return new ResponseObject(response.getStatus(), response.getResponse());
	}

	// serves Balance Transfer Request
	private ResponseObject serveTransferRequest(IRequest treq) {
		RequestResponse response = (RequestResponse) treq.execute();
		if(response.getStatus()) {
			AccountPair res = (AccountPair)response.getResult();
			updateStore(res.getSource());
			updateStore(res.getDestination());
		}
		logger_.info(RequestType.transfer.name() + " -> " + response.getResponse());
		return new ResponseObject(response.getStatus(), response.getResponse());
	}

	// serves Money Withdraw Request
	private ResponseObject serveWithdrawRequest(IRequest wreq) {
		RequestResponse response = (RequestResponse) wreq.execute();
		if(response.getStatus()) {
			updateStore((Account)response.getResult());
		}
		logger_.info(RequestType.withdraw.name() + " -> " + response.getResponse());
		return new ResponseObject(response.getStatus(), response.getResponse());
	}

	// serves money Deposit Request
	private ResponseObject serveDepositRequest(IRequest dreq) {
		RequestResponse response = (RequestResponse) dreq.execute();
		if(response.getStatus()) {
			updateStore((Account)response.getResult());
		}
		logger_.info(RequestType.deposit.name() + " -> " + response.getResponse());
		return new ResponseObject(response.getStatus(), response.getResponse());
	}

	// serves new Account Creation Request
	private ResponseObject serveNewAccountRequest(IRequest areq) {
		RequestResponse response = (RequestResponse) areq.execute();
		if(response.getStatus()) {
			updateStore((Account)response.getResult());
		}
		//TODO: should we need this logging? we should change the log formatter too !!!!
		logger_.info(RequestType.newaccount.name() + " -> " + response.getResponse());
		return new ResponseObject(response.getStatus(), response.getResponse());
	}
	
	/**
	 * Adds a new client request to the server 
	 */
	public void addNewRequest(IRequestObject reqObject, Socket clientSocket, BankServer bankServer) {
		RequestType type = (RequestType)reqObject.reqType();
		ServerRequest sreq = null;
		switch(type) {
			case newaccount: {
				sreq = addNewAccountRequest(reqObject, clientSocket);
				break;
			}
			case deposit: {
				sreq = addDepositRequest(reqObject, clientSocket);
				break;
			}
			case withdraw: {
				sreq = addWithdrawRequest(reqObject, clientSocket);
				break;
			}
			case transfer: {
				sreq = addTransferRequest(reqObject, clientSocket);
				break;
			}
			case balance: {
				sreq = addBalanceRequest(reqObject, clientSocket);
				break;
			}
			case halt: {
				sreq = addHaltRequest(reqObject, clientSocket);
				break;
			}
			default: {
				break;
			}
		}
		if(sreq != null) {
			// TODO: we are passing bankServer instance too much which might
			// lead to synchronization problems, check how to avoid this?

			// this is to broadcast the request received by this server to all
			// the other servers in the pool.
			new ServerBroadcasterThread(peerServers_, sreq, bankServer).run();
		}
	}
	
	private ServerRequest addHaltRequest(IRequestObject reqObject, Socket clientSocket) {
		HaltRequestObject hro = (HaltRequestObject) reqObject;
		HaltRequestB hreq = new HaltRequestB();
		ServerRequest sreq = new ServerRequest(processId_, clock_.getClockValue(), hreq);
		logger_.info(processId_ + " " + "CLNT-REQ" + " "
				+ System.currentTimeMillis() + " <" + sreq.getClockValue()
				+ ", " + sreq.getSourceProcessId() + "> " + RequestType.halt.name()
				+ " <" + "NONE" + ">");
		addClientRequest(sreq, clientSocket);
		return null;
	}

	// Adds new Balance Inquiry Request to Queue
	private ServerRequest addBalanceRequest(IRequestObject reqObject, Socket clientSocket) {
		BalanceRequestObject bro = (BalanceRequestObject) reqObject;
		BalanceRequestB breq = new BalanceRequestB(getFromStore(bro.accountID()));
		ServerRequest sreq = new ServerRequest(processId_, clock_.updateAndGetClockValue(), breq);
		logger_.info(processId_ + " " + "CLNT-REQ" + " "
				+ System.currentTimeMillis() + " <" + sreq.getClockValue()
				+ ", " + sreq.getSourceProcessId() + "> " + RequestType.newaccount.name()
				+ " <" + bro.accountID() + ">");
		addClientRequest(sreq, clientSocket);
		return sreq;
	}

	// Adds Balance Transfer Request to Queue
	private ServerRequest addTransferRequest(IRequestObject reqObject, Socket clientSocket) {
		TransferRequestObject tro = (TransferRequestObject) reqObject;
		TransferRequestB treq = new TransferRequestB(
				getFromStore(tro.sourceID()),
				getFromStore(tro.destinationID()), tro.amount());
		ServerRequest sreq = new ServerRequest(processId_, clock_.updateAndGetClockValue(), treq);
		logger_.info(processId_ + " " + "CLNT-REQ" + " "
				+ System.currentTimeMillis() + " <" + sreq.getClockValue()
				+ ", " + sreq.getSourceProcessId() + "> " + RequestType.newaccount.name()
				+ " <" + tro.sourceID() + ", " + tro.destinationID() + ", "
				+ tro.amount() + ">");
		addClientRequest(sreq, clientSocket);
		return sreq;
	}

	// Adds Money Withdraw Request to Queue
	private ServerRequest addWithdrawRequest(IRequestObject reqObject, Socket clientSocket) {
		WithdrawRequestObject wro = (WithdrawRequestObject) reqObject;
		WithdrawRequestB wreq = new WithdrawRequestB(getFromStore(wro.accountID()), wro.amount());
		ServerRequest sreq = new ServerRequest(processId_, clock_.updateAndGetClockValue(), wreq);
		logger_.info(processId_ + " " + "CLNT-REQ" + " "
				+ System.currentTimeMillis() + " <" + sreq.getClockValue()
				+ ", " + sreq.getSourceProcessId() + "> " + RequestType.newaccount.name()
				+ " <" + wro.accountID() + ", " + wro.amount() + ">");
		addClientRequest(sreq, clientSocket);
		return sreq;
	}

	// Adds money Deposit Request to Queue
	private ServerRequest addDepositRequest(IRequestObject reqObject, Socket clientSocket) {
		DepositRequestObject dro = (DepositRequestObject) reqObject;
		DepositRequestB dreq = new DepositRequestB(getFromStore(dro.accountID()), dro.amount());
		ServerRequest sreq = new ServerRequest(processId_, clock_.updateAndGetClockValue(), dreq);
		logger_.info(processId_ + " " + "CLNT-REQ" + " "
				+ System.currentTimeMillis() + " <" + sreq.getClockValue()
				+ ", " + sreq.getSourceProcessId() + "> " + RequestType.newaccount.name()
				+ " <" + dro.accountID() + ", " + dro.amount() + ">");
		addClientRequest(sreq, clientSocket);
		return sreq;
	}

	// Adds new Account Creation Request to Queue
	private ServerRequest addNewAccountRequest(IRequestObject reqObject, Socket clientSocket) {
		NewAccountRequestObject aro = (NewAccountRequestObject) reqObject;
		NewAccountRequestB areq = new NewAccountRequestB(aro.firstName(), aro.lastName(), aro.address());
		ServerRequest sreq = new ServerRequest(processId_, clock_.updateAndGetClockValue(), areq);
		logger_.info(processId_ + " " + "CLNT-REQ" + " "
				+ System.currentTimeMillis() + " <" + sreq.getClockValue()
				+ ", " + sreq.getSourceProcessId() + "> " + RequestType.newaccount.name()
				+ " <" + aro.firstName() + ", " + aro.lastName() + ", "
				+ aro.address() + ">");
		addClientRequest(sreq, clientSocket);
		return sreq;
	}
	
	public static void main(String[] args) {

		if(args.length!=1){
            throw new RuntimeException("Enter the port number!");
        }

		//TODO: assuming id will be the 0th argument
		BankServer bankServer = new BankServer(Integer.parseInt(args[0]));
		new ClientRequestHandlingThread(clientReqPort_, bankServer);
		new ServerRequestHandlingThread(serverReqPort_, bankServer);
		new ServerJobExecuterThread(bankServer);
		
		//TODO: should wait here until we receive a halt message. should i use a join?
	}

	public void addServerRequest(ServerRequest req) {
		synchronized(reqLock_) {
			clock_.updateAndGetClockValue(req.getClockValue());
			requests_.add(req);
		}
	}

	public void addClientRequest(ServerRequest sreq, Socket clientSocket) {
		synchronized(reqLock_) {
			requests_.add(sreq);
			directRequestVsConnection_.put(sreq, clientSocket);
		}
	}

	public void updateAcksToServer(ServerRequest request, HashSet<String> acks) {
		reqVsAcks_.put(request, acks);
	}

}
