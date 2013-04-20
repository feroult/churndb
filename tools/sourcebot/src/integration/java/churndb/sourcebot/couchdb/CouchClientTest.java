package churndb.sourcebot.couchdb;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonObject;

public class CouchClientTest {

	private static final String COUCHDB_HOST = "http://127.0.0.1:5984";

	private static final String CHURNDB = "churndbtest";

	private static final String DOC = "doc";

	private CouchClient couch;

	@Before
	public void before() {
		couch = new CouchClient(COUCHDB_HOST, CHURNDB);
	}
	
	@Test
	public void testWelcome() {
		JsonObject welcome = couch.welcome().json();
		Assert.assertEquals("Welcome", welcome.get("couchdb").getAsString());
	}

	@Test
	public void testCreateDropDatabase() {
		deleteDatabaseIfExists();

		couch.create();

		JsonObject info = couch.get().json();
		Assert.assertEquals(CHURNDB, info.get("db_name").getAsString());

		couch.drop();
		
		CouchResponse response = couch.get(CHURNDB);
		Assert.assertTrue(response.objectNotFound());
	}

	private void deleteDatabaseIfExists() {
		CouchResponse response = couch.get();
		if (!response.objectNotFound()) {
			couch.drop();
		}
	}
	
	@Test
	public void testCreateDeleteDocument() {
		deleteDatabaseIfExists();
		
		couch.create();		
		
		couch.put(DOC, "{\"field\": \"blah\"}");
		
		JsonObject doc = couch.get(DOC).json();		
		Assert.assertEquals("blah", doc.get("field").getAsString());
		
		couch.drop();
	}
}
