package churndb.sourcebot.couchdb;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.JsonObject;

public class CouchClientTest {

	private static final String SCOOBYDOO = "/scoobydoo";

	private static final String COUCHDB_HOST = "http://127.0.0.1:5984";

	private CouchClient couch = new CouchClient(COUCHDB_HOST);;

	@Test
	public void testWelcome() {
		JsonObject welcome = couch.get().json();
		Assert.assertEquals("Welcome", welcome.get("couchdb").getAsString());
	}

	@Test
	public void testCreateDrop() {
		deleteDatabaseIfExists(SCOOBYDOO);

		couch.put(SCOOBYDOO);

		JsonObject info = couch.get(SCOOBYDOO).json();
		Assert.assertEquals("scoobydoo", info.get("db_name").getAsString());

		couch.delete(SCOOBYDOO);
		
		CouchResponse response = couch.get(SCOOBYDOO);
		Assert.assertTrue(response.objectNotFound());
	}

	private void deleteDatabaseIfExists(String database) {
		CouchResponse response = couch.get(database);
		if (!response.objectNotFound()) {
			couch.delete(database);
		}
	}
}
