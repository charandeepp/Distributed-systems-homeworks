package assignment5;


import java.io.Serializable;
import java.util.HashMap;

/**
 * class which denotes the typical structure for the arguments passed between an
 * RMI Server and Client
 * 
 * @author rkandur
 *
 */
public class Arguments implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private HashMap<String, String> args_ = new HashMap<String, String>();
	
	public Arguments() {
		
	}
	
	public void addArgument(String key, String value) {
		args_.put(key, value);
	}
	
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		
		for(String key : args_.keySet()) {
			sb.append(key).append(":").append(args_.get(key)).append(",");
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append("}");
			
		return sb.toString();
	}
	
	public void parseArguments(String args) {
		
		String temp = args.trim().substring(args.indexOf('{')+1, args.lastIndexOf('}'));
		String[] toks = temp.split(",");
		for(int i = 0; i  < toks.length; ++i) {
			String[] ts = toks[i].split(":");
			if(ts.length == 2) { args_.put(ts[0], ts[1]); }
		}
		
	}
	
	public String getValueFor(String key) {
		return args_.get(key);
	}
	
}
