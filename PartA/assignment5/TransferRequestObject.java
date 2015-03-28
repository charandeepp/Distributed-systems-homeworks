package assignment5;

/**
 * {@link IRequestObject} for Transfer requests. Holds the key and value pairs
 * of all the fields which are necessary to make a {@link TransferRequest}
 * 
 * @author rkandur
 *
 */
public class TransferRequestObject extends AbstractRequestObject {

	private static final long serialVersionUID = 1L;

	public enum Keys {
		sourceID,
		destinationID,
		amount;
	}
	
	public TransferRequestObject(Arguments args) {
		this(RequestType.transfer, args);
	}
	
	public TransferRequestObject(RequestType rt, Arguments args) {
		super(rt, args);
	}

	public Integer sourceID() {
		return Integer.parseInt(args_.getValueFor(Keys.sourceID.name()));
	}
	
	public Integer destinationID() {
		return Integer.parseInt(args_.getValueFor(Keys.destinationID.name()));
	}
	
	public Integer amount() {
		return Integer.parseInt(args_.getValueFor(Keys.amount.name()));
	}
}
