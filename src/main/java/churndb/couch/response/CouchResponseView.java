package churndb.couch.response;

import org.apache.http.StatusLine;

import churndb.couch.CouchClient;

import com.google.gson.JsonObject;

public class CouchResponseView extends CouchResponse {

	public CouchResponseView(CouchClient couch, String responseBody, StatusLine statusLine) {
		super(couch, responseBody, statusLine);
	}

	public JsonObject json(int i) {
		return json.get("rows").getAsJsonArray().get(i).getAsJsonObject();
	}
	
	public JsonObject first() {
		return json(0);
	}

	public int size() {
		return json.get("rows").getAsJsonArray().size();
	}

	public CouchResponse get(int i) {
		return couch().get(json(i).get("id"));
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public <T> T firstAs(Class<T> clazz) {
		if (isEmpty()) {
			return null;
		}
		return get(0).as(clazz);
	}
		
}
