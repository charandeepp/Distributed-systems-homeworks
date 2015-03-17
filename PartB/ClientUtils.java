

import java.rmi.RemoteException;
import java.util.logging.Logger;

/**
 * Utility class which does all the client related pre and post request
 * processing
 * 
 * @author rkandur
 *
 */
public class ClientUtils {

	private ClientUtils() {
		
	}
	
	private static Logger logger = ClientLogger.logger();
	
	// sends a new Withdraw Request to the server and fetches the response
	public static String withdrawFromAccount(IBankServer impl, Integer accountID, Integer amount) throws RemoteException {

		Arguments reqArgs = new Arguments();
		reqArgs.addArgument(WithdrawRequestObject.Keys.accountID.name(), accountID.toString());
		reqArgs.addArgument(WithdrawRequestObject.Keys.amount.name(), amount.toString());
		
		WithdrawRequestObject reqObject = new WithdrawRequestObject(reqArgs);
		ResponseObject reqResponse = impl.handleRequest(reqObject);
		
		if(reqResponse.getStatus()) {
			logger.info("Successful Withdraw . Response {" + reqResponse.getResponse() + "}");
			return reqResponse.getResponse();
		} 

		logger.warning("Failed to withdraw amount from the Account. Response { " + reqResponse.getResponse() + " }");
		return new String();
		
	}

	// sends a new Transfer Request to the server and fetches the response
	public static String transferAmount(IBankServer impl, Integer sourceID,
			Integer destinationID, Integer amount) throws RemoteException {
		
		Arguments reqArgs = new Arguments();
		reqArgs.addArgument(TransferRequestObject.Keys.sourceID.name(), sourceID.toString());
		reqArgs.addArgument(TransferRequestObject.Keys.destinationID.name(), destinationID.toString());
		reqArgs.addArgument(TransferRequestObject.Keys.amount.name(), amount.toString());
		
		TransferRequestObject reqObject = new TransferRequestObject(reqArgs);
		ResponseObject reqResponse = impl.handleRequest(reqObject);
		
		if(reqResponse.getStatus()) {
			logger.info("Transferred amount successfully. Response {" + reqResponse.getResponse() + "}");
			return reqResponse.getResponse();
		}
		
		logger.warning("Failed to transfer amount. Response { " + reqResponse.getResponse() + " }");
		return new String();
		
	}

	// sends a new Balance Enquiry Request to the server and fetches the response
	public static Integer balanceEnquiry(IBankServer impl, Integer accountID) throws RemoteException {

		Arguments reqArgs = new Arguments();
		reqArgs.addArgument(BalanceRequestObject.Keys.accountID.name(), accountID.toString());
		
		BalanceRequestObject reqObject = new BalanceRequestObject(reqArgs);
		ResponseObject reqResponse = impl.handleRequest(reqObject);
		
		if(reqResponse.getStatus()) {
			logger.info("Balance {" + reqResponse.getResponse() + "}");
			return Integer.parseInt(reqResponse.getResponse());
		} 
			
		logger.warning("Failed to get Balance for this account. Response { " + reqResponse.getResponse() + " }");
		return -1;
		
	}

	// sends a new Deposit Request to the server and fetches the response
	public static String depositToAccount(IBankServer impl, Integer accountID, Integer amount) throws RemoteException {

		Arguments reqArgs = new Arguments();
		reqArgs.addArgument(DepositRequestObject.Keys.accountID.name(), accountID.toString());
		reqArgs.addArgument(DepositRequestObject.Keys.amount.name(), amount.toString());
		
		DepositRequestObject reqObject = new DepositRequestObject(reqArgs);
		ResponseObject reqResponse = impl.handleRequest(reqObject);
		
		if(reqResponse.getStatus()) {
			logger.info("Deposit Successful. Response {" + reqResponse.getResponse() + "}");
			return reqResponse.getResponse();
		} 

		logger.warning("Failed to deposit to Account. Response { " + reqResponse.getResponse() + " }");
		return new String();
		
	}

	// sends a new Account Request to the server and fetches the response
	public static Integer createAccount(IBankServer impl, String firstname, String lastname, String address) throws RemoteException {
		
		Arguments reqArgs = new Arguments();
		reqArgs.addArgument(NewAccountRequestObject.Keys.firstname.name(), firstname);
		reqArgs.addArgument(NewAccountRequestObject.Keys.lastname.name(), lastname);
		reqArgs.addArgument(NewAccountRequestObject.Keys.address.name(), address);
		
		NewAccountRequestObject reqObject = new NewAccountRequestObject(reqArgs);
		ResponseObject reqResponse = impl.handleRequest(reqObject);
		
		if(reqResponse.getStatus()) {
			logger.info("Created new Account Successfully. Response {" + reqResponse.getResponse() + "}");
			return Integer.parseInt(reqResponse.getResponse());
		}
		
		logger.warning("Failed to create new Account. Response { " + reqResponse.getResponse() + " }");
		return -1;
		
	}
	
}
