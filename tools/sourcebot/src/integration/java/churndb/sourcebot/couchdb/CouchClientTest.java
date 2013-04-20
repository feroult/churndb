package churndb.sourcebot.couchdb;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonObject;

public class CouchClientTest {

	private static final String CHURNDB = "churndbtest";

	private static final String COUCHDB_HOST = "http://127.0.0.1:5984";

	private CouchClient couch;

	@Before
	public void before() {
		couch = new CouchClient(COUCHDB_HOST);
	}
	
	@Test
	public void testWelcome() {
		JsonObject welcome = couch.get().json();
		Assert.assertEquals("Welcome", welcome.get("couchdb").getAsString());
	}

	@Test
	public void testCreateDropDatabase() {
		deleteDatabaseIfExists(CHURNDB);

		couch.put(CHURNDB);

		JsonObject info = couch.get(CHURNDB).json();
		Assert.assertEquals("churndbtest", info.get("db_name").getAsString());

		couch.delete(CHURNDB);
		
		CouchResponse response = couch.get(CHURNDB);
		Assert.assertTrue(response.objectNotFound());
	}

	private void deleteDatabaseIfExists(String database) {
		CouchResponse response = couch.get(database);
		if (!response.objectNotFound()) {
			couch.delete(database);
		}
	}
	
	@Test
	public void testCreateDeleteDocument() {
		deleteDatabaseIfExists(CHURNDB);
		
		couch.setDatabase(CHURNDB);		
		couch.put();		
		
		couch.put("doc", "{\"field\": \"blah\"}");
		
		JsonObject doc = couch.get("doc").json();		
		Assert.assertEquals("blah", doc.get("field").getAsString());
		
		couch.delete();
	}
}
