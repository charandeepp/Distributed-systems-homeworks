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
public class ServerRequest extends PeerMsgType{

	private static final long serialVersionUID = 1L;

	public TimeStamp ts_ = new TimeStamp();
	private Long clockValue_;
	private IRequest request_;

	public ServerRequest(int procId, long clkVal, IRequest req) {
        super(1);
        System.out.println(procId);
        System.out.println(clkVal);
		setTimeStamp(clkVal,procId);
		setClockValue(clkVal);
		setRequest(req);
	}

	public ServerRequest(ServerRequest req) {
        super(1);
		this.ts_ = req.ts_;
		this.clockValue_ = req.clockValue_;
		this.request_ = req.request_;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ServerRequest) {
			return (((ServerRequest)obj).getTimeStamp()).equals(this.getTimeStamp());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.getTimeStamp().hashCode();
	}
	
	public int getSourceProcessId() {
		return ts_.getProcessId();
	}

	public void setTimeStamp(long clockValue_,int sourceProcessId_) {
		ts_.updateClock(clockValue_);
        ts_.updateProcessId(sourceProcessId_);
	}

	public long getClockValue() {
		return clockValue_;
	}

    public long getOriginClockValue(){
        return ts_.getClock();
    }

	public void setClockValue(long clockValue_) {
		this.clockValue_ = clockValue_;
	}

	public IRequest getRequest() {
		return request_;
	}

    public TimeStamp getTimeStamp(){
        return ts_;
    }

	public void setRequest(IRequest request_) {
		this.request_ = request_;
	}
	
}
