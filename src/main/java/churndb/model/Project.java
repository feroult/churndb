package churndb.model;

import java.util.Date;

import churndb.couch.CouchBean;

public class Project extends CouchBean {
	private String code;

	private String repoUrl;

	private String type = "project";

	private String lastCommit;

	private Date lastChange;

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

	public String getType() {
		return type;
	}

	public String getLastCommit() {
		return lastCommit;
	}

	public void setLastCommit(String lastCommit) {
		this.lastCommit = lastCommit;

	}

	public void setLastChange(Date lastChange) {
		this.lastChange = lastChange;
	}
	
	public Date getLastChange() {
		return lastChange;
	}	
}
