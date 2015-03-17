/**
 * Created by Charandeep
 *
 *
 * An object of this class is sent to the server for creating a new account.
 */
public class NewAccountRequest extends Request{

    String firstname;
    String lastname;
    String address;


    NewAccountRequest(String firstname, String lastname, String address){
        super("NewAccount");
        this.firstname = firstname;
        this.lastname = lastname;
        this.address = address;
    }
}
