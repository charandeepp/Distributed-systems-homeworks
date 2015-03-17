/**
 * Created by Charandeep
 *
 *
 * An object of this kind is sent by the server in response to a withdraw request.
 *
 */
public class WithdrawResponse extends Response {

    WithdrawResponse(Integer status){
        super("Withdraw");
        super.update_status(status);
    }
}
