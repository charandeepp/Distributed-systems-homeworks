package assignment5;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by Charandeep on 3/31/15.
 */
public class ServerRequestReceiverThread extends Thread {

    private ServerRequest req;
    private BankServer bankServer_;
    ServerRequestReceiverThread(ServerRequest servRequest,BankServer bankServer_){
        this.req = servRequest;
        this.bankServer_ = bankServer_;
    }

    @Override
    public void run() {
        try {
            ServerRequest clientReq;

            //add the request received to the current local queue which will be executed as per the rules
            // framed by StateMachineModel
            bankServer_.addServerRequest(req);
            if (req.getSourceProcessId() == bankServer_.processId_ || bankServer_.directRequestVsConnection_.isEmpty()) {

            }
            else {
                while (true) {
                    synchronized (bankServer_.clientDSLock_) {
                        clientReq = (ServerRequest) bankServer_.directRequestVsConnection_.entrySet().iterator().next().getKey();
                        System.out.println(clientReq.ts_.getClock()+"_"+clientReq.ts_.getProcessId());
                        System.out.println(req.ts_.getClock()+"_"+req.ts_.getProcessId());
                        if (req.ts_.compareTimeStamps(clientReq.ts_) == 1) {
                            break;
                        }
                    }
                }
            }

            // NOTE: we are using processID+clockValue+"success" to indicate an ACK message
            synchronized (bankServer_.reqLock_) {
                long newClkValue = bankServer_.clock_.updateAndGetClockValue();
                AckMessage ack_ = new AckMessage();
                ack_.updateAckClk(newClkValue);
                ack_.updateAckProcessId(bankServer_.processId_);
                ack_.updateOriginTimeStamp(req.ts_);

                Socket selfSocket = new Socket(bankServer_.hostName_, bankServer_.serverReqPort_);
                new ObjectOutputStream(selfSocket.getOutputStream()).writeObject(ack_);
                selfSocket.close();

                for (String host : bankServer_.peerServers_.keySet()) {
                		System.out.println("Sending ack to " + host + ack_.getAckClk()+"_"+ack_.getAckProcessId());
                        //broadcast this ack to all other servers
                        Socket peerSocket = new Socket(host, bankServer_.peerServers_.get(host));
                        new ObjectOutputStream(peerSocket.getOutputStream()).writeObject(ack_);
                        peerSocket.close();

                }
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }

    }
}
