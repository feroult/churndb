package churndb.sourcebot.couchdb;

import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;

public class CouchResponseException extends RuntimeException {

	private static final long serialVersionUID = 2310011895549995839L;
	
	private HttpResponseException response;

	public CouchResponseException(HttpResponseException response) {
		this.response = response;	
	}

	public HttpResponseException getResponse() {
		return response;
	}

	public int getStatus() {
		return response.getStatusCode();
	}

	public boolean objectNotFound() {
		return getStatus() == HttpStatus.SC_NOT_FOUND; 
	}
}
