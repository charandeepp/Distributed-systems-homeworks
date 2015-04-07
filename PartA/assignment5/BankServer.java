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
import java.util.LinkedHashMap;
import java.util.PriorityQueue;
import java.util.logging.Logger;

/** CSci5105 Spring 2015
 * Assignment# 5
 * name: <Ravali Kandur>, <Charandeep Parisineti>
 * student id: <5084769>, <5103173>
 * x500 id: <id1>, <id2 (optional)>
 * CSELABS machine: <kh1262-08.cselabs.umn.edu, kh1262-09.cselabs.umn.edu, 
 * 					kh1262-10.cselabs.umn.edu, kh1262-11.cselabs.umn.edu>
 */

/**
 * class which has the Implementation details of an Bank Server
 * 
 * @author rkandur
 *
 */
public class BankServer {
	
	private static String CONFIG_FILE_PATH = "server.cfg";
	
	private ObjectOutputStream clientouts = null;

	// store which maintains all the accounts for this bank
	private static Hashtable<Integer, Account> accountsStore_ =  new Hashtable<Integer, Account>();
 
	public LamportClock clock_ = new LamportClock();
	
	// map of peer server host and port on which this server can communicate
	public HashMap<String, Integer> peerServers_ = new HashMap<String, Integer>();
	
	// map to hold the requests which are made by client directly to this server
	// and hold a socket object on which response needs to be sent back
	public LinkedHashMap<ServerRequest, Socket> directRequestVsConnection_ = new LinkedHashMap<ServerRequest, Socket>();
	
	// map to hold the direct requests vs the Acknowledgements made by the other servers
	public HashMap<ServerRequest, HashSet<String>> reqVsAcks_ = new HashMap<ServerRequest, HashSet<String>>();

    public HashMap<TimeStamp,HashSet<Integer>> ackSet = new HashMap<TimeStamp,HashSet<Integer>>();

    public Integer ackLock = 3;
    
    public int numberOfServers_ = 0;
    
	Logger logger_;
	
	long performanceTime_ = 0;
	
    public static String hostName_;
	public static int processId_;
	public static int serverReqPort_;
	public static int clientReqPort_;
	
	// lock variable to maintain the local queue in state machine model
	public Integer reqLock_ = 1;

    public Integer clientDSLock_ = 2;

	// queue which holds all the yet to be executed requests
	public PriorityQueue<ServerRequest> requests_ = new PriorityQueue<ServerRequest>(10,new Comparator<ServerRequest>() {
		@Override
		public int compare(ServerRequest r1, ServerRequest r2) {
			// prioritize based on lamport clock values as per total ordering rules in StateMachineModel
			if(r1.getOriginClockValue() == r2.getOriginClockValue()) {
				return r1.getSourceProcessId() - r2.getSourceProcessId() > 0 ? 1 : -1;
			}
			return (int) (r1.getOriginClockValue() - r2.getOriginClockValue()) > 0 ? 1 : -1;
		}
	});
	
	public BankServer(int procId) {
        processId_ = procId;
        logger_ = ServerLogger.logger(processId_);
        init();
        loadFromConfig();
	}
	
	private void init() {

		Integer account = 1;
		for(; account <= 10; ++account) {
			Account a = new Account(account, 1000, account.toString()+"F", account.toString()+"L", account.toString()+"A");
			updateStore(a);
		}
		logger_.info("Initialized all accounts, Ready to receive client requests ...");
	}

	void execute() {
	
		synchronized (reqLock_) {
			if(requests_.isEmpty()) {
				return;
			}
			if(!okToProceed(requests_.peek())) {
				return;
			}
			ServerRequest r = requests_.poll();
			ResponseObject resp = serveRequest(r.getRequest());
			performanceTime_ += System.currentTimeMillis();
			logger_.info(processId_ + " " + "PROCESS" + " " + System.currentTimeMillis() + " " + r.getClockValue());
			// if it is a direct request, we also need to send a response back to the client
			synchronized (clientDSLock_) {
				if(directRequestVsConnection_.containsKey(r)) {
					try {
						Socket cs = directRequestVsConnection_.remove(r);
						if(clientouts == null || r.getRequest() instanceof HaltRequestB){
							clientouts = new ObjectOutputStream(cs.getOutputStream());
						}
						clientouts.writeObject(resp);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
			if(r.getRequest() instanceof HaltRequestB) {
				// we should exit when we encounter a HALT request
				System.exit(0);
			}
		}
	}
	
	// this is to make sure that we have received all the acknowledgements.
	private boolean okToProceed(ServerRequest req) {
        synchronized (ackLock) {
	       	if(ackSet.containsKey(req.getTimeStamp())) {
		            if (ackSet.get(req.getTimeStamp()).size() == numberOfServers_) {
		                return true;
		            }
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
                        hostName_ = toks[0].trim();
					} else {
						peerServers_.put(toks[0].trim(), Integer.parseInt(toks[3].trim()));
					}
					numberOfServers_++;
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
		logger_.info(RequestType.halt.name() + " -> " + response.getResponse());

		//print all account balances
		logger_.info("Account Balances ... ");
		int cumBal = 0;
		for(Integer a : accountsStore_.keySet()) {
			logger_.info("Account { " + a + " } has balance = { " + accountsStore_.get(a).getBalance() + " }");
			cumBal += accountsStore_.get(a).getBalance();
		}
		System.out.println("Cumulative Balance = " + cumBal);
		
		// print all pending requests
		logger_.info("Pending Requests ... ");
		while(!requests_.isEmpty()) {
			ServerRequest r = requests_.poll();
		}
		
		// print performance data
		performanceTime_ += System.currentTimeMillis();
		logger_.info("Performance Measurement Data ... ");
		Long timeTaken = performanceTime_/(numberOfServers_*100+1);
		logger_.info("Time taken to process the request when number of servers = { "
				+ numberOfServers_ + " } is " + timeTaken.toString() + " milliseconds");
		
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
		return sreq;
	}

	// Adds new Balance Inquiry Request to Queue
	private ServerRequest addBalanceRequest(IRequestObject reqObject, Socket clientSocket) {
		BalanceRequestObject bro = (BalanceRequestObject) reqObject;
		BalanceRequestB breq = new BalanceRequestB(getFromStore(bro.accountID()));
		ServerRequest sreq = new ServerRequest(processId_, clock_.updateAndGetClockValue(), breq);
		logger_.info(processId_ + " " + "CLNT-REQ" + " "
				+ System.currentTimeMillis() + " <" + sreq.getClockValue()
				+ ", " + sreq.getSourceProcessId() + "> " + RequestType.balance.name()
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
				+ ", " + sreq.getSourceProcessId() + "> " + RequestType.transfer.name()
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
				+ ", " + sreq.getSourceProcessId() + "> " + RequestType.withdraw.name()
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
				+ ", " + sreq.getSourceProcessId() + "> " + RequestType.deposit.name()
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
            throw new RuntimeException("Enter the process id!");
        }

		BankServer bankServer = new BankServer(Integer.parseInt(args[0]));
		ClientRequestHandlingThread t1 = new ClientRequestHandlingThread(clientReqPort_, bankServer);
		ServerRequestHandlingThread t2 = new ServerRequestHandlingThread(serverReqPort_, bankServer);
		ServerJobExecuterThread t3 = new ServerJobExecuterThread(bankServer);
		
		t1.start();
		t2.start();
		t3.start();
		
		try {
			t1.join();
			t2.join();
			t3.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

	public void addServerRequest(ServerRequest req) {
		synchronized(reqLock_) {
			clock_.updateAndGetClockValue(req.getClockValue());
			requests_.add(req);
		}
	}

	public void addClientRequest(ServerRequest sreq, Socket clientSocket) {
		synchronized(reqLock_) {
			directRequestVsConnection_.put(sreq, clientSocket);
		}
	}

	public void updateAcksToServer(ServerRequest request, HashSet<String> acks) {
		reqVsAcks_.put(request, acks);
	}

}
