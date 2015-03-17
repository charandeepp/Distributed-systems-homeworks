

/**
 * class which denotes the details of an {@link Account}
 * 
 * @author rkandur
 *
 */
public class Account {

	private Integer accountID_;			//	account ID
	private Integer balance_;			// balance
	private String firstName_;			// Firstname of the Account holder
	private String lastName_;			// Lastname of the Account holder
	private String address_;			// Address of the Account holder
	
	public Account(Integer account, Integer balance, String firstName,
			String lastName, String address) {
		setAccountID(account);
		setBalance(balance);
		setFirstName(firstName);
		setLastName(lastName);
		setAddress(address);
	}

	public boolean isValid() {
		return (this.accountID_ > 0 && balance_ >= 0 && 
				firstName_ != null && !firstName_.isEmpty() && 
				lastName_ != null && !lastName_.isEmpty() && 
				address_ != null && !address_.isEmpty());
	}
	
	public Integer getBalance() {
		return balance_;
	}

	public void setBalance(Integer balance) {
		this.balance_ = balance;
	}

	public Integer getAccountID() {
		return accountID_;
	}

	public void setAccountID(Integer accountID) {
		this.accountID_ = accountID;
	}

	public String getFirstName() {
		return firstName_;
	}

	public void setFirstName(String firstName) {
		this.firstName_ = firstName;
	}

	public String getLastName() {
		return lastName_;
	}

	public void setLastName(String lastName) {
		this.lastName_ = lastName;
	}


	public String getAddress() {
		return address_;
	}

	public void setAddress(String address) {
		this.address_ = address;
	}
	
}
