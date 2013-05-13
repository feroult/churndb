package churndb.model;

import com.google.gson.Gson;

public class Project {	
	private String name;
	
	private String root;
	
	private String repoUrl;

	private String type = "project";	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public String getRepoUrl() {
		return repoUrl;
	}

	public void setRepoUrl(String repoUrl) {
		this.repoUrl = repoUrl;
	}

	public String json() {
		return new Gson().toJson(this);
	}

	public String getType() {
		return type;
	}
}
