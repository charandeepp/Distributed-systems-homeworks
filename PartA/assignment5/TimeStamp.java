package assignment5;

import java.io.Serializable;

/**
 * Created by Charandeep on 3/31/15.
 */
public class TimeStamp implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long clk;
    private Integer processId;

    void updateClock(Long clk){
        this.clk = clk;
    }

    long getClock(){
        return this.clk;
    }

    void updateProcessId(Integer pId){
        this.processId = pId;
    }

    Integer getProcessId(){
        return this.processId;
    }

    int compareTimeStamps(TimeStamp t2){

        if(this.clk < t2.clk || ((this.clk==t2.clk)&&(this.processId<t2.processId))){
            return 1;
        }
        else if(this.clk == t2.clk && this.processId == t2.processId)
            return 0;
        else
            return -1;
    }
    
    @Override
    public boolean equals(Object obj) {
    	if(obj instanceof TimeStamp) {
    		if(this.clk.equals(((TimeStamp)obj).clk) && this.processId.equals(((TimeStamp)obj).processId)) {
	            return true;
    		}
    	}
    	return false;
    	
    }
    
    @Override
    public int hashCode() {
    	return clk.hashCode() ^ processId.hashCode();
    }

}
