package churndb.sourcebot.jobs;

import java.util.List;

import churndb.sourcebot.couchdb.CouchClient;
import churndb.sourcebot.importer.folder.SourceScanner;
import churndb.sourcebot.model.SourceMetrics;
import churndb.sourcebot.model.Source;

public class FolderJob {

	private String path;

	public FolderJob(String code, String name, String path) {
		this.path = path;
	}

	public List<Source> loadSources() {
		SourceScanner scanner = new SourceScanner(path);				
		List<Source> sources = scanner.apply(new SourceMetrics());
		return sources;
	}

	public void run(CouchClient couch) {
		for(Source source : loadSources()) {
			couch.put(source.getPath(), source.json());
		}
	}

}
