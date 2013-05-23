package churndb.tasks;

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
				Source source = configureSource(commit, change, metrics);
				couch.put(couch.id(), source.json());
			}			
		}
	}

	private Source configureSource(Commit commit, Change change, Metrics metrics) {
		Source source = new Source(setup().getRoot(project.getCode()), change.getPath());
		source.setProject(project.getCode());
		source.initChurn(commit);
		metrics.apply(source);
		return source;
	}

	private void deleteProjectIfExists() {
		couch.viewDelete("core/projects", project.getCode());
		couch.viewDelete("core/sources", project.getCode());
	}

}
