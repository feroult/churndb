package churndb.couch;

import churndb.couch.CouchClient;
import churndb.utils.TestConstants;

public class CouchTestBase {

	protected CouchClient couch = new CouchClient(TestConstants.COUCHDB_HOST, TestConstants.CHURNDB);

}
