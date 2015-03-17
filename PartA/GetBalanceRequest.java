/**
 * Created by Charandeep
 *
 * An object of this class is sent to server for balance request.
 *
 */
public class GetBalanceRequest extends Request {

    Integer accId;
    GetBalanceRequest(Integer accId){
        super("GetBalance");
        this.accId = accId;
    }
}
