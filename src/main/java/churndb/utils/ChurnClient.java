package churndb.utils;

import churndb.couch.CouchClient;
import churndb.couch.response.CouchResponseView;
import churndb.model.Source;
import churndb.tasks.Setup;

public class ChurnClient extends CouchClient {

	private static final String CORE_PROJECTS = "core/projects";
	
	private static final String CORE_SOURCES = "core/sources";
	
	private transient Setup setup = Setup.homeFolderSetup();	
	
	public ChurnClient(String host, String database) {
		super(host, database);
	}

	public Source getSource(String project, String path) {		
		CouchResponseView view = view(CORE_SOURCES, project, path);
		if (view.isEmpty()) {
			return newSource(project, path);
		}
		return view.get(0).as(Source.class);
	}

	private Source newSource(String project, String path) {
		Source source = new Source(setup.getRoot(project), path);
		source.setProject(project);
		return source;
	}

	public void deleteProject(String project) {
		viewDelete(CORE_PROJECTS, project);
		viewDelete(CORE_SOURCES, project);		
	}		

}
