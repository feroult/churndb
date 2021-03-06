package churndb.couch.response;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import churndb.couch.CouchClient;

public class CouchResponseHandler implements ResponseHandler<CouchResponse> {

	private CouchClient couch;
	
	private Class<? extends CouchResponse> responseClazz;

	public CouchResponseHandler(CouchClient couch) {
		this(couch, CouchResponse.class);
	}
	
	public CouchResponseHandler(CouchClient couch, Class<? extends CouchResponse> responseClazz) {
		this.couch = couch;
		this.responseClazz = responseClazz;
	}

	@Override
	public CouchResponse handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
		StatusLine statusLine = response.getStatusLine();
		HttpEntity entity = response.getEntity();

		String responseBody = entity == null ? null : EntityUtils.toString(entity);

		return newCouchResponse(responseBody, statusLine);
	}

	private CouchResponse newCouchResponse(String responseBody, StatusLine statusLine) {
		try {
			Constructor<? extends CouchResponse> constructor = responseClazz.getConstructor(CouchClient.class, String.class,
					StatusLine.class);
			return constructor.newInstance(couch, responseBody, statusLine);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
}
