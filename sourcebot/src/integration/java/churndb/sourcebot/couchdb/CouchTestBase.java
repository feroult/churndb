package churndb.sourcebot.couchdb;

import churndb.couch.CouchClient;

public class CouchTestBase {

	protected static final String COUCHDB_HOST = "http://127.0.0.1:5984";
	protected static final String CHURNDB = "churndbtest";
	protected CouchClient couch = new CouchClient(COUCHDB_HOST, CHURNDB);

}
