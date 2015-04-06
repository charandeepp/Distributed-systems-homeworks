package assignment5;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This will be responsible for accepting client requests and passing on to the BankServer
 * 
 * @author rkandur
 *
 */
public class ClientRequestHandlingThread extends Thread {

	private int port_;						//port on which this part of the server should run
	private ServerSocket socket_;			//socket for communicating with clients 
	private BankServer bankServer_;			//BankServer implementation
	
	public ClientRequestHandlingThread(int port, BankServer bankServer) {
		port_ = port;
		bankServer_ = bankServer;
		try {
			socket_ = new ServerSocket(port_);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {

			while(true) {
                Socket clientSocket = socket_.accept();
				// accept requests from clients for the entire lifetime?
                new ClientHandlerHelperThread(clientSocket,bankServer_).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    public class ClientHandlerHelperThread extends Thread{
        private Socket mSocket;
        private BankServer mBankServer;

        public ClientHandlerHelperThread(Socket socket, BankServer bankServer){
            this.mSocket = socket;
            this.mBankServer = bankServer;
        }

        @Override
        public void run() {
            try {
                IRequestObject reqObj = (IRequestObject) new ObjectInputStream(this.mSocket.getInputStream()).readObject();
                //add this request to the local queue to execute them as per StateMachineModel rules.
                this.mBankServer.addNewRequest(reqObj, this.mSocket, this.mBankServer);
            }
            catch(IOException e){
                e.printStackTrace();
            }
            catch(ClassNotFoundException e){
                e.printStackTrace();
            }
        }
    }
}
