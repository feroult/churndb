package churndb.utils;

import churndb.couch.CouchClient;
import churndb.couch.response.CouchResponseView;
import churndb.model.Project;
import churndb.model.Source;

public class ChurnClient extends CouchClient {

	private static final String CORE_PROJECTS = "core/projects";

	private static final String CORE_SOURCES = "core/sources";

	public ChurnClient(String host, String database) {
		super(host, database);
	}

	public Project getProject(String projectCode) {
		return viewGetFirst(CORE_PROJECTS, TestConstants.PROJECT_CODE).as(Project.class);
	}
	
	public void deleteProject(String projectCode) {
		viewDelete(CORE_PROJECTS, projectCode);
		viewDelete(CORE_SOURCES, projectCode);
	}

	public Source getSource(String projectCode, String path) {
		CouchResponseView view = view(CORE_SOURCES, projectCode, path);
		if (view.isEmpty()) {
			return null;
		}
		return view.get(0).as(Source.class);
	}

	public void deleteSource(String projectCode, Source source) {
		viewDelete(CORE_SOURCES, projectCode, source.getPath());	
	}

}
