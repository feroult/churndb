package churndb.sourcebot.couchdb;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.JsonObject;

public class CouchDBClientTest {

	private static final String COUCHDB_HOST = "http://127.0.0.1:5984";

	@Test
	public void testVersion() {
		CouchDBClient chouch = new CouchDBClient();
		JsonObject welcome = chouch.get(COUCHDB_HOST);		
		Assert.assertEquals("Welcome", welcome.get("couchdb").getAsString());
	}
	
	public void testCreateDrop() {		
		// TODO	
	}
}
