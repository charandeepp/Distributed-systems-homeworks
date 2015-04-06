package assignment5;


/**
 * An implementation of {@link IRequest} which is used to request a new Deposit
 * into an {@link Account}
 * 
 * @author rkandur
 *
 */
public class DepositRequestB extends AbstractRequest {

	private static final long serialVersionUID = 1L;
	
	private Account account_;
	private Integer depositAmount_;
	
	public DepositRequestB(Account account, Integer deposit) throws IllegalArgumentException {
		if(account == null || !account.isValid()) {
			throw new IllegalArgumentException("Invalid Bank Account, Cannot proceed further !");
		}
		account_ = account;
		depositAmount_ = deposit;
	}
	
	public RequestResponse execute() {

		if(depositAmount_ <= 0) {
			ServerLogger.logger().severe(
					"Minimum deposit should be > 0, current deposit request = "
							+ depositAmount_ + " for the account = "
							+ account_.getAccountID());
			return new RequestResponse(Boolean.FALSE, "FAIL", null);
		}
		
		account_.setBalance(account_.getBalance()+depositAmount_);
		return new RequestResponse(Boolean.TRUE, "OK", account_);
	}

}
