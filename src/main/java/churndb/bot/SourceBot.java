package churndb.bot;

import churndb.couch.CouchClient;
import churndb.git.Change;
import churndb.git.Commit;
import churndb.git.GIT;
import churndb.model.Project;
import churndb.model.Source;
import churndb.model.SourceMetrics;

public class SourceBot {

	private Project project;

	public SourceBot(Project project) {
		this.project = project;		
	}

	public void reload(GIT git, CouchClient couch) {		
		deleteProjectIfExists(couch);				
		reloadProjectFromGIT(git, couch);
	}

	private void reloadProjectFromGIT(GIT git, CouchClient couch) {
		couch.put(couch.id(), project.json());

		SourceMetrics metrics = new SourceMetrics();
		
		for(Commit commit : git.log()) {
			for(Change change : commit.getChanges()) {
				Source source = new Source(change.getPath());
				source.setProject(project.getCode());
				source.setCommitTime(commit.getCommitTime());
				metrics.apply(source);
				couch.put(couch.id(), source.json());
			}			
		}
	}

	private void deleteProjectIfExists(CouchClient couch) {
		couch.viewDelete("core/projects", project.getCode());
		couch.viewDelete("core/sources", project.getCode());
	}

}
