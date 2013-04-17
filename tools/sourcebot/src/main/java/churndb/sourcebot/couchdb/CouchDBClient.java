package churndb.sourcebot.couchdb;

import java.io.IOException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CouchDBClient {

	public JsonObject get(String url) {

		HttpClient httpclient = new DefaultHttpClient();

		try {
			HttpGet httpget = new HttpGet(url);

			String responseBody = httpclient.execute(httpget, new BasicResponseHandler());

			return (JsonObject) new JsonParser().parse(responseBody);

		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
	}
}
