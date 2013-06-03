package churndb.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import churndb.couch.CouchBean;

public class Source extends CouchBean {	
	
	private String path;
	
	private String type = "source";
		
	private Map<String, Integer> metrics = new HashMap<String, Integer>();

	private String projectCode;

	private String lastCommit;

	private int churn = 0;

	private Date lastChange;
		
	public Source(String projectCode, String path) {
		this.projectCode = projectCode;
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public Integer getMetric(String key) {
		return metrics.get(key);
	}
		
	public void setMetric(String key, Integer value) {
		metrics.put(key, value);		
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;		
	}

	public String getProjectCode() {
		return projectCode;
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

	public Date getLastChange() {
		return lastChange;
	}

	public void setLastChange(Date lastChange) {
		this.lastChange = lastChange;
		
	}
	
}
