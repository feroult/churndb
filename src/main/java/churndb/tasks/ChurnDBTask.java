package churndb.tasks;

import churndb.couch.CouchClient;

public abstract class ChurnDBTask {

	protected Setup setup;
	
	protected CouchClient couch;

	public ChurnDBTask() {
		this.couch = new CouchClient(setup().getHost(), setup().getDatabase());
	}
	
	protected Setup setup() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
