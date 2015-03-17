/**
 * Created by Charandeep
 *
 * An object of this class is sent by the server in response to a transfer request
 *
 */
public class TransferResponse extends Response {

    TransferResponse(Integer status){
        super("Transfer");
        super.update_status(status);
    }
}
