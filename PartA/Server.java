/* CSci5105 Spring 2015
* Assignment# 2
* name: Charandeep Parisineti, Ravali Kandur
* student id: 5103173, 5084769
* x500 id: paris102, kandu009
* CSELABS machine: kh1262-01.cselabs.umn.edu
*/



/**
 * Multi-threaded server implementation.
 * Takes the client requests and updates the account information.
 * Sends a response
 *
 * @author paris102
 */




import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

/**
 * Created by Charandeep on 2/11/15.
 */
public class Server{

    static Integer curr_Acc = 1;
    static ConcurrentHashMap acc_dB;
    static Logger logger;
    public static void main(String[] args) throws IOException,ClassNotFoundException{
        if(args.length!=1){
            throw new RuntimeException("Enter the port number!");
        }

        logger = Logger.getLogger("MyLog");
        FileHandler fh;

        try {

            // This block configure the logger with handler and formatter
            fh = new FileHandler("serverLogfile");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        acc_dB = new ConcurrentHashMap(200);
        Set acc_Num = new HashSet();
        System.out.println("Creating a Server on 1234...");
        ServerSocket bank = new ServerSocket(Integer.parseInt(args[0]));
        System.out.println("Server is waiting for the client...");
        try {
            while(true){
                new ServerThread(bank.accept()).start();
            }
        }
        finally{
            bank.close();
        }


    }

    private static class ServerThread extends Thread {
        private Socket socket;
        public ServerThread(Socket socket){
            this.socket = socket;
        }
        @Override
        public void run() {
            try{
                InputStream ins = socket.getInputStream();
                ObjectInputStream req = new ObjectInputStream(ins);
                Request r = (Request)req.readObject();
                OutputStream outs = socket.getOutputStream();
                ObjectOutputStream res = new ObjectOutputStream(outs);
                switch(r.req_type) {
                    case Request.NewAccount:
                        NewAccountRequest nr = (NewAccountRequest) r;
                        logger.info("NewAccount " + nr.firstname + " " + nr.lastname + " " + nr.address + " " + curr_Acc);
                        synchronized (this) {
                            AccountInfo na = new AccountInfo(curr_Acc, nr.firstname, nr.lastname, nr.address);
                            acc_dB.put(curr_Acc, na);
                            NewAccountResponse nrs = new NewAccountResponse(curr_Acc);
                            res.writeObject(nrs);
                            curr_Acc++;
                        }
                        break;
                    case Request.Deposit:
                        DepositRequest dr = (DepositRequest) r;
                        AccountInfo nb = (AccountInfo) acc_dB.get(dr.accId);
                        nb.updateBalance(dr.num);
                        acc_dB.put(dr.accId, nb);
                        DepositResponse drs = new DepositResponse(1);
                        res.writeObject(drs);
                        logger.info("Deposit "+dr.accId+" "+"100 "+drs.get_status());
                        break;
                    case Request.Withdraw:
                        WithdrawRequest wr = (WithdrawRequest) r;
                        AccountInfo nc = (AccountInfo) acc_dB.get(wr.accId);
                        WithdrawResponse wrs;
                        synchronized (this) {
                            if (nc.getBalance() < wr.num) {
                                wrs = new WithdrawResponse(0);
                            } else {
                                nc.updateBalance(-wr.num);
                                acc_dB.put(wr.accId, nc);
                                wrs = new WithdrawResponse(1);
                            }
                        }
                        res.writeObject(wrs);
                        logger.info("Withdraw "+wr.accId+" "+"100 "+wrs.get_status());
                        break;
                    case Request.GetBalance:
                        GetBalanceRequest gb = (GetBalanceRequest) r;
                        AccountInfo nd = (AccountInfo) acc_dB.get(gb.accId);
                        Integer bal = nd.getBalance();
                        GetBalanceResponse gbs = new GetBalanceResponse(bal);
                        res.writeObject(gbs);
                        logger.info("GetBalance "+gb.accId+" "+ bal);
                        break;

                    case Request.Transfer:
                        TransferRequest tr = (TransferRequest) r;
                        AccountInfo ne = (AccountInfo) acc_dB.get(tr.sAccId);
                        AccountInfo ne2 = (AccountInfo) acc_dB.get(tr.tAccId);
                        TransferResponse trs;
                        synchronized (this){
                            if (ne.getBalance() < tr.num) {
                                trs = new TransferResponse(0);
                            } else {
                                ne.updateBalance(-tr.num);
                                acc_dB.put(tr.sAccId, ne);
                                ne2.updateBalance(tr.num);
                                acc_dB.put(tr.tAccId, ne2);
                                trs = new TransferResponse(1);
                            }
                        }
                        res.writeObject(trs);
                        logger.info("Transfer "+tr.sAccId+" "+ tr.tAccId+" "+tr.num +" "+trs.get_status());
                        break;
                }
            }
            catch(ClassNotFoundException cne){
                cne.printStackTrace();
            }
            catch (IOException ioe){
                ioe.printStackTrace();
            }
            finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    //log("Couldn't close a socket, what's going on?");
                }
            }
        }
    }

}
