package churndb.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import churndb.couch.CouchBean;

public class Tree extends CouchBean {

	private String projectCode;

	private String commit;

	private int number;

	private List<String> sources;

	private String type = "tree";

	private Date date;

	public Tree(String projectCode, String commit, Date date, int number) {
		this.projectCode = projectCode;
		this.commit = commit;
		this.date = date;
		this.number = number;
		this.sources = new ArrayList<String>();
	}

	public void add(List<Source> sources) {
		for (Source source : sources) {
			this.sources.add(source.get_id());
		}
	}

	public String getProjectCode() {
		return projectCode;
	}

	public String getCommit() {
		return commit;
	}

	public List<String> getSources() {
		return sources;
	}

	public Date getDate() {
		return date;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getType() {
		return type;
	}

}
