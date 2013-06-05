package churndb.tasks;

import churndb.git.Change;
import churndb.git.Commit;
import churndb.git.GIT;
import churndb.git.Type;
import churndb.model.Metrics;
import churndb.model.Project;
import churndb.model.Source;

public class ProjectTask extends ChurnDBTask {

	private Project project;
	
	private GIT git;
	
	public ProjectTask(Project project) {
		super();
		this.project = project;
		this.git = new GIT(Setup.getRoot(project.getCode()));
	}

	public void reload() {		
		churn.deleteProject(project.getCode());				
		reloadProjectFromGIT();
	}

	private void reloadProjectFromGIT() {		
		Metrics metrics = new Metrics();
			
		for(Commit commit : git.log()) {
			
			if(isNewerCommitForProject(commit, project)) {
				project.setLastCommit(commit.getName());
				project.setLastChange(commit.getDate());
				churn.put(project);
			}
			
			git.checkout(commit.getName());
			
			for(Change change : commit.getChanges()) {
				
				if(!isSupportedSourceType(change.getPathBeforeChange())) {
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
		return path.endsWith(".java") || path.endsWith(".java_");
	}

	private void updateSource(Commit commit, Change change, Metrics metrics) {
		
		Source source = churn.getSource(project.getCode(), change.getPathBeforeChange());		
						
		switch (change.getType()) {
		case COPY:
		case ADD:			
			addSource(source, commit, change, metrics);			
			break;
		case DELETE:
			deleteSource(source, commit, change, metrics);
			break;
		case MODIFY:
			modifySource(source, commit, change, metrics);
			break;
		case RENAME:
			renameSource(source, commit, change, metrics);
			break;
		}				
	}

	private void renameSource(Source source, Commit commit, Change change, Metrics metrics) {		
		source.setPath(change.getNewPath());
		
		if(isNewerCommitForSource(commit, source)) {
			updateSourceCommit(source, commit, metrics);						
		} 		
		
		source.addChurnCount();
		churn.put(source);		
	}

	private void modifySource(Source source, Commit commit, Change change, Metrics metrics) {
		if(isNewerCommitForSource(commit, source)) {
			updateSourceCommit(source, commit, metrics);						
		} 
		
		source.addChurnCount();
		churn.put(source);
	}

	private void deleteSource(Source source, Commit commit, Change change, Metrics metrics) {
		String renamedPath = git.findSimilarInOldCommits(commit.getName(), change.getPathBeforeChange(), Type.ADD);
		
		if(renamedPath != null) {
			Source renamedSource = churn.getSource(project.getCode(), renamedPath);			
			renamedSource.setLastCommit(commit.getName());
			renamedSource.addChurnCount(source.getChurnCount());
			churn.put(renamedSource);
			churn.deleteSource(project.getCode(), source);
		} else {
			source.setDeleted(true);
			source.setLastCommit(commit.getName());
			source.setLastChange(commit.getDate());
			churn.put(source);
		}						
	}

	private void addSource(Source source, Commit commit, Change change, Metrics metrics) {
		if(source != null) {
			throw new RuntimeException("Added source already existed in churndb");
		}		
		
		String renamedPath = git.findSimilarInOldCommits(commit.getName(), change.getPathAfterChange(), Type.DELETE);
		
		if(renamedPath != null) {
			source = churn.getSource(project.getCode(), renamedPath);
			source.setDeleted(false);
			source.setPath(change.getPathAfterChange());			
		} else {		
			source = new Source(project.getCode(), change.getPathAfterChange());
		}
				
		updateSourceCommit(source, commit, metrics);						
		
		source.addChurnCount();
		churn.put(source);
	}

	private void updateSourceCommit(Source source, Commit commit, Metrics metrics) {
		source.setLastCommit(commit.getName());		
		source.setLastChange(commit.getDate());
		metrics.apply(source);
	}

	private boolean isNewerCommitForSource(Commit commit, Source source) {
		if(source.getLastChange() == null) {
			return true;
		}
		
		return commit.getDate().after(source.getLastChange());
	}

	public void cloneRepository() {
		git.cloneRepository(project.getRepoUrl());		
	}

}
