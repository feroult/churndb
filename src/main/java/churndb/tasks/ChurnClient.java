package churndb.tasks;

import java.util.List;

import churndb.couch.CouchClient;
import churndb.couch.ViewOptions;
import churndb.model.Project;
import churndb.model.Source;
import churndb.model.Tree;

public class ChurnClient extends CouchClient {

	private static final String PROJECTS_ALL = "projects/all";

	private static final String SOURCES_ALL = "sources/all";

	private static final String SOURCES_ACTIVE = "sources/active";

	private static final String SOURCES_COMMIT = "sources/commit";
	
	private static final String TREES_ALL = "trees/all";
	
	private static final String TREES_SOURCES = "trees/sources";

	public ChurnClient(String host, String database) {
		super(host, database);
	}

	public Project getProject(String projectCode) {
		return view(PROJECTS_ALL, projectCode).firstAs(Project.class);
	}
	
	public void deleteProject(String projectCode) {
		viewDelete(PROJECTS_ALL, projectCode);
		deleteProjectSources(projectCode);
	}

	public void deleteProjectSources(String projectCode) {
		viewDelete(SOURCES_ALL, projectCode);
		deleteProjectTrees(projectCode);
	}

	private void deleteProjectTrees(String projectCode) {
		viewDelete(TREES_ALL, projectCode);
	}

	public Tree getTree(String projectCode, String commit) {
		return view(TREES_ALL, projectCode, commit).firstAs(Tree.class);
	}
	
	public Source getActiveSource(String projectCode, String path) {
		return view(SOURCES_ACTIVE, projectCode, path).firstAs(Source.class);
	}

	public List<Source> getActiveSources(String projectCode) {
		return view(SOURCES_ACTIVE, projectCode).valuesAs(Source.class);
	}

	public Source getSourceInCommit(String projectCode, String commit, String path) {
		return view(SOURCES_COMMIT, projectCode, commit, path).firstAs(Source.class);
	}

	public Source getLastSource(String projectCode, String path) {
		return view(SOURCES_ALL, ViewOptions.DESCENDING, projectCode, path).firstAs(Source.class);
	}

	public List<Source> getSourcesInTree(String projectCode, String commit) {
		return view(TREES_SOURCES, ViewOptions.INCLUDE_DOCS, projectCode, commit).docsAs(Source.class);
	}

}
