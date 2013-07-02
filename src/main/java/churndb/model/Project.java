package churndb.model;

import java.util.Date;

import churndb.couch.CouchBean;

public class Project extends CouchBean {
	private String code;

	private String repoUrl;

	private String type = "project";

	private String commit;

	private int treeNumber = 0;

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

	public String getCommit() {
		return commit;
	}

	public void setCommit(String commit) {
		this.commit = commit;

	}

	public void setLastChange(Date lastChange) {
		this.lastChange = lastChange;
	}

	public Date getLastChange() {
		return lastChange;
	}

	public int getTreeNumber() {
		return treeNumber;
	}

	public void addTreeNumber() {
		this.treeNumber++;
	}

	public void reset() {
		this.lastChange = null;
		this.commit = null;
		this.treeNumber = 0;		
	}

}
