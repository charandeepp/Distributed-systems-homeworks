
/**
 * Abstract Implementation for a {@link IRequestObject}
 * 
 * It usually has two fields, {@link RequestType} and {@link Arguments} which
 * are keyvalue pairs of the arguments sent in {@link IRequest}
 * 
 * @author rkandur
 *
 */
public class AbstractRequestObject implements IRequestObject {

	private static final long serialVersionUID = 1L;
	
	public RequestType reqType_;
	public Arguments args_;
	
	public AbstractRequestObject(RequestType rt, Arguments args) {
		reqType_ = rt;
		args_ = args;
	}

	public RequestType reqType() {
		return reqType_;
	}

	public Arguments arguments() {
		return args_;
	}
	
}
