package churndb.couch.response;

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

	public int totalRows() {
		return json.get("total_rows").getAsInt();
	}

	public int size() {
		return json.get("rows").getAsJsonArray().size();
	}
		
}
