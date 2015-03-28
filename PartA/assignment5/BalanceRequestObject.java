package assignment5;

/**
 * {@link IRequestObject} for Balance requests. Holds the key and value pairs
 * of all the fields which are necessary to make a {@link BalanceRequestB}
 * 
 * @author rkandur
 *
 */
public class BalanceRequestObject extends AbstractRequestObject {

	private static final long serialVersionUID = 1L;

	public enum Keys {
		accountID;
	}
	
	public BalanceRequestObject(Arguments args) {
		this(RequestType.balance, args);
	}
	
	public BalanceRequestObject(RequestType rt, Arguments args) {
		super(rt, args);
	}

	public Integer accountID() {
		return Integer.parseInt(args_.getValueFor(Keys.accountID.name()));
	}
	
}
