package churndb.sourcebot.couchdb;

import org.junit.Assert;
import org.junit.Test;

import churndb.couch.response.CouchResponse;

import com.google.gson.JsonObject;

public class DatabaseTest extends CouchTestBase {

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

}
