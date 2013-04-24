package churndb.sourcebot;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import churndb.sourcebot.couchdb.CouchClient;
import churndb.sourcebot.couchdb.CouchResponse;
import churndb.sourcebot.importer.folder.JavaSourceFolderScanner;
import churndb.sourcebot.model.JavaSource;
import churndb.sourcebot.model.JavaSourceMetrics;
import churndb.sourcebot.utils.ResourceUtils;

import com.google.gson.JsonObject;

public class BotTest  {

	private static final String COUCHDB_HOST = "http://127.0.0.1:5984";

	private static final String CHURNDB = "churndbtest";	
	
	private CouchClient couch = new CouchClient(COUCHDB_HOST, CHURNDB);
	
	@Test
	public void testLoadSources() {
		dropDatabaseIfExists();
		
		couch.create();
		
		List<JavaSource> sources = loadSources();		
		
		for(JavaSource source : sources) {
			couch.put(source.getPath(), source.json());
		}
		
		for(JavaSource source : sources) {
			JsonObject json = couch.get(source.getPath()).json();			
			Assert.assertEquals(source.getPath(), json.get("path").getAsString());
			Assert.assertEquals(source.getMetric(JavaSourceMetrics.CCN),
					json.get("metrics").getAsJsonObject().get(JavaSourceMetrics.CCN).getAsString());
		}		
				
		couch.drop();
	}

	private List<JavaSource> loadSources() {
		JavaSourceFolderScanner scanner = new JavaSourceFolderScanner(ResourceUtils.realPath("/churndb/sourcebot/importer/project/"));				
		List<JavaSource> sources = scanner.apply(new JavaSourceMetrics());
		return sources;
	}
	
	private void dropDatabaseIfExists() {
		CouchResponse response = couch.get();
		if (!response.objectNotFound()) {
			couch.drop();
		}
	}
}
