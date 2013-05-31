package churndb.couch;

import churndb.tasks.Setup;

import com.google.gson.Gson;

public class CouchBean {

	private transient Setup setup;	

	protected transient CouchClient couch;
	
	private String _id;

	private String _rev;
	
	public CouchBean() {
		this.couch = new CouchClient(setup().getHost(), setup().getDatabase());
	}
		
	public CouchBean(CouchClient couch) {
		this.couch = couch;
	}

	public void setCouch(CouchClient couch) {
		this.couch = couch;
	}

	protected Setup setup() {
		if(setup == null ) {
			setup = Setup.homeFolderSetup();
		}
		return setup;
	}	
	
	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String get_rev() {
		return _rev;
	}

	public void set_rev(String _rev) {
		this._rev = _rev;
	}

	public String json() {
		return new Gson().toJson(this);
	}
}
