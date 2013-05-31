package churndb.model;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import churndb.couch.CouchBean;

public class Source extends CouchBean {	
	
	private String path;
	
	private String type = "source";
	
	private transient File file;
	
	private Map<String, Integer> metrics = new HashMap<String, Integer>();

	private String project;

	private String lastCommit;

	private int churn = 1;
		
	public Source(String root, String path) {
		this.file = new File(normalizeRoot(root) + path);
		this.path = path;
	}

	private String normalizeRoot(String root) {
		if(root.endsWith("/")) {
			return root;
		}
		return root + "/";
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

	public void setProject(String project) {
		this.project = project;		
	}

	public String getProject() {
		return project;
	}

	public String getType() {
		return type;
	}

	public void setLastCommit(String lastCommit) {
		this.lastCommit = lastCommit;		
	}
	
	public String getLastCommit() {		
		return lastCommit;
	}

	public int getChurn() {
		return churn;
	}

	public void addChurn() {
		churn++;		
	}
	
}
