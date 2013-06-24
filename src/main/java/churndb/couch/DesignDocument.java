package churndb.couch;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class DesignDocument extends CouchBean {

	private String language = "javascript";
	
	private Map<String, View> views;
	
	public DesignDocument(String id) {
		set_id("_design/" + id);
	}

	public void addViewMap(String key, String source) {
		createViewIfNotExists(key);
		
		View view = views.get(key);
		view.setMap(source);
	}
	
	public void addViewReduce(String key, String source) {
		createViewIfNotExists(key);
		
		View view = views.get(key);
		view.setReduce(source);		
	}

	private void createViewIfNotExists(String key) {
		if(views == null) {
			views = new HashMap<String, View>();
		}
		if(!views.containsKey(key)) {
			views.put(key, new View());
		}
	}

	public Map<String, View> getViews() {
		return views;
	}

	public String json() {
		return new Gson().toJson(this);
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
	
}
