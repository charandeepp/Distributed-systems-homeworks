/**
 * Created by Charandeep
 *
 * An object of this class is sent by the server in response to new account creation request.
 */
public class NewAccountResponse extends Response {

    Integer accId;
    NewAccountResponse(Integer accId){
        super("NewAccount");
        this.accId = accId;
    }
}
