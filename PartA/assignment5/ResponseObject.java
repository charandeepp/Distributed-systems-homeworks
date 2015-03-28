package assignment5;


import java.io.Serializable;

/**
 * Class which represents the typical Response Object sent by the server to
 * client in response to a request
 * 
 * @author rkandur
 *
 */
public class ResponseObject implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Boolean status_;		// True/False which means success/failure
	private String response_;		// Response as a string 
	
	public ResponseObject(Boolean status, String resp) {
		setStatus(status);
		setResponse(resp);
	}

	public Boolean getStatus() {
		return status_;
	}

	public void setStatus(Boolean status_) {
		this.status_ = status_;
	}

	public String getResponse() {
		return response_;
	}

	public void setResponse(String response_) {
		this.response_ = response_;
	}

}
