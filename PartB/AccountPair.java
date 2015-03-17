

/**
 * A pair of {@link Account} which is used internally
 * 
 * @author rkandur
 *
 */
public class AccountPair {

	private Account source_;
	private Account destination_;
	
	public AccountPair(Account source, Account destination) {
		setSource(source);
		setDestination(destination);
	}

	public Account getSource() {
		return source_;
	}

	public void setSource(Account source_) {
		this.source_ = source_;
	}

	public Account getDestination() {
		return destination_;
	}

	public void setDestination(Account destination_) {
		this.destination_ = destination_;
	}
	
}
