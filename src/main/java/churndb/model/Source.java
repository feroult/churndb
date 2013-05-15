package churndb.model;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class Source {

	private String path;
	
	private String type = "source";
	
	private transient File file;
	
	private Map<String, String> metrics = new HashMap<String, String>();

	private String project;
	
	private Commit commit = new Commit();
	
	public Source(String root, File file) {
		this.path = extractRoot(root, file.getPath());
		this.file = file;
	}

	public Source(String path) {
		this.path = path;
	}

	private String extractRoot(String root, String path) {
		return path.replaceFirst(normalizePath(root), "");
	}

	private String normalizePath(String root) {
		if(!root.endsWith("/")) {
			root += "/";
		}
		return root;
	}

	public String getPath() {
		return path;
	}

	public File getFile() {
		return file;
	}

	public String getMetric(String key) {
		return metrics.get(key);
	}
	
	private void setMetric(String key, String value) {
		metrics.put(key, value);
	}
		
	public void setMetric(String key, long value) {
		setMetric(key, String.valueOf(value));		
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

	public void setCommitTime(int commitTime) {
		commit.setCommitTime(commitTime);		
	}

}
