package churndb.sourcebot.couchdb;

import org.junit.Assert;
import org.junit.Test;

import churndb.sourcebot.couchdb.response.CouchResponse;

import com.google.gson.JsonObject;

public class CouchClientTest extends CouchTestBase {

	private static final String DOC = "doc";
	
	@Test
	public void testWelcome() {
		JsonObject welcome = couch.welcome().json();
		Assert.assertEquals("Welcome", welcome.get("couchdb").getAsString());
	}

	@Test
	public void testCreateDropDatabase() {
		couch.dropIfExists();
		couch.create();

		JsonObject info = couch.get().json();
		Assert.assertEquals(CHURNDB, info.get("db_name").getAsString());

		couch.drop();
		
		CouchResponse response = couch.get(CHURNDB);
		Assert.assertTrue(response.objectNotFound());
	}
	
	@Test
	public void testCreateDeleteDocument() {
		couch.dropIfExists();		
		couch.create();		
		
		couch.put(DOC, "{\"field\": \"blah\"}");
		
		JsonObject doc = couch.get(DOC).json();		
		Assert.assertEquals("blah", doc.get("field").getAsString());
		
		couch.drop();
	}	
}
