package assignment5;


/**
 * Abstract Implementation of a {@link IRequest} which is supported and served
 * by the server.
 * 
 * @author rkandur
 *
 */
public abstract class AbstractRequest implements IRequest {

	private static final long serialVersionUID = 1L;

	public abstract Object execute();
	
}
