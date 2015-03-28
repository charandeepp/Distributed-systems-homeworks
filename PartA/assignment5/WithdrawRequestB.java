package assignment5;

/**
 * An implementation of {@link IRequest} which is used to make a withdrawal from
 * an {@link Account}
 * 
 * @author rkandur
 *
 */

public class WithdrawRequestB extends AbstractRequest {

	private Account account_;
	private Integer withdrawAmount_;
	
	public WithdrawRequestB(Account account, Integer withdraw) {
		
		if(account == null || !account.isValid()) {
			throw new IllegalArgumentException("Invalid Bank Account, Cannot proceed further !");
		}
		this.account_ = account;
		this.withdrawAmount_ = withdraw;
	}
	
	public RequestResponse execute() {
		
		if(withdrawAmount_ <= 0) {
			ServerLogger.logger().severe(
					"Minimum withdraw should be > 0, current withdraw request = "
							+ withdrawAmount_ + " for the account = "
							+ account_.getAccountID());
			return new RequestResponse(Boolean.FALSE, "FAIL", null);
		}
		if(account_.getBalance() < withdrawAmount_) {
			ServerLogger.logger().severe(
					"Withdraw Amount is more than Balance, current withdraw request = "
							+ withdrawAmount_ + " for the account = "
							+ account_.getAccountID() + " current balance = "
							+ account_.getBalance());
			return new RequestResponse(Boolean.FALSE, "FAIL", null);
		}
		
		account_.setBalance(account_.getBalance()-withdrawAmount_);
		return new RequestResponse(Boolean.TRUE, "OK", account_);
	}

}
