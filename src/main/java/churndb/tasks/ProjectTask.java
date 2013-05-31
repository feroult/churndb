package churndb.tasks;

import churndb.couch.response.CouchResponseView;
import churndb.git.Change;
import churndb.git.Commit;
import churndb.git.GIT;
import churndb.model.Project;
import churndb.model.Source;
import churndb.model.Metrics;

public class ProjectTask extends ChurnDBTask {

	private Project project;
	
	private GIT git;
	
	public ProjectTask(Project project) {
		super();
		this.project = project;
		this.git = new GIT(setup().getRoot(project.getCode()));
	}

	public void reload() {		
		deleteProjectIfExists();				
		reloadProjectFromGIT();
	}

	private void reloadProjectFromGIT() {		
		boolean first = true;

		Metrics metrics = new Metrics();
			
		for(Commit commit : git.log()) {
			if(first) {
				first = false;
				project.setHead(commit.getName());
				couch.put(couch.id(), project.json());
			}
			
			git.checkout(commit.getName());
			
			for(Change change : commit.getChanges()) {
				
				if(!isSupportedSourceType(change.getPath())) {
					continue;
				}
				
				updateSource(commit, change, metrics);				
			}			
		}
	}

	private boolean isSupportedSourceType(String path) {
		return path.endsWith(".java");
	}

	private void updateSource(Commit commit, Change change, Metrics metrics) {
		CouchResponseView view = couch.view("core/sources", project.getCode(), change.getPath());
		
		if(view.isEmpty()) {
			Source source = new Source(setup().getRoot(project.getCode()), change.getPath());
			source.setProject(project.getCode());
			source.setLastCommit(commit.getName());
			
			metrics.apply(source);
			
			couch.put(couch.id(), source.json());
		} else {
			Source source = view.get(0).as(Source.class);
			source.addChurn();
			
			couch.put(source);
		}				
	}

	private void deleteProjectIfExists() {
		couch.viewDelete("core/projects", project.getCode());
		couch.viewDelete("core/sources", project.getCode());
	}

	public void cloneRepository() {
		deleteProjectIfExists();		
		git.cloneRepository(project.getRepoUrl());
		couch.put(couch.id(), project.json());
	}


}
