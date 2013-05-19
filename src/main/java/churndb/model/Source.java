package churndb.model;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import churndb.git.Commit;

import com.google.gson.Gson;

public class Source {

	private String path;
	
	private String type = "source";
	
	private transient File file;
	
	private Map<String, Integer> metrics = new HashMap<String, Integer>();

	private String project;
	
	private Churn churn = new Churn();
	
	public Source(String root, String path) {
		this.file = new File(root + path);
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public File getFile() {
		return file;
	}

	public Integer getMetric(String key) {
		return metrics.get(key);
	}
		
	public void setMetric(String key, Integer value) {
		metrics.put(key, value);		
	}

	public String json() {
		return new Gson().toJson(this);
	}

	public void setProject(String project) {
		this.project = project;		
	}

	public String getProject() {
		return project;
	}

	public String getType() {
		return type;
	}

	public void initChurn(Commit commit) {
		churn.init(commit);		
	}

	public Churn getChurn() {
		return churn;
	}
}
