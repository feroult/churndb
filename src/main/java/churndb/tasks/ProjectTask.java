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
		Metrics metrics = new Metrics();
			
		for(Commit commit : git.log()) {
			
			if(isNewerCommitForProject(commit, project)) {
				project.setLastCommit(commit.getName());
				project.setLastChange(commit.getDate());
				couch.put(project);
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

	private boolean isNewerCommitForProject(Commit commit, Project project2) {
		if(project.getLastChange() == null) { 
			return true;
		}
		
		return commit.getDate().after(project.getLastChange());
	}

	private boolean isSupportedSourceType(String path) {
		return path.endsWith(".java");
	}

	private void updateSource(Commit commit, Change change, Metrics metrics) {
		
		Source source = project.getSource(change.getPathBeforeChange());
						
		if(isNewerCommitForSource(commit, source)) {
			source.setLastCommit(commit.getName());		
			source.setLastChange(commit.getDate());
			metrics.apply(source);						
		} 
		
		source.addChurn();
		couch.put(source);
	}

	private boolean isNewerCommitForSource(Commit commit, Source source) {
		if(source.getLastChange() == null) {
			return true;
		}
		
		return commit.getDate().after(source.getLastChange());
	}

	public void cloneRepository() {
		project.deleteIfExists();		
		git.cloneRepository(project.getRepoUrl());		
		couch.put(project);
	}

}
