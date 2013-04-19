package churndb.sourcebot.couchdb;

import java.io.IOException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CouchClient {

	private String couchdbHost;

	public CouchClient(String couchdbHost) {
		this.couchdbHost = couchdbHost;	
	}
	
	public JsonObject get(String ... params) throws CouchResponseException {
		return executeRequest(new HttpGet(requestUrl(params)));
	}
	
	public JsonObject put(String ... params) throws CouchResponseException {
		return executeRequest(new HttpPut(requestUrl(params)));
	}	

	public JsonObject delete(String ... params) throws CouchResponseException {
		return executeRequest(new HttpDelete(requestUrl(params)));		
	}
	
	private JsonObject executeRequest(HttpUriRequest request) throws CouchResponseException {
		HttpClient httpclient = new DefaultHttpClient();
		
		try {			
			String responseBody = httpclient.execute(request, new BasicResponseHandler());
			return (JsonObject) new JsonParser().parse(responseBody);

		} catch(HttpResponseException e) {
			throw new CouchResponseException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
	}
	
	private String requestUrl(String[] params) {
		StringBuilder sb = new StringBuilder(couchdbHost);
		
		if(params == null || params.length == 0) {
			return sb.toString();
		}
		
		sb.append(params[0]);
		
		if(params.length > 1) {
			sb.append("?");
		}
		
		for(int i = 1; i < params.length; i++) {
			sb.append(params[i]);
		}
		
		return sb.toString();
	}

}
