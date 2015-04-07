package assignment5;

/**
 * Class which is used to denote a pair of host and port of the peer servers
 * 
 * @author rkandur
 *
 */
public class PeerServer {

	String host_;
	Integer port_;
	
	public PeerServer(String host, Integer port) {
		host_ = host;
		port_ = port;
	}

	public String getHost() {
		return host_;
	}
	
	public Integer getPort() {
		return port_;
	}
	
}
