package churndb.model;

import java.util.ArrayList;
import java.util.List;

import churndb.couch.CouchBean;

public class Tree extends CouchBean {

	private String projectCode;

	private String commit;

	private List<String> sources;

	private String type = "tree";

	public Tree(String projectCode, String commit) {
		this.projectCode = projectCode;
		this.commit = commit;
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

	public String getType() {
		return type;
	}

}
