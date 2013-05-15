package churndb.couch.response;

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CouchResponse {

	protected JsonObject json;
	protected StatusLine statusLine;

	public CouchResponse(String responseBody, StatusLine statusLine) {
		this.statusLine = statusLine;
		json = (JsonObject) new JsonParser().parse(responseBody);
	}

	public JsonObject json() {
		return json;
	}

	public boolean objectNotFound() {
		return statusLine.getStatusCode() == HttpStatus.SC_NOT_FOUND;
	}

	public <T> T bean(Class<T> clazz) {
		return new Gson().fromJson(json, clazz);
	}

}