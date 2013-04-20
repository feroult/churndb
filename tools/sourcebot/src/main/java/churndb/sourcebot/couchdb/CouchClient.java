package churndb.sourcebot.couchdb;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class CouchClient {

	private String couchdbHost;
	private String database;

	public CouchClient(String couchdbHost, String database) {
		this.couchdbHost = normalizePath(couchdbHost);
		this.database = normalizePath(database);
	}

	public void setDatabase(String database) {
		this.database = normalizePath(database);
	}

	public CouchResponse welcome() {
		return executeRequest(new HttpGet(couchdbHost));
	}
	
	public CouchResponse get() {
		return get("");
	}

	public CouchResponse get(String url) {
		return executeRequest(new HttpGet(requestUrl(url)));
	}

	public void create() {
		put("");
	}

	public CouchResponse put(String url) {
		return executeRequest(new HttpPut(requestUrl(url)));
	}

	public CouchResponse put(String url, String body) {
		HttpPut request = new HttpPut(requestUrl(url));

		try {
			request.setEntity(new StringEntity(body));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

		return executeRequest(request);
	}

	public void drop() {
		delete("");
	}

	public CouchResponse delete(String url) {
		return executeRequest(new HttpDelete(requestUrl(url)));
	}

	private CouchResponse executeRequest(HttpUriRequest request) {
		HttpClient httpclient = new DefaultHttpClient();

		try {
			return httpclient.execute(request, new CouchResponseHandler());

		} catch (ClientProtocolException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
	}

	private String requestUrl(String url) {
		StringBuilder sb = new StringBuilder(couchdbHost);

		if (database != null) {
			sb.append(database);
		}

		if (url != null) {
			sb.append(url);
		}

		return sb.toString();
	}

	private String normalizePath(String host) {
		if (host.charAt(host.length() - 1) != '/') {
			return host + "/";
		}
		return host;
	}
}
