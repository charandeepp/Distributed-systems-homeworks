package assignment5;


/**
 * An implementation of {@link IRequest} which is used to create a new
 * {@link Account}
 * 
 * @author rkandur
 *
 */

public class NewAccountRequestB extends AbstractRequest {

	private static final long serialVersionUID = 1L;
	
	private static Integer STARTING_BALANCE = 0;
	private static Integer CURRENT_ACCOUNT_NUMBER = 1;
	
	private String firstName_;
	private String lastName_;
	private String address_;
	
	public NewAccountRequestB(String firstName, String lastName, String address)
			throws IllegalArgumentException {
		setFirstName(firstName);
		setLastName(lastName);
		setAddress(address);
	}

	public RequestResponse execute() {
		
		Account account = new Account(getNewAccountID(), STARTING_BALANCE,
				getFirstName(), getLastName(), getAddress());

		if (!account.isValid()) {
			ServerLogger
					.logger()
					.severe("Account details provided are incorrect. Could not create account! FirstName = "
							+ getFirstName() + " Last Name = "
							+ getLastName() + " Address  = " + getAddress());
			return new RequestResponse(Boolean.FALSE, "-1", null);
		}
		
		return new RequestResponse(Boolean.TRUE, account.getAccountID().toString(), account);
	}
	
	private Integer getNewAccountID() {
		CURRENT_ACCOUNT_NUMBER++;
		return CURRENT_ACCOUNT_NUMBER;
	}

	public String getFirstName() {
		return firstName_;
	}

	public void setFirstName(String firstName_) throws IllegalArgumentException {
		this.firstName_ = firstName_;
	}

	public String getLastName() {
		return lastName_;
	}

	public void setLastName(String lastName_) throws IllegalArgumentException {
		this.lastName_ = lastName_;
	}

	public String getAddress() {
		return address_;
	}

	public void setAddress(String address_) throws IllegalArgumentException {
		this.address_ = address_;
	}

	@Override
	public String arguments() {
		return new StringBuilder().append(firstName_).append(lastName_)
				.append(address_).toString();
	}
	
}
