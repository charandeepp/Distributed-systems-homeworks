/**
 * Created by Charandeep
 *
 * An object of this class is sent to the sever to make a withdraw request.
 *
 */
public class WithdrawRequest extends Request {

    Integer accId;
    Integer num;

    WithdrawRequest(Integer accId, Integer num){
        super("Withdraw");
        this.accId = accId;
        this.num = num;
    }
}
