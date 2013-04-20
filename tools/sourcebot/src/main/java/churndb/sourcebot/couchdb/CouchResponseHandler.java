package churndb.sourcebot.couchdb;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

public class CouchResponseHandler implements ResponseHandler<CouchResponse> {

	@Override
	public CouchResponse handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
		StatusLine statusLine = response.getStatusLine();
		HttpEntity entity = response.getEntity();

		String responseBody = entity == null ? null : EntityUtils.toString(entity);
		
		return new CouchResponse(responseBody, statusLine);
	}

}
