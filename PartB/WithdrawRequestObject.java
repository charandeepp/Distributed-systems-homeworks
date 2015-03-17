

/**
 * {@link IRequestObject} for Withdraw requests. Holds the key and value pairs
 * of all the fields which are necessary to make a {@link WithdrawRequest}
 * 
 * @author rkandur
 *
 */
public class WithdrawRequestObject extends AbstractRequestObject {

	private static final long serialVersionUID = 1L;

	public enum Keys {
		accountID,
		amount;
	}
	
	public WithdrawRequestObject(Arguments args) {
		this(RequestType.withdraw, args);
	}
	
	public WithdrawRequestObject(RequestType rt, Arguments args) {
		super(rt, args);
	}

	public Integer accountID() {
		return Integer.parseInt(args_.getValueFor(Keys.accountID.name()));
	}
	
	public Integer amount() {
		return Integer.parseInt(args_.getValueFor(Keys.amount.name()));
	}
	
}
