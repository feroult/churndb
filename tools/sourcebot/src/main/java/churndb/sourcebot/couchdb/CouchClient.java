package churndb.sourcebot.couchdb;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.JsonObject;

public class CouchClient {

	private String couchdbHost;

	public CouchClient(String couchdbHost) {
		this.couchdbHost = couchdbHost;
	}

	public CouchResponse get(String... params) {
		return executeRequest(new HttpGet(requestUrl(params)));
	}

	public JsonObject put(String... params) {
		return executeRequest(new HttpPut(requestUrl(params))).json();
	}

	public JsonObject delete(String... params)  {
		return executeRequest(new HttpDelete(requestUrl(params))).json();
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

	private String requestUrl(String[] params) {
		StringBuilder sb = new StringBuilder(couchdbHost);

		if (params == null || params.length == 0) {
			return sb.toString();
		}

		sb.append(params[0]);

		if (params.length > 1) {
			sb.append("?");
		}

		for (int i = 1; i < params.length; i++) {
			sb.append(params[i]);
		}

		return sb.toString();
	}

}
