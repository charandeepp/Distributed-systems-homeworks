package assignment5;


/**
 * Abstract Implementation of a {@link IRequest} which is supported and served
 * by the server.
 * 
 * @author rkandur
 *
 */
public abstract class AbstractRequest implements IRequest {

	public abstract Object execute();
	
}
