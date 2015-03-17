

/**
 * An implementation of {@link IRequest} which is used to find the balance of an
 * {@link Account}
 * 
 * @author rkandur
 *
 */
public class BalanceRequest extends AbstractRequest {

	Account account_;
	
	public BalanceRequest(Account account) {
		if(account == null || !account.isValid()) {
			throw new IllegalArgumentException("Invalid Bank Account, Cannot proceed further !");
		}
		this.account_ = account;
	}
	
	public RequestResponse execute() {
		return new RequestResponse(Boolean.TRUE, account_.getBalance().toString(), account_.getBalance());
	}

}
