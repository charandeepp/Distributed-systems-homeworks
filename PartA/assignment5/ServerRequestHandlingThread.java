package assignment5;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * This will be responsible for the server peer communication/activity
 * 
 * This thread handles all the requests that are shared by the other peer
 * servers whenever some client contacts them.
 * 
 * @author rkandur
 *
 */
public class ServerRequestHandlingThread extends Thread {

	private ServerSocket socket_;	// socket for handling server communication
	private BankServer bankServer_;		// bankServerImplementation
	private int port_;					// port on which this thread creates a socket and listens for requests from other servers
	
	public ServerRequestHandlingThread(int port, BankServer server) {
		port_ = port;
		bankServer_ = server;
		try {
			socket_ = new ServerSocket(port_);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		while(true){
            try {
             	// infinitely accepts new requests for the other servers
				Socket socket = socket_.accept();
                new MyHelperThread(socket,this.bankServer_).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}

    private class MyHelperThread extends Thread{

        BankServer bankServer_;
        Socket socket_;
        MyHelperThread(Socket socket, BankServer bankServer){
            this.socket_ = socket;
            this.bankServer_ = bankServer;
        }

        @Override
        public void run() {
            try {
            
                PeerMsgType req = (PeerMsgType) new ObjectInputStream(this.socket_.getInputStream()).readObject();
                switch(req.peer_msg_type){
                    case 1:
                        synchronized (bankServer_.timeLock) {
                            bankServer_.performanceTime_ -= System.currentTimeMillis();
                        }
                    	ServerRequest servRequest = (ServerRequest)req;
                        String type = new String();
                        if(servRequest.getRequest() instanceof NewAccountRequestB) {
                			type = RequestType.newaccount.name();
                		} else if(servRequest.getRequest() instanceof DepositRequestB) {
                			type = RequestType.deposit.name();
                		} else if(servRequest.getRequest() instanceof WithdrawRequestB) {
                			type = RequestType.withdraw.name();
                		} else if(servRequest.getRequest() instanceof TransferRequestB) {
                			type = RequestType.transfer.name();
                		} else if(servRequest.getRequest() instanceof BalanceRequestB) {
                			type = RequestType.balance.name();
                		} else if(servRequest.getRequest() instanceof HaltRequestB) {
                			type = RequestType.halt.name();
                		}
                        
					bankServer_.logger_.info("SRV-REQ" + " "
							+ System.currentTimeMillis() + " <"
							+ servRequest.getClockValue() + ", "
							+ servRequest.getSourceProcessId() + "> " + type
							+ " " + servRequest.getRequest().arguments());
					
                        new ServerRequestReceiverThread(servRequest,bankServer_).start();
                        break;
                    case 2:
                        AckMessage ack_ = (AckMessage)req;
                        new AckReceiverThread(bankServer_,ack_).start();
                        break;
                
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }
            catch(ClassNotFoundException e){
                e.printStackTrace();
            }
            finally {
                try {
                    socket_.close();
                }
                catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
	
}
