
/**
 * class which denotes Response format which is used by the {@link IBankServer}
 * internally
 * 
 * @author rkandur
 *
 */
public class RequestResponse {

	private Boolean status_;			// true/false which denotes success/failure of a request
	private String response_;			// Response code
	private Object result_;				// Result of the request
	
	public RequestResponse(Boolean status, String resp, Object result) {
		setStatus(status);
		setResponse(resp);
		setResult(result);
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

	public Object getResult() {
		return result_;
	}

	public void setResult(Object result_) {
		this.result_ = result_;
	}
	
}
