package assignment5;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Thread which broadcasts to all other servers whenever a client makes a new
 * request to this server
 * 
 * @author rkandur
 *
 */
public class ServerBroadcasterThread extends Thread {

	private ServerRequest request_;
	private HashMap<String, Integer> servers_;
	private BankServer bankServer_;

	public ServerBroadcasterThread(HashMap<String, Integer> peerServers, ServerRequest req, BankServer bankServer) {
		servers_ = peerServers;
		request_ = req;
		bankServer_ = bankServer;
	}

	@Override
	public void run() {
		
		// TODO: creating a new connection for each message to be communicated.
		// should change this to create a connection once and use them till the end.
		
		HashSet<String> acks = new HashSet<String>();
		
		// NOTE: making a copy of the original ServerRequest method as we should
		// not modify the actual request which is already in the queue
		ServerRequest sendingReq = new ServerRequest(request_);

		// NOTE: this is to make sure that we are updating the time stamp in the
		// message that we are sending to other processes according to State
		// Machine Model rules
		long newClkValue = bankServer_.clock_.updateAndGetClockValue();
		sendingReq.setClockValue(newClkValue);
		
		for(String host : servers_.keySet()) {
			try {
				
				//broadcast this request to all other servers
				Socket peerSocket = new Socket(host, servers_.get(host));
				new ObjectOutputStream(peerSocket.getOutputStream()).writeObject(sendingReq);

				// ack message from each peer servers
				acks.add(new ObjectInputStream(peerSocket.getInputStream()).readObject().toString());
				peerSocket.close();
			
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		// update the ack messages to the BankServer, this will be used
		// to validate if a message on the top of the queue in server
		// should be executed or not		
		bankServer_.updateAcksToServer(request_, acks);
	
	}
	
}
