package assignment5;

/**
 * Created by Charandeep on 3/31/15.
 */
public class TimeStamp {

    private long clk;
    private Integer processId;

    void updateClock(long clk){
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

}
