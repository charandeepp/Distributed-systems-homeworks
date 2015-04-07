package assignment5;


/**
 * An implementation of {@link IRequest} which is used to find the balance of an
 * {@link Account}
 * 
 * @author rkandur
 *
 */
public class BalanceRequestB extends AbstractRequest {

	private static final long serialVersionUID = 1L;
	
	Account account_;
	
	public BalanceRequestB(Account account) {
		if(account == null || !account.isValid()) {
			throw new IllegalArgumentException("Invalid Bank Account, Cannot proceed further !");
		}
		this.account_ = account;
	}
	
	public RequestResponse execute() {
		return new RequestResponse(Boolean.TRUE, account_.getBalance().toString(), account_.getBalance());
	}
	
	@Override
	public String arguments() {
		return new StringBuilder().append(account_.getAccountID()).toString();
	}

}
