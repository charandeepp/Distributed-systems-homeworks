package assignment5;


/**
 * An implementation of {@link IRequest} which is used to make a transfer from a
 * source {@link Account} to a destination {@link Account}
 * 
 * @author rkandur
 *
 */

public class TransferRequestB extends AbstractRequest {

	Account source_;
	Account destination_;
	Integer amount_;
	
	public TransferRequestB(Account source, Account destination, Integer amount) {
		if(source == null || !source.isValid() ||
				destination == null || !destination.isValid()) {
			throw new IllegalArgumentException("Invalid Source/Destination Accounts, Cannot proceed further !");
		}
		this.source_ = source;
		this.destination_ = destination;
		this.amount_ = amount;
	}
	
	public RequestResponse execute() {
		
		if(amount_ <= 0) {
			ServerLogger.logger().severe(
					"Amount to be transferred should be > 0, current request : sourceID "
							+ source_.getAccountID() + " , destinationID = "
							+ destination_.getAccountID() + " , amount = " + amount_);
			return new RequestResponse(Boolean.FALSE, "FAIL", null);
		}
		if(source_.getBalance() < amount_) {
			ServerLogger.logger().severe(
					"Source Account does not have enough balance to make the transaction,"
							+ " Source Balance = " + +source_.getBalance()
							+ " , amount = " + amount_);
			return new RequestResponse(Boolean.FALSE, "FAIL", null);
		}
		
		source_.setBalance(source_.getBalance()-amount_);
		destination_.setBalance(destination_.getBalance()+amount_);
		return new RequestResponse(Boolean.TRUE, "OK", new AccountPair(source_, destination_));
	}

}
