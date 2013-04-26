package churndb.sourcebot.jobs;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import churndb.sourcebot.couchdb.CouchClient;
import churndb.sourcebot.model.Source;
import churndb.sourcebot.model.JavaSourceMetrics;
import churndb.sourcebot.utils.ResourceUtils;

import com.google.gson.JsonObject;

public class FolderJobTest  {

	private static final String COUCHDB_HOST = "http://127.0.0.1:5984";

	private static final String CHURNDB = "churndbtest";	
	
	private CouchClient couch = new CouchClient(COUCHDB_HOST, CHURNDB);
	
	@Before
	public void before() {
		couch.dropIfExists();		
		couch.create();		
	}
	
	@After
	public void after() {
		couch.drop();
	}
	
	@Test
	public void testImportFolderToCouch() {		
		FolderJob job = new FolderJob("51", "Confidence", ResourceUtils.realPath("/churndb/sourcebot/importer/project/"));
				
		job.run(couch);
		
		for(Source source : getMockSources()) {
			JsonObject json = couch.get(source.getPath()).json();			
			Assert.assertEquals(source.getPath(), json.get("path").getAsString());
			Assert.assertEquals(source.getMetric(JavaSourceMetrics.CCN),
					json.get("metrics").getAsJsonObject().get(JavaSourceMetrics.CCN).getAsString());
		}					
	}

	private List<Source> getMockSources() {
		List<Source> sources = new ArrayList<Source>();
		
		Source product = new Source("Product.java_");
		product.setMetric(JavaSourceMetrics.CCN, 25);
		product.setMetric(JavaSourceMetrics.LOC, 10);
		
		sources.add(product);
		
		return sources;
	}
}
