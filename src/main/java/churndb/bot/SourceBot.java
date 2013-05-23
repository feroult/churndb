package churndb.bot;

import churndb.couch.CouchClient;
import churndb.git.Change;
import churndb.git.Commit;
import churndb.git.GIT;
import churndb.model.Project;
import churndb.model.Source;
import churndb.model.Metrics;
import churndb.tasks.ChurnDBSetup;

public class SourceBot {

	private Project project;
	
	private ChurnDBSetup setup;

	public SourceBot(Project project, ChurnDBSetup setup) {
		this.project = project;
		this.setup = setup;		
	}

	public void reload(GIT git, CouchClient couch) {		
		deleteProjectIfExists(couch);				
		reloadProjectFromGIT(git, couch);
	}

	private void reloadProjectFromGIT(GIT git, CouchClient couch) {		
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
		Source source = new Source(setup.getRoot(project.getCode()), change.getPath());
		source.setProject(project.getCode());
		source.initChurn(commit);
		metrics.apply(source);
		return source;
	}

	private void deleteProjectIfExists(CouchClient couch) {
		couch.viewDelete("core/projects", project.getCode());
		couch.viewDelete("core/sources", project.getCode());
	}

}
