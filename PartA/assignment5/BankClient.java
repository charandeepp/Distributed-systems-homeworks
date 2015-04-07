package assignment5;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;


/**
 * client to the {@link BankServer} which performs some transactions as
 * mentioned below on some of the accounts
 * 
 * 
 * This is a multi-threaded client where number of threads will be equal to the
 * number of server processes. Each client thread will be dedicated to
 * communicate with exactly one of the server processes.
 * 
 * @author rkandur
 *
 */
public class BankClient {
	
	private static String CONFIG_FILE_PATH = "client.cfg";
	private static Logger logger_ = ClientLogger.logger();

	// processID vs <hostname, port>
	private static HashMap<Integer, HashMap<String, Integer>> serverIdVsHostPort_ = new HashMap<Integer, HashMap<String, Integer>>();
	
	/**
	 * Helper method to pick up initial config information from cfg file
	 */
	private static void loadFromConfig() {
		try {
			File file = new File(CONFIG_FILE_PATH);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				if (!line.startsWith("#")) {
					String[] toks = line.split(" ");
					if (toks.length != 3) {
						continue;
					}
                    HashMap<String,Integer> hostPortMap = new HashMap<String, Integer>();
                    hostPortMap.put(toks[0].trim(), Integer.parseInt(toks[2].trim()));
					serverIdVsHostPort_.put(Integer.parseInt(toks[1].trim()), hostPortMap);
				}
			}
			bufferedReader.close();
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		CONFIG_FILE_PATH = args[0].trim();
		loadFromConfig();
		
		HashSet<BankClientThread> thGroup = new HashSet<BankClientThread>();
		
		// create client threads which are equal to the number of server processes
		for(Integer pid : serverIdVsHostPort_.keySet()) {
			HashMap<String, Integer> hp = serverIdVsHostPort_.get(pid);
			BankClientThread t = new BankClientThread(pid, hp.entrySet().iterator().next().getKey(), hp.entrySet().iterator().next().getValue(), logger_);
			t.start();
			thGroup.add(t);
		}
		
		// join on all threads
		for(BankClientThread t : thGroup) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//send HALT messsage to server with processID = 0
		try {
            HashMap<String, Integer> hp = serverIdVsHostPort_.get(0);
            for(Integer i : serverIdVsHostPort_.keySet()) {
            	System.out.println("i" + i);
            	for(String s : serverIdVsHostPort_.get(i).keySet()) {
            		System.out.println(s);
            	}
            	System.out.println("");
            }
			Socket socket = new Socket(hp.entrySet().iterator().next().getKey(), hp.entrySet().iterator().next().getValue());
			ObjectOutputStream outs = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream ins = new ObjectInputStream(socket.getInputStream());
			
			HaltRequestObject reqObject = new HaltRequestObject();
			logger_.info("0" + " " + " REQ " + System.currentTimeMillis() + " " + RequestType.halt.name());
			outs.writeObject(reqObject);
			
			ResponseObject reqResponse = (ResponseObject) ins.readObject();
			logger_.info("0" + " " + " RSP " + System.currentTimeMillis() + " " + reqResponse.getResponse());
			
			socket.close();
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
}
