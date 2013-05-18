package churndb.model;

import com.google.gson.Gson;

public class Project {	
	private String code;
		
	private String repoUrl;

	private String type = "project";

	private String head;	
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
		
	}
}
