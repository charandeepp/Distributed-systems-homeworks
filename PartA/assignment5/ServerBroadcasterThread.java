package assignment5;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Thread which broadcasts to all other servers whenever a client makes a new
 * request to this server
 * 
 * @author rkandur
 *
 */
public class ServerBroadcasterThread extends Thread {

    private ServerRequest request_;
    private HashMap<String, Integer> servers_;
    private BankServer bankServer_;
    HashSet<String> acks;

    private Integer acksLock_;

    public ServerBroadcasterThread(HashMap<String, Integer> peerServers, ServerRequest req, BankServer bankServer) {
        servers_ = peerServers;
        request_ = req;
        bankServer_ = bankServer;
    }

    @Override
    public void run() {

        // TODO: creating a new connection for each message to be communicated.
        // should change this to create a connection once and use them till the end.

        acks = new HashSet<String>();
        ArrayList<HelperThread> HelperThreadsList = new ArrayList<HelperThread>();

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

            // ack message from the server
            acks.add(new ObjectInputStream(selfSocket.getInputStream()).readObject().toString());
            selfSocket.close();
        }

        //end
        catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        for (String host : servers_.keySet()) {
            try {

                //broadcast this request to all other servers
                Socket peerSocket = new Socket(host, servers_.get(host));

                HelperThread mHelper = new HelperThread(peerSocket, sendingReq);
                mHelper.start();
                HelperThreadsList.add(mHelper);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
            // update the ack messages to the BankServer, this will be used
            // to validate if a message on the top of the queue in server
            // should be executed or not

        Iterator mIterator = HelperThreadsList.iterator();
        while(mIterator.hasNext()) {
            try {
                ((HelperThread) mIterator.next()).join();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        synchronized (bankServer_.reqLock_) {
            bankServer_.clock_.updateAndGetClockValue();
            bankServer_.updateAcksToServer(request_, acks);
        }
    }

        private class HelperThread extends Thread {
            private Socket peerSocket;
            private ServerRequest sendingReq;

            HelperThread(Socket socket, ServerRequest sendingReq) {
                this.peerSocket = socket;
                this.sendingReq = sendingReq;
            }

            @Override
            public void run() {
                try {
                    new ObjectOutputStream(this.peerSocket.getOutputStream()).writeObject(sendingReq);
                    // ack message from each peer servers
                    synchronized (acksLock_) {
                        acks.add(new ObjectInputStream(this.peerSocket.getInputStream()).readObject().toString());
                    }
                    this.peerSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }


        }

    }
}
