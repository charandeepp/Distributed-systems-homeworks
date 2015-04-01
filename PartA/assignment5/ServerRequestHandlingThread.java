package assignment5;

import com.sun.corba.se.spi.activation.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Time;


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
                while (true) {
                    PeerMsgType req = req = (PeerMsgType) new ObjectInputStream(this.socket_.getInputStream()).readObject();
                    switch(req.peer_msg_type){
                        case 1:
                            ServerRequest servRequest = (ServerRequest)req;
                            new ServerRequestReceiverThread(servRequest,bankServer_).start();
                        case 2:
                            AckMessage ack_ = (AckMessage)req;
                            AckReceiverThread ackThread = new AckReceiverThread(bankServer_,ack_);
                            ackThread.start();
                            ackThread.join();
                    }
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }
            catch(ClassNotFoundException e){
                e.printStackTrace();
            }
            catch(InterruptedException e){
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
