package churndb.couch;

import java.lang.reflect.Constructor;

import org.apache.http.StatusLine;

import churndb.couch.response.CouchResponseView;

public class CouchResponseReduce extends CouchResponseView {

	public CouchResponseReduce(CouchClient couch, String responseBody, StatusLine statusLine) {
		super(couch, responseBody, statusLine);
	}

	@Override
	public <T> T as(Class<T> clazz) {
		try {
			Constructor<T> constructor = clazz.getConstructor(String.class);
			return constructor.newInstance(first().get("value").getAsString());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		 
	}
		
}
