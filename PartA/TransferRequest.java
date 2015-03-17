/**
 * Created by Charandeep
 *
 * An object of this class is sent to the server for making a transfer request
 *
 */
public class TransferRequest extends Request {

    Integer sAccId;
    Integer tAccId;
    Integer num;
    TransferRequest(Integer sAccId, Integer tAccId, Integer num){
        super("Transfer");
        this.sAccId = sAccId;
        this.tAccId = tAccId;
        this.num = num;

    }
}
