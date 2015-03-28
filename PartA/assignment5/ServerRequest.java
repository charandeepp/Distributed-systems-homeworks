package assignment5;

/**
 * Class which is used to denote the internal structure which represents each
 * client request
 * 
 * It has the original client request -> {@link IRequest}, 
 * the clock value associated with the request, 
 * source process ID which has received the message from client.
 * 
 * @author rkandur
 *
 */
public class ServerRequest {

	private int sourceProcessId_;
	private long clockValue_;
	private IRequest request_;
	
	public ServerRequest(int procId, long clkVal, IRequest req) {
		setSourceProcessId(procId);
		setClockValue(clkVal);
		setRequest(req);
	}

	public ServerRequest(ServerRequest req) {
		this.sourceProcessId_ = req.sourceProcessId_;
		this.clockValue_ = req.clockValue_;
		this.request_ = req.request_;
	}

	public int getSourceProcessId() {
		return sourceProcessId_;
	}

	public void setSourceProcessId(int sourceProcessId_) {
		this.sourceProcessId_ = sourceProcessId_;
	}

	public long getClockValue() {
		return clockValue_;
	}

	public void setClockValue(long clockValue_) {
		this.clockValue_ = clockValue_;
	}

	public IRequest getRequest() {
		return request_;
	}

	public void setRequest(IRequest request_) {
		this.request_ = request_;
	}
	
}
