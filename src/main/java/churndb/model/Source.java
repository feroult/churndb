package churndb.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import churndb.couch.CouchBean;

public class Source extends CouchBean {

	private String sourceId;

	private String path;

	private String type = "source";

	private Map<String, Integer> metrics = new HashMap<String, Integer>();

	private String projectCode;

	private String commit;

	private int churnCount = 0;

	private Date date;

	private boolean active = true;

	public Source(String projectCode, String path) {
		this.projectCode = projectCode;
		this.path = path;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public void setPath(String path) {
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

	public void setCommit(String commit) {
		this.commit = commit;
	}

	public String getCommit() {
		return commit;
	}

	public int getChurnCount() {
		return churnCount;
	}

	public void addChurnCount() {
		churnCount++;
	}

	public void setChurnCount(int churnCount) {
		this.churnCount = churnCount;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return sourceId;
	}

}
