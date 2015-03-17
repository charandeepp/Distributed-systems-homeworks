/* CSci5105 Spring 2015
* Assignment# 2
* name: Charandeep Parisineti, Ravali Kandur
* student id: 5103173, 5084769
* x500 id: paris102, kandu009
* CSELABS machine: kh1262-10.cselabs.umn.edu
*/

/**
 * Created by Charandeep on 2/11/15.
 *
 * Person's information record contains
 * Class Account:
 * accountID: Integer
 * balance: Integer
 * firstname: String
 * lastname: String
 * address: String
 *
 * NewAccount:
 * in parameter: firstname, lastname, address
 * out parameter: accountID for the new account
 *
 * Deposit:
 * in parameters: accountID, and amount
 * out parameter: status
 *
 * Withdraw:
 * in parameters: accountID, and amount
 * out parameter: status (OK or Fail)
 *
 * GetBalance:
 * in parameters: accountID
 * out parameter: Integer
 *
 * Transfer:
 * in parameters: accountID for source, account ID for target, positive integer amount
 * out parameter: status (OK or Fail)
 *
 * The implementation of first client
 *
 */

import java.io.*;
import java.net.*;
import java.util.*;

public class ClientA {
    static ObjectOutputStream outs;
    static ObjectInputStream ins;


    static String hostname;
    static Integer port;

    public static void main(String[] args) throws IOException{

        if(args.length!=2){
            throw new RuntimeException("hostname and port as arguments");
        }

        hostname = args[0];
        port = Integer.parseInt(args[1]);

        Integer acc1 = newAccountRequest("Charandeep","Parisineti","600 10th Avenue SE");

        Integer acc2 = newAccountRequest("Ravali","Kandur","blah blah!");

        deposit(acc1);
        deposit(acc2);

        getBalance(acc1);
        getBalance(acc2);

        transfer(acc1,acc2);

        getBalance(acc1);
        getBalance(acc2);

        withdraw(acc1);
        withdraw(acc2);

        transfer(acc1,acc2);

        getBalance(acc1);
        getBalance(acc2);

    }


    public static int newAccountRequest(String firstname,String lastname,String address) throws IOException{

        NewAccountRequest nr = new NewAccountRequest(firstname, lastname, address);

        Socket socket = new Socket(hostname,port);

        OutputStream rawOut = socket.getOutputStream ();
        outs = new ObjectOutputStream(rawOut);

        outs.writeObject(nr);

        InputStream rawIn = socket.getInputStream ();
        ins = new ObjectInputStream(rawIn);
        NewAccountResponse nrs =null;
        try {
            nrs = (NewAccountResponse)ins.readObject();
        }
        catch(ClassNotFoundException cnf){
            cnf.printStackTrace();
        }
        finally {
            socket.close();
        }
        if(nrs!=null){
            System.out.println("Your account number is "+nrs.accId+"\n");
        }
        else{
            System.out.println("Account creation failed\n");
        }
        return nrs.accId;
    }

    public static void getBalance(Integer accId) throws IOException{
        GetBalanceRequest gb = new GetBalanceRequest(accId);

        Socket socket = new Socket(hostname,port);

        OutputStream rawOut = socket.getOutputStream ();
        outs = new ObjectOutputStream(rawOut);

        outs.writeObject(gb);

        InputStream rawIn = socket.getInputStream ();
        ins = new ObjectInputStream(rawIn);

        GetBalanceResponse gbs = null;
        try {
            gbs = (GetBalanceResponse)ins.readObject();
        }
        catch (ClassNotFoundException cnf){
            cnf.printStackTrace();
        }
        finally {
            socket.close();
        }
        System.out.println("Your balance is "+gbs.bal+"\n");
    }

    public static void transfer(Integer sAccId, Integer tAccId) throws IOException{

        Socket socket = new Socket(hostname,port);

        OutputStream rawOut = socket.getOutputStream ();
        outs = new ObjectOutputStream(rawOut);
        TransferRequest tr = new TransferRequest(sAccId,tAccId,100);
        outs.writeObject(tr);

        InputStream rawIn = socket.getInputStream ();
        ins = new ObjectInputStream(rawIn);
        TransferResponse ts = null;
        try{
            ts = (TransferResponse)ins.readObject();
        }
        catch (ClassNotFoundException cnf){
            cnf.printStackTrace();
        }
        finally {
            socket.close();
        }
        System.out.println("Transfer status is " + ts.get_status() + "\n");
    }

    public static void deposit(Integer accId) throws IOException{
        DepositRequest dr = new DepositRequest(accId,100);

        Socket socket = new Socket(hostname,port);

        OutputStream rawOut = socket.getOutputStream ();
        outs = new ObjectOutputStream(rawOut);
        outs.writeObject(dr);

        InputStream rawIn = socket.getInputStream ();
        ins = new ObjectInputStream(rawIn);
        DepositResponse drs = null;
        try {
            drs = (DepositResponse)ins.readObject();
        }
        catch (ClassNotFoundException cnf){
            cnf.printStackTrace();
        }
        finally {
            socket.close();
        }
        System.out.println("Your deposit status is "+drs.get_status()+"\n");
    }

    public static void withdraw(Integer accId) throws IOException{

        Socket socket = new Socket(hostname,port);
        WithdrawRequest wr = new WithdrawRequest(accId,100);
        OutputStream rawOut = socket.getOutputStream ();
        outs = new ObjectOutputStream(rawOut);
        outs.writeObject(wr);

        InputStream rawIn = socket.getInputStream ();
        ins = new ObjectInputStream(rawIn);
        WithdrawResponse wrs = null;
        try {
            wrs = (WithdrawResponse)ins.readObject();
        }
        catch (ClassNotFoundException cnf){
            cnf.printStackTrace();
        }
        finally {
            socket.close();
        }
        System.out.println("Your withdraw status is "+wrs.get_status()+"\n");

    }
}
