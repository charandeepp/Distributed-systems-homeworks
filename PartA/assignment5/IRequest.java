package assignment5;

import java.io.Serializable;

/**
 * Interface which denotes a request sent by the client to an RMI server
 * 
 * @author rkandur
 *
 */
public interface IRequest extends Serializable {
	
	public Object execute();
	
}
