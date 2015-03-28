package assignment5;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

		// TODO: intentionally doing this sequentially, must check if this needs to be made multi-threaded?
		//TODO: can we save the connections rather than making a new connection per communication?
        
		while(true){
            try {
            	
            	// infinitely accepts new requests for the other servers
				Socket socket = socket_.accept();
                ServerRequest req = (ServerRequest) new ObjectInputStream(socket.getInputStream()).readObject();
                ObjectOutputStream outs = new ObjectOutputStream(socket.getOutputStream());
				
                //add the request received to the current local queue which will be executed as per the rules
                // framed by StateMachineModel
                bankServer_.addServerRequest(req);

                // NOTE: we are using processID+clockValue+"success" to indicate an ACK message
				outs.writeObject("ack_" + req.getSourceProcessId() + req.getClockValue());
				socket.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
            
        }
		
	}
	
}
