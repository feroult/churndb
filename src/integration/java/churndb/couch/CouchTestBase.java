package churndb.couch;

import org.junit.After;
import org.junit.Before;

import churndb.utils.TestConstants;

public class CouchTestBase {

	protected CouchClient couch = new CouchClient(TestConstants.COUCHDB_HOST, TestConstants.CHURNDB);

	@Before
	public void recreateDabase() {
		couch.dropIfExists();		
		couch.create();		
	}

	@After
	public void dropDatabase() {
		couch.drop();
	}

}
