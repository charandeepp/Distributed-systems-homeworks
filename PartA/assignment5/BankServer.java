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
 * Assignment# 2
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
	
	private static String CONFIG_FILE_PATH = "serverconfig.txt";

	// store which maintains all the accounts for this bank
	private static Hashtable<Integer, Account> accountsStore_ =  new Hashtable<Integer, Account>();
 
	//TODO: should add logic to appropriately increment clock based on events happening
	public LamportClock clock_ = new LamportClock();
	private HashMap<String, Integer> peerServers_ = new HashMap<String, Integer>();
	private HashMap<ServerRequest, Socket> directRequestVsConnection_ = new HashMap<ServerRequest, Socket>();
	private HashMap<ServerRequest, HashSet<String>> reqVsAcks_ = new HashMap<ServerRequest, HashSet<String>>();

	//TODO:logging is yet to be added
	Logger logger_ = ServerLogger.logger();

	private static int processId_;
	private static int serverReqPort_;
	private static int clientReqPort_;
	
	// lock variable to maintain the local queue in state machine model
	private Integer reqLock_;
	
	private PriorityQueue<ServerRequest> requests_ = new PriorityQueue<ServerRequest>(new Comparator<ServerRequest>() {
		@Override
		public int compare(ServerRequest r1, ServerRequest r2) {
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
		if(!requests_.isEmpty()) {
			if(okToProceed(requests_.peek())) {
				//TODO: use reqVsAcks_ and make sure you have received all the acknowledgements before we execute this.
				ServerRequest r = requests_.poll();
				ResponseObject resp = serveRequest(r.getRequest());
				if(directRequestVsConnection_.containsKey(r)) {
					try {
						Socket cs = directRequestVsConnection_.remove(r);
						new ObjectOutputStream(cs.getOutputStream()).writeObject(resp);
						cs.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
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

	public ResponseObject serveRequest(IRequest req) {
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
		}
		return response;
	}
	
	// Handles new Balance Inquiry Request
	private ResponseObject serveBalanceRequest(IRequest breq) {
		RequestResponse response = (RequestResponse) breq.execute();
		logger_.info(RequestType.balance.name() + " -> " + response.getResponse());
		return new ResponseObject(response.getStatus(), response.getResponse());
	}

	// Handles Balance Transfer Request
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

	// Handles Money Withdraw Request
	private ResponseObject serveWithdrawRequest(IRequest wreq) {
		RequestResponse response = (RequestResponse) wreq.execute();
		if(response.getStatus()) {
			updateStore((Account)response.getResult());
		}
		logger_.info(RequestType.withdraw.name() + " -> " + response.getResponse());
		return new ResponseObject(response.getStatus(), response.getResponse());
	}

	// Handles money Deposit Request
	private ResponseObject serveDepositRequest(IRequest dreq) {
		RequestResponse response = (RequestResponse) dreq.execute();
		if(response.getStatus()) {
			updateStore((Account)response.getResult());
		}
		logger_.info(RequestType.deposit.name() + " -> " + response.getResponse());
		return new ResponseObject(response.getStatus(), response.getResponse());
	}

	// Handles new Account Creation Request
	private ResponseObject serveNewAccountRequest(IRequest areq) {
		RequestResponse response = (RequestResponse) areq.execute();
		if(response.getStatus()) {
			updateStore((Account)response.getResult());
		}
		//TODO: we need to modify logging as per new requirements. we should change the log formatter too !!!!
		logger_.info(RequestType.newaccount.name() + " -> " + response.getResponse());
		return new ResponseObject(response.getStatus(), response.getResponse());
	}
	
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
	
	// Adds new Balance Inquiry Request to Queue
	private ServerRequest addBalanceRequest(IRequestObject reqObject, Socket clientSocket) {
		BalanceRequestObject bro = (BalanceRequestObject) reqObject;
		BalanceRequestB breq = new BalanceRequestB(getFromStore(bro.accountID()));
		ServerRequest sreq = new ServerRequest(processId_, clock_.updateAndGetClockValue(), breq);
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
		addClientRequest(sreq, clientSocket);
		return sreq;
	}

	// Adds Money Withdraw Request to Queue
	private ServerRequest addWithdrawRequest(IRequestObject reqObject, Socket clientSocket) {
		WithdrawRequestObject wro = (WithdrawRequestObject) reqObject;
		WithdrawRequestB wreq = new WithdrawRequestB(getFromStore(wro.accountID()), wro.amount());
		ServerRequest sreq = new ServerRequest(processId_, clock_.updateAndGetClockValue(), wreq);
		addClientRequest(sreq, clientSocket);
		return sreq;
	}

	// Adds money Deposit Request to Queue
	private ServerRequest addDepositRequest(IRequestObject reqObject, Socket clientSocket) {
		DepositRequestObject dro = (DepositRequestObject) reqObject;
		DepositRequestB dreq = new DepositRequestB(getFromStore(dro.accountID()), dro.amount());
		ServerRequest sreq = new ServerRequest(processId_, clock_.updateAndGetClockValue(), dreq);
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
				+ ", " + processId_ + "> " + RequestType.newaccount.name()
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
