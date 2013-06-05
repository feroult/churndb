package churndb.utils;

import churndb.couch.CouchClient;
import churndb.couch.response.CouchResponseView;
import churndb.model.Project;
import churndb.model.Source;

public class ChurnClient extends CouchClient {

	private static final String PROJECTS_ALL = "projects/all";

	private static final String SOURCES_ALL = "sources/all";

	public ChurnClient(String host, String database) {
		super(host, database);
	}

	public Project getProject(String projectCode) {
		return viewGetFirst(PROJECTS_ALL, TestConstants.PROJECT_CODE).as(Project.class);
	}
	
	public void deleteProject(String projectCode) {
		viewDelete(PROJECTS_ALL, projectCode);
		viewDelete(SOURCES_ALL, projectCode);
	}

	public Source getSource(String projectCode, String path) {
		CouchResponseView view = view(SOURCES_ALL, projectCode, path);
		if (view.isEmpty()) {
			return null;
		}
		return view.get(0).as(Source.class);
	}

	public void deleteSource(String projectCode, Source source) {
		viewDelete(SOURCES_ALL, projectCode, source.getPath());	
	}

}
