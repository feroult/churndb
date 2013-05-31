package churndb.tasks;

import churndb.git.Change;
import churndb.git.Commit;
import churndb.git.GIT;
import churndb.model.Metrics;
import churndb.model.Project;
import churndb.model.Source;

public class ProjectTask extends ChurnDBTask {

	private Project project;
	
	private GIT git;
	
	public ProjectTask(Project project) {
		super();
		this.project = project;
		this.git = new GIT(setup().getRoot(project.getCode()));
	}

	public void reload() {		
		project.deleteIfExists();				
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
		
		Source source = project.getSource(change.getPathBeforeChange());
				
		if(source != null) {
			source.addChurn();
		} else {
			source = new Source(setup().getRoot(project.getCode()), change.getPath());
			source.setProject(project.getCode());
			source.setLastCommit(commit.getName());			
			metrics.apply(source);						
		}				
		
		couch.put(source);
	}

	public void cloneRepository() {
		project.deleteIfExists();		
		git.cloneRepository(project.getRepoUrl());		
		couch.put(project);
	}

}
