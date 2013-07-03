package churndb.couch.response;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.StatusLine;

import churndb.couch.CouchClient;
import churndb.utils.JsonUtils;

import com.google.gson.JsonObject;

public class CouchResponseView extends CouchResponse {

	public CouchResponseView(CouchClient couch, String responseBody, StatusLine statusLine) {
		super(couch, responseBody, statusLine);
	}

	public JsonObject json(int i) {
		return json.get("rows").getAsJsonArray().get(i).getAsJsonObject();
	}
	
	private JsonObject value(int i) {
		return json(i).get("value").getAsJsonObject();
	}

	private JsonObject doc(int i) {
		return json(i).get("doc").getAsJsonObject();
	}	
	
	public JsonObject first() {
		return json(0);
	}

	public int size() {
		return json.get("rows").getAsJsonArray().size();
	}

	public CouchResponse get(int i) {
		return couch.get(json(i).get("id"));
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

	public <T> List<T> valuesAs(Class<T> clazz) {
		List<T> result = new ArrayList<T>();
		
		for(int i = 0; i < size(); i++) {
			result.add(JsonUtils.from(value(i), clazz)); 
		}
		
		return result;
	}

	public <T> List<T> docsAs(Class<T> clazz) {
		List<T> result = new ArrayList<T>();
		
		for(int i = 0; i < size(); i++) {
			result.add(JsonUtils.from(doc(i), clazz)); 
		}
		
		return result;
	}

			
}
