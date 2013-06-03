package churndb.utils;

import churndb.couch.CouchClient;
import churndb.couch.response.CouchResponseView;
import churndb.model.Source;

public class ChurnClient extends CouchClient {

	private static final String CORE_PROJECTS = "core/projects";
	
	private static final String CORE_SOURCES = "core/sources";
		
	public ChurnClient(String host, String database) {
		super(host, database);
	}

	public Source getSource(String project, String path) {		
		CouchResponseView view = view(CORE_SOURCES, project, path);
		if (view.isEmpty()) {
			return new Source(project, path);
		}
		return view.get(0).as(Source.class);
	}

	public void deleteProject(String project) {
		viewDelete(CORE_PROJECTS, project);
		viewDelete(CORE_SOURCES, project);		
	}		

}
