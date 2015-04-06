package assignment5;

import java.util.HashSet;

/**
 * Created by Charandeep on 3/31/15.
 */
public class AckReceiverThread extends Thread {

    AckMessage ack_;
    BankServer bankServer_;

    AckReceiverThread(BankServer bankServer,AckMessage ack){
        this.bankServer_ = bankServer;
        this.ack_ = ack;
    }

    @Override
    public void run() {
        TimeStamp originTimeStamp = ack_.getOriginTimeStamp();
        HashSet<Integer> pIds;
        synchronized (bankServer_.ackLock) {
            pIds = bankServer_.ackSet.get(originTimeStamp);
            pIds.add(ack_.getAckProcessId());
            bankServer_.ackSet.put(originTimeStamp, pIds);
        }
    }

}
