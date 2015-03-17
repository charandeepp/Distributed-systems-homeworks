/**
 * Created by Charandeep
 *
 * An object of this class is sent to the server for a deposit request.
 *
 */
public class DepositRequest extends Request {

    Integer accId;
    Integer num;
    DepositRequest(int accId, int num){
        super("Deposit");
        this.accId = accId;
        this.num = num;

    }

}
