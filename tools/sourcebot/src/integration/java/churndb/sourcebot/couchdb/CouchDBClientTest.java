package churndb.sourcebot.couchdb;

import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

import com.google.gson.JsonObject;

public class CouchDBClientTest {

	private static final String SCOOBYDOO = "/scoobydoo";

	private static final String COUCHDB_HOST = "http://127.0.0.1:5984";

	private CouchDBClient couch = new CouchDBClient(COUCHDB_HOST);;

	@Test
	public void testVersion() {
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
			Assert.assertEquals(HttpStatus.SC_NOT_FOUND, e.getStatus());
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
