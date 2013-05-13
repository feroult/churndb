package churndb.couch;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class DesignDocument {

	private transient String id;

	private Map<String, View> views;
	
	public DesignDocument(String id) {
		this.id = "_design/" + id;
	}

	public void addViewMap(String key, String source) {
		createViewIfNotExists(key);
		
		View view = views.get(key);
		view.setMap(source);
	}

	private void createViewIfNotExists(String key) {
		if(views == null) {
			views = new HashMap<String, View>();
		}
		if(!views.containsKey(key)) {
			views.put(key, new View());
		}
	}

	public String getId() {
		return id;
	}

	public Map<String, View> getViews() {
		return views;
	}

	public String json() {
		return new Gson().toJson(this);
	}
}
