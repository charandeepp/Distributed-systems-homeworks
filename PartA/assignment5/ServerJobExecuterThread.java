package assignment5;

/**
 * This Thread continuously scans the messages in the Queue and executes the one
 * which is ready.
 * 
 * @author rkandur
 *
 */
public class ServerJobExecuterThread extends Thread {

	BankServer bankServer_;
	
	public ServerJobExecuterThread(BankServer bankServer) {
		bankServer_ = bankServer;
	}
	
	@Override
	public void run() {
		while (true) {
			bankServer_.execute();
		}
	}
	
}
