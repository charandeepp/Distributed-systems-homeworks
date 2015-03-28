package assignment5;

/**
 * {@link IRequestObject} for Deposit requests. Holds the key and value pairs of
 * all the fields which are necessary to make a {@link DepositRequest}
 * 
 * @author rkandur
 *
 */
public class DepositRequestObject extends AbstractRequestObject {

	private static final long serialVersionUID = 1L;

	public enum Keys {
		accountID,
		amount;
	}
	
	public DepositRequestObject(Arguments args) {
		this(RequestType.deposit, args);
	}
	
	public DepositRequestObject(RequestType rt, Arguments args) {
		super(rt, args);
	}
	
	public Integer accountID() {
		return Integer.parseInt(args_.getValueFor(Keys.accountID.name()));
	}
	
	public Integer amount() {
		return Integer.parseInt(args_.getValueFor(Keys.amount.name()));
	}

}
