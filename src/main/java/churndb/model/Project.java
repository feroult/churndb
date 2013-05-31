package churndb.model;

import churndb.couch.CouchBean;
import churndb.couch.response.CouchResponseView;

public class Project extends CouchBean {	
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

	public String getType() {
		return type;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
		
	}

	// service methods
	
	public Source getSource(String path) {		
		CouchResponseView view = couch.view("core/sources", code, path);		
		if(view.isEmpty()) {
			return null;
		}		
		return view.get(0).as(Source.class);		
	}
}
