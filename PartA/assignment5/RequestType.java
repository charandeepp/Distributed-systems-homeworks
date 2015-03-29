package assignment5;


/**
 * Enum which denotes all the supported {@link IRequest} types by this RMI
 * client-server model
 * 
 * @author rkandur
 *
 */
public enum RequestType {
	
	newaccount, 
	deposit, 
	withdraw, 
	transfer, 
	balance,
	halt,
	invalid;

	public RequestType getRequestTypeFor(String req) {
		for (RequestType r : RequestType.values()) {
			if (req.toLowerCase().equals(r.name())) {
				return r;
			}
		}
		return RequestType.invalid;
	}
}
