package assignment5;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Each unit of Client which connects to a specific server process 
 * 
 * @author rkandur
 *
 */
public class BankClientThread extends Thread {

	private static Integer MAX_ACCOUNT_NUMBER = 10;
	private static Integer NUMBER_OF_ITERATIONS = 100;
	
	Integer serverPId_;
	String serverHost_;
	Integer serverPort_;
	private Logger logger_;
	
	public BankClientThread(Integer pid, String host, Integer port, Logger logger) {
		serverPort_ = port;
		serverHost_ = host;
		serverPId_ = pid;
		logger_ = logger;
	}
	
	private Integer getRandomAccount() {
		return new Random().nextInt(MAX_ACCOUNT_NUMBER)+1;
	}
	
	@Override
	public void run() {

		Socket socket;
		ObjectOutputStream outs;
		ObjectInputStream ins;
		try {
			socket = new Socket(serverHost_, serverPort_);
			outs = new ObjectOutputStream(socket.getOutputStream());
			ins = null;
		} catch (Exception e1) {
			e1.printStackTrace();
			System.out.println("Exception *******************");
			return;
		}

		// do NUMBER_OF_ITERATIONS number of transfers from randomly chosen account numbers
		for(int i = 0; i < NUMBER_OF_ITERATIONS; ++i) {
			try {
				System.out.println("In  iteration " + i + "******************");
				Arguments reqArgs = new Arguments();
				reqArgs.addArgument(TransferRequestObject.Keys.sourceID.name(), getRandomAccount().toString());
				reqArgs.addArgument(TransferRequestObject.Keys.destinationID.name(), getRandomAccount().toString());
				reqArgs.addArgument(TransferRequestObject.Keys.amount.name(), new String("10"));
				
				TransferRequestObject reqObject = new TransferRequestObject(reqArgs);
				logger_.info(serverPId_ + " " + " REQ " + System.currentTimeMillis() + " " + RequestType.transfer.name() + " " + reqArgs.toString());
				System.out.println(serverPId_ + " " + " REQ " + System.currentTimeMillis() + " " + RequestType.transfer.name() + " " + reqArgs.toString());
				outs.writeObject(reqObject);
				System.out.println("Write successfull " + i + "*****************");

				if(ins == null) {
					ins = new ObjectInputStream(socket.getInputStream());
				}
				ResponseObject reqResponse = (ResponseObject) ins.readObject();
				logger_.info(serverPId_ + " " + " RSP " + System.currentTimeMillis() + " " + reqResponse.getResponse());
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
		}
        
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
	
}
