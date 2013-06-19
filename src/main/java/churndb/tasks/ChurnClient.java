package churndb.tasks;

import java.util.List;

import churndb.couch.CouchClient;
import churndb.couch.ViewOptions;
import churndb.model.Project;
import churndb.model.Source;

public class ChurnClient extends CouchClient {

	private static final String PROJECTS_ALL = "projects/all";

	private static final String SOURCES_ALL = "sources/all";

	private static final String SOURCES_ACTIVE = "sources/active";

	private static final String SOURCES_COMMIT = "sources/commit";
	
	private static final String TREES_SOURCES = "trees/sources";

	public ChurnClient(String host, String database) {
		super(host, database);
	}

	public Project getProject(String projectCode) {
		return view(PROJECTS_ALL, projectCode).getFirstAs(Project.class);
	}

	public void deleteProjectSources(String projectCode) {
		viewDelete(SOURCES_ALL, projectCode);
	}

	public Source getActiveSource(String projectCode, String path) {
		return view(SOURCES_ACTIVE, projectCode, path).getFirstAs(Source.class);
	}

	public List<Source> getActiveSources(String projectCode) {
		return view(SOURCES_ACTIVE, projectCode).valuesAs(Source.class);
	}

	public Source getSourceInCommit(String projectCode, String commit, String path) {
		// TODO different view for commit snapshots
		return view(SOURCES_COMMIT, projectCode, commit, path).getFirstAs(Source.class);
	}

	public Source getLastSource(String projectCode, String path) {
		return viewDescending(SOURCES_ALL, projectCode, path).getFirstAs(Source.class);
	}

	public List<Source> getSourcesInCommit(String projectCode, String commit) {
		return view(TREES_SOURCES, ViewOptions.INCLUDE_DOCS, projectCode, commit).docsAs(Source.class);
	}
}
