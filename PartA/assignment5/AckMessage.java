package assignment5;

/**
 * Created by Charandeep on 3/31/15.
 */
public class AckMessage extends PeerMsgType{

	private static final long serialVersionUID = 1L;
	
	private Integer processId;
    private long clk;
    private TimeStamp tsp;

    AckMessage(){
        super(2);
    }

    void updateAckProcessId(int pid){
        this.processId = pid;
    }

    void updateAckClk(long clk){
        this.clk = clk;
    }

    void updateOriginTimeStamp(TimeStamp ts)
    {
        this.tsp = ts;
    }

    int getAckProcessId(){
        return this.processId;
    }

    long getAckClk(){
        return this.clk;
    }

    TimeStamp getOriginTimeStamp(){
        return tsp;
    }
}
