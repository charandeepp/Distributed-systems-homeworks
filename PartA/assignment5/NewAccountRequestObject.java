package assignment5;
/**
 * {@link IRequestObject} for New Account requests. Holds the key and value
 * pairs of all the fields which are necessary to make a
 * {@link NewAccountRequest}
 * 
 * @author rkandur
 *
 */
public class NewAccountRequestObject extends AbstractRequestObject {

	private static final long serialVersionUID = 1L;

	public enum Keys {
		firstname,
		lastname,
		address;
	}
	
	public NewAccountRequestObject(Arguments args) {
		this(RequestType.newaccount, args);
	}
	
	public NewAccountRequestObject(RequestType rt, Arguments args) {
		super(rt, args);
	}

	public String firstName() {
		return args_.getValueFor(Keys.firstname.name());
	}
	
	public String lastName() {
		return args_.getValueFor(Keys.lastname.name());
	}
	
	public String address() {
		return args_.getValueFor(Keys.address.name());
	}
	
	
}
