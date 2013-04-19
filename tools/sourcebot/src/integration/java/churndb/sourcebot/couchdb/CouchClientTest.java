package churndb.sourcebot.couchdb;

import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

import com.google.gson.JsonObject;

public class CouchClientTest {

	private static final String SCOOBYDOO = "/scoobydoo";

	private static final String COUCHDB_HOST = "http://127.0.0.1:5984";

	private CouchClient couch = new CouchClient(COUCHDB_HOST);;

	@Test
	public void testWelcome() {
		JsonObject welcome = couch.get();
		Assert.assertEquals("Welcome", welcome.get("couchdb").getAsString());
	}

	@Test
	public void testCreateDrop() {
		deleteDatabaseIfExists(SCOOBYDOO);
		
		couch.put(SCOOBYDOO);
		
		JsonObject info = couch.get(SCOOBYDOO);
		Assert.assertEquals("scoobydoo", info.get("db_name").getAsString());
		
		couch.delete(SCOOBYDOO);
		
		try {
			couch.get(SCOOBYDOO); 
		} catch(CouchResponseException e) {
			Assert.assertTrue(e.objectNotFound());
		}
	}

	private void deleteDatabaseIfExists(String database) {
		try {
			
			couch.get(database);			
			
		} catch(CouchResponseException e) {
			if(e.getStatus() != HttpStatus.SC_NOT_FOUND) {
				throw new RuntimeException(e);
			}
			
			return;
		}
		
		couch.delete(database);
	}
}
