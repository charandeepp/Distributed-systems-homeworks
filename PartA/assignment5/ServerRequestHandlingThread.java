package assignment5;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;



/**
 * This will be responsible for the server peer communication/activity
 * 
 * @author rkandur
 *
 */
public class ServerRequestHandlingThread extends Thread {

	private ServerSocket peerSocket_;
	private BankServer bankServer_;
	private int port_;
	
	public ServerRequestHandlingThread(int port, BankServer server) {
		port_ = port;
		bankServer_ = server;
		try {
			peerSocket_ = new ServerSocket(port_);
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
				Socket socket = peerSocket_.accept();
                ServerRequest req = (ServerRequest) new ObjectInputStream(socket.getInputStream()).readObject();
                ObjectOutputStream outs = new ObjectOutputStream(socket.getOutputStream());
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
