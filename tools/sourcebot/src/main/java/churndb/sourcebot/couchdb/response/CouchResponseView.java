package churndb.sourcebot.couchdb.response;

import org.apache.http.StatusLine;

import com.google.gson.JsonObject;

public class CouchResponseView extends CouchResponse {

	public CouchResponseView(String responseBody, StatusLine statusLine) {
		super(responseBody, statusLine);
	}

	public JsonObject rows(int i) {
		return json.get("rows").getAsJsonArray().get(i).getAsJsonObject();
	}
	
	public JsonObject first() {
		return rows(0);
	}
		
}
