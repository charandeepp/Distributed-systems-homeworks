/**
 * Created by Charandeep
 *
 * Implements serializable.
 * Base class to all the request types.
 *
 *
 */

import java.io.Serializable;
public class Request implements Serializable{

    public static final int NewAccount = 1;
    public static final int Deposit = 2;
    public static final int Withdraw = 3;
    public static final int GetBalance = 4;
    public static final int Transfer = 5;

    String req;
    Integer req_type;

    Request(String req){
        this.req = req;
        if(req == "NewAccount"){
            req_type =1;
        }
        else if(req == "Deposit"){
            req_type =2;
        }
        else if(req == "Withdraw"){
            req_type =3;
        }
        else if(req == "GetBalance"){
            req_type =4;
        }
        else if(req == "Transfer"){
            req_type =5;
        }
        else{
            req_type = -1;
        }
    }
}
