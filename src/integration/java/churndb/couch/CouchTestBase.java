package churndb.couch;

import churndb.utils.TestConstants;

public class CouchTestBase {

	protected CouchClient couch = new CouchClient(TestConstants.COUCHDB_HOST, TestConstants.CHURNDB);

}
