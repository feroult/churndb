package churndb.sourcebot;

import java.util.List;

import churndb.sourcebot.couchdb.CouchClient;
import churndb.sourcebot.importer.folder.SourceScanner;
import churndb.sourcebot.importer.svn.SVN;
import churndb.sourcebot.model.Project;
import churndb.sourcebot.model.Source;
import churndb.sourcebot.model.SourceMetrics;

public class SourceBot {

	private Project project;

	public SourceBot(Project project) {
		this.project = project;		
	}

	public void fromTo(SVN svn, CouchClient couch) {
		svn.checkout(project.getRoot());		
		couch.put(couch.id(), project.json());
		
		SourceScanner scanner = new SourceScanner(project.getRoot());
		List<Source> sources = scanner.apply(new SourceMetrics());
		
		for(Source source : sources) {
			source.setProject(project.getName());
			couch.put(couch.id(), source.json());
		}
	}

}
