package assignment5;

/**
 * {@link IRequestObject} for Halt request. ACtually has nothing to hold, but
 * added this for following the way we handle requests
 * 
 * @author rkandur
 *
 */
public class HaltRequestObject extends AbstractRequestObject {

	private static final long serialVersionUID = 1L;

	public HaltRequestObject() {
		super(RequestType.halt, new Arguments());
	}

}
