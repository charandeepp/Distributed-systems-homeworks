package assignment5;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashSet;

/**
 * Thread which broadcasts to all other servers whenever a client makes a new
 * request to this server
 * 
 * @author rkandur
 *
 */
public class ServerBroadcasterThread extends Thread {

    private ServerRequest request_;
    private HashSet<PeerServer> servers_;
    private BankServer bankServer_;

    public ServerBroadcasterThread(HashSet<PeerServer> peerServers, ServerRequest req, BankServer bankServer) {
        servers_ = peerServers;
        request_ = req;
        bankServer_ = bankServer;
    }

    @Override
    public void run() {

        // NOTE: making a copy of the original ServerRequest method as we should
        // not modify the actual request which is already in the queue
        ServerRequest sendingReq = new ServerRequest(request_);

        // NOTE: this is to make sure that we are updating the time stamp in the
        // message that we are sending to other processes according to State
        // Machine Model rules
        synchronized (bankServer_.reqLock_) {
            long newClkValue = bankServer_.clock_.updateAndGetClockValue();
            sendingReq.setClockValue(newClkValue);
        }

        //new start
        try {
            Socket selfSocket = new Socket(bankServer_.hostName_, bankServer_.serverReqPort_);
            new ObjectOutputStream(selfSocket.getOutputStream()).writeObject(sendingReq);
            selfSocket.close();
        }
        //end
        catch (IOException e) {
            e.printStackTrace();
        }

        for (PeerServer ps : servers_) {
            try {

                //broadcast this request to all other servers
                Socket peerSocket = new Socket(ps.getHost(), ps.getPort());
                new ObjectOutputStream(peerSocket.getOutputStream()).writeObject(sendingReq);
                peerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // update the ack messages to the BankServer, this will be used
        // to validate if a message on the top of the queue in server
        // should be executed or not
    }

}
