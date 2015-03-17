/**
 * Created by Charandeep
 *
 * Server sends an object of this class in response to a deposit request
 */
public class DepositResponse extends Response {

    DepositResponse(Integer status){
        super("Deposit");
        super.update_status(status);
    }
}
