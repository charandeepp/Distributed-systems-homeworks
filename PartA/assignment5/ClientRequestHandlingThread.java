package assignment5;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This will be responsible for handling client requests
 * 
 * @author rkandur
 *
 */
public class ClientRequestHandlingThread extends Thread {

	private int port_;
	private ServerSocket serverSocket_;
	private BankServer bankServer_;
	
	public ClientRequestHandlingThread(int port, BankServer bankServer) {
		port_ = port;
		bankServer_ = bankServer;
		try {
			serverSocket_ = new ServerSocket(port_);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			while(true) {
				Socket clientSocket = serverSocket_.accept();
				IRequestObject reqObj = (IRequestObject) new ObjectInputStream(clientSocket.getInputStream()).readObject();
		        bankServer_.addNewRequest(reqObj, clientSocket, bankServer_);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
