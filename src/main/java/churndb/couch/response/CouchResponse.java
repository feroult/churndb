package churndb.couch.response;

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;

import churndb.couch.CouchClient;
import churndb.utils.JsonUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CouchResponse {

	protected CouchClient couch;
	
	protected JsonObject json;
	
	protected StatusLine statusLine;

	public CouchResponse(CouchClient couch, String responseBody, StatusLine statusLine) {
		this.couch = couch;
		this.statusLine = statusLine;
		json = (JsonObject) new JsonParser().parse(responseBody);
	}

	public JsonObject json() {
		return json;
	}

	public boolean objectNotFound() {
		return statusLine.getStatusCode() == HttpStatus.SC_NOT_FOUND;
	}

	public <T> T as(Class<T> clazz) {
		return JsonUtils.from(json, clazz);
	}

}
