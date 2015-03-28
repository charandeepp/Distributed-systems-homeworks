package assignment5;


import java.io.Serializable;

/**
 * Interface which represents the typical RequestObject sent by the client while
 * making a request to the Server 
 * 
 * @author rkandur
 *
 */
public interface IRequestObject extends Serializable {

	Object reqType();
	
	Object arguments();
	
}
