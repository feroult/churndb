package churndb.sourcebot.couchdb;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.JsonObject;

public class CouchDBClientTest {

	@Test
	public void testVersion() {
		CouchDBClient chouch = new CouchDBClient();
		JsonObject welcome = chouch.get("http://127.0.0.1:5984");
		Assert.assertEquals("Welcome", welcome.get("couchdb").getAsString());
	}
}
