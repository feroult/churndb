package churndb.couch;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import churndb.couch.response.CouchResponse;
import churndb.couch.response.CouchResponseHandler;
import churndb.couch.response.CouchResponseView;
import churndb.utils.JsonUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CouchClient {

	private String couchdbHost;
	private String database;

	public CouchClient(String couchdbHost, String database) {
		this.couchdbHost = normalizePath(couchdbHost);
		this.database = normalizePath(database);
	}

	public CouchResponse welcome() {
		return executeRequest(new HttpGet(couchdbHost));
	}

	public void create() {
		put("");
	}

	public void drop() {
		delete("");
	}

	public CouchResponse get() {
		return get("");
	}

	public CouchResponse get(String url) {
		return executeRequest(new HttpGet(requestUrl(url)));
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

	public CouchResponse delete(String url) {
		return executeRequest(new HttpDelete(requestUrl(url)));
	}

	private CouchResponse executeRequest(HttpUriRequest request) {
		return executeRequest(request, new CouchResponseHandler());
	}

	private CouchResponse executeRequest(HttpUriRequest request, CouchResponseHandler handler) {
		HttpClient httpclient = new DefaultHttpClient();

		try {
			return httpclient.execute(request, handler);

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

	public void dropIfExists() {
		CouchResponse response = get();
		if (!response.objectNotFound()) {
			drop();
		}
	}

	public CouchResponseView view(String viewUri, String... keys) {
		String[] split = viewUri.split("/");
		String module = split[0];
		String view = split[1];

		String key = keys.length == 0 ? "" : "?key=" + urlEncode(JsonUtils.key(keys));
		
		HttpGet request = new HttpGet(
				requestUrl("_design/" + module + "/_view/" + view + key));
		return (CouchResponseView) executeRequest(request, new CouchResponseHandler(CouchResponseView.class));
	}

	private String urlEncode(String key) {
		try {
			return URLEncoder.encode(key, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public void put(DesignDocument designDocument) {
		put(designDocument.getId(), designDocument.json());
	}

	public CouchResponse get(JsonElement jsonElement) {
		return get(jsonElement.getAsString());
	}

	public String id() {
		CouchResponse request = executeRequest(new HttpGet(couchdbHost + "_uuids"));
		return request.json().get("uuids").getAsJsonArray().get(0).getAsString();
	}

	public CouchResponse viewGet(String viewUri, String... keys) {
		CouchResponseView response = view(viewUri, keys);		
		return get(response.first().get("id"));
	}

	
	
	public void viewDelete(String viewUri, String... keys) {
		CouchResponseView response = view(viewUri, keys);		
		for(int i = 0; i < response.totalRows(); i++) {
			JsonObject row = response.rows(i);
			// the view must emit doc._rev as value to be able use viewDelete
			delete(row.get("id").getAsString() + "?rev=" + row.get("value").getAsString());
		}				
	}

}