/**
 * Created by Charandeep on 2/11/15.
 *
 * An object of this class is sent by the server in response to get Balance request.
 */
public class GetBalanceResponse extends Response{

    Integer bal;
    GetBalanceResponse(Integer num){
        super("GetBalance");
        this.bal = num;
    }
}
