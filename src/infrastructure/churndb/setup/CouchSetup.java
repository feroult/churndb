package churndb.setup;

import churndb.couch.CouchClient;

public class CouchSetup {

	protected static final String COUCHDB_HOST = "http://127.0.0.1:5984";
	protected static final String CHURNDB = "churndb";
	protected static final CouchClient couch = new CouchClient(COUCHDB_HOST, CHURNDB);

}
