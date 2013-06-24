package churndb.couch;

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

import churndb.couch.response.CouchResponse;
import churndb.couch.response.CouchResponseHandler;
import churndb.couch.response.CouchResponseView;

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
		executeRequest(new HttpPut(fullRequestUrl("")));
	}

	public void drop() {
		delete("");
	}

	public CouchResponse get() {
		return get("");
	}

	public CouchResponse get(String url) {
		return executeRequest(new HttpGet(fullRequestUrl(url)));
	}

	public CouchResponse put(String url, String body) {
		HttpPut request = new HttpPut(fullRequestUrl(url));

		try {
			request.setEntity(new StringEntity(body));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

		return executeRequest(request);
	}

	public CouchResponse delete(String url) {
		return executeRequest(new HttpDelete(fullRequestUrl(url)));
	}
	
	public CouchResponse delete(CouchBean bean) {
		return delete(bean.get_id());		
	}

	private CouchResponse executeRequest(HttpUriRequest request) {
		return executeRequest(request, new CouchResponseHandler(this));
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

	private String fullRequestUrl(String url) {
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
		HttpGet request = new HttpGet(fullRequestUrl(viewRequestUrl(viewUri, 0, keys)));
		return (CouchResponseView) executeRequest(request, new CouchResponseHandler(this, CouchResponseView.class));
	}
	
	public CouchResponseView view(String viewUri, int options, String... keys) {
		HttpGet request = new HttpGet(fullRequestUrl(viewRequestUrl(viewUri, options, keys)));
		return (CouchResponseView) executeRequest(request, new CouchResponseHandler(this, CouchResponseView.class));
	}	
	
	private String viewRequestUrl(String viewUri, int options, String... keys) {
		String[] split = viewUri.split("/");
		String module = split[0];
		String view = split[1];

		StringBuilder normalizedKeys = new StringBuilder("?");
		if (keys.length > 0) {
			normalizedKeys.append(CouchUtils.keys(ViewOptions.descending(options), keys));
		}
		
		normalizedKeys.append("&include_docs=" + ViewOptions.includeDocs(options));
		
		normalizedKeys.append("&descending=" + ViewOptions.descending(options));
		
		normalizedKeys.append("&reduce=" + ViewOptions.reduce(options));
		
		String viewRequestUrl = "_design/" + module + "/_view/" + view + normalizedKeys;
		return viewRequestUrl;
	}

	public void put(DesignDocument designDocument) {
		DesignDocument previous = get(designDocument.get_id()).as(DesignDocument.class);
		if(previous != null) {
			designDocument.set_rev(previous.get_rev());
		}
		
		put(designDocument.get_id(), designDocument.json());
	}

	public void put(ResourcesDocument resourcesDocument) {
		ResourcesDocument previous = get(resourcesDocument.get_id()).as(ResourcesDocument.class);
		if(previous != null) {
			resourcesDocument.set_rev(previous.get_rev());
		}
		
		put(resourcesDocument.get_id(), resourcesDocument.json());
	}	
	
	public CouchResponse get(JsonElement jsonElement) {
		return get(jsonElement.getAsString());
	}

	public String id() {
		CouchResponse request = executeRequest(new HttpGet(couchdbHost + "_uuids"));
		return request.json().get("uuids").getAsJsonArray().get(0).getAsString();
	}

	public CouchResponse viewGetFirst(String viewUri, String... keys) {
		CouchResponseView response = view(viewUri, keys);
		return get(response.first().get("id"));
	}

	public void viewDelete(String viewUri, String... keys) {
		CouchResponseView response = view(viewUri, keys);
		for (int i = 0; i < response.size(); i++) {
			JsonObject row = response.json(i);
			// the view must emit doc._rev, [doc._rev, or doc or as value to be able use viewDelete
			delete(row.get("id").getAsString() + "?rev=" + extractRevision(row.get("value")));
		}
	}

	private String extractRevision(JsonElement value) {
		if (value.isJsonArray()) {
			return value.getAsJsonArray().get(0).getAsString();
		}

		if (value.isJsonObject()) {
			return value.getAsJsonObject().get("_rev").getAsString();
		}

		return value.getAsString();
	}

	public CouchResponse viewGetAt(String viewUri, int index, String... keys) {
		CouchResponseView response = view(viewUri, keys);
		return get(response.json(index).get("id"));
	}

	public CouchResponseReduce reduce(String viewUri, String... keys) {
		HttpGet request = new HttpGet(fullRequestUrl(viewRequestUrl(viewUri, ViewOptions.REDUCE, keys)));
		// TODO remove specific handler for reduce
		return (CouchResponseReduce) executeRequest(request, new CouchResponseHandler(this, CouchResponseReduce.class));
	}

	public CouchBean put(CouchBean bean) {
		if (bean.get_id() == null) {
			bean.set_id(id());
		}
		CouchResponse response = put(bean.get_id(), bean.json());
		bean.set_rev(response.json().get("rev").getAsString());
		
		return bean;
	}

	public CouchBean put(String id, CouchBean bean) {
		if(bean.get_id() == null) {
			bean.set_id(id);
		}
		return put(bean);
	}

}
