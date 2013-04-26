package churndb.sourcebot.jobs;

import java.util.List;

import churndb.sourcebot.couchdb.CouchClient;
import churndb.sourcebot.importer.folder.JavaSourceFolderScanner;
import churndb.sourcebot.model.JavaSourceMetrics;
import churndb.sourcebot.model.Source;

public class FolderJob {

	private String path;

	public FolderJob(String code, String name, String path) {
		this.path = path;
	}

	public List<Source> loadSources() {
		JavaSourceFolderScanner scanner = new JavaSourceFolderScanner(path);				
		List<Source> sources = scanner.apply(new JavaSourceMetrics());
		return sources;
	}

	public void run(CouchClient couch) {
		for(Source source : loadSources()) {
			couch.put(source.getPath(), source.json());
		}
	}

}
