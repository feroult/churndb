package churndb.couch;

import churndb.utils.JsonUtils;

public class CouchBean {
	
	private String _id;

	private String _rev;
		
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
		return JsonUtils.to(this);
	}
}
