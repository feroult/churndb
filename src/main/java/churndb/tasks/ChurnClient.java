package churndb.tasks;

import churndb.couch.CouchClient;
import churndb.model.Project;
import churndb.model.Source;

public class ChurnClient extends CouchClient {

	private static final String PROJECTS_ALL = "projects/all";

	private static final String SOURCES_ALL = "sources/all";
	
	private static final String SOURCES_ACTIVE = "sources/active";
	
	private static final String SOURCES_COMMIT = "sources/commit";

	public ChurnClient(String host, String database) {
		super(host, database);
	}

	public Project getProject(String projectCode) {
		return view(PROJECTS_ALL, projectCode).firstAs(Project.class);
	}
	
	public void deleteProjectSources(String projectCode) {
		viewDelete(SOURCES_ALL, projectCode);
	}

	public Source getActiveSource(String projectCode, String path) {
		return view(SOURCES_ACTIVE, projectCode, path).firstAs(Source.class);
	}

	public void deleteSource(String projectCode, Source source) {
		// TODO change this makes sense?
		viewDelete(SOURCES_ALL, projectCode, source.getPath());	
	}

	public Source getSourceInCommit(String projectCode, String commit, String path) {
		// TODO different view for commit snapshots
		return view(SOURCES_COMMIT, projectCode, commit, path).firstAs(Source.class);
	}

	public Source getLastSource(String projectCode, String path) {
		return viewDescending(SOURCES_ALL, projectCode, path).firstAs(Source.class);
	}

}
