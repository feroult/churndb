package churndb.sourcebot.jobs;

import java.util.List;

import churndb.sourcebot.couchdb.CouchClient;
import churndb.sourcebot.importer.folder.JavaSourceFolderScanner;
import churndb.sourcebot.model.JavaSourceMetrics;
import churndb.sourcebot.model.Source;
import churndb.sourcebot.utils.ResourceUtils;

public class FolderJob {

	public FolderJob(String code, String name, String path) {
		// TODO Auto-generated constructor stub
	}

	public List<Source> loadSources() {
		JavaSourceFolderScanner scanner = new JavaSourceFolderScanner(ResourceUtils.realPath("/churndb/sourcebot/importer/project/"));				
		List<Source> sources = scanner.apply(new JavaSourceMetrics());
		return sources;
	}

	public void run(CouchClient couch) {
		for(Source source : loadSources()) {
			couch.put(source.getPath(), source.json());
		}
	}

}
