package churndb.couch;

import static org.junit.Assert.assertEquals;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;

import churndb.utils.TestConstants;

public class ResourcesDocumentTest extends CouchTestBase {

	@Test
	public void testSimpleResource() {				
		ResourcesDocument document = new ResourcesDocument("web");		
		document.addAttachment("abc/xpto/simple.html", "text/html", "/churndb/couch/resources/html/simple.html");
		
		couch.put(document);		
		
		assertEquals("<html>hello</html>", httpGetAsString("/web/abc/xpto/simple.html"));				
	}
	
	@Test
	public void testResourcesFolder() {
		ResourcesDocument document = new ResourcesDocument("web");
		
		document.addAttachmentsByExtension("abc/xpto/", "/churndb/couch/resources");
		
		couch.put(document);
		
		assertEquals("<html>hello</html>", httpGetAsString("/web/abc/xpto/html/simple.html"));
		assertEquals("function hello() {}", httpGetAsString("/web/abc/xpto/js/simple.js"));
	}
	
	private String httpGetAsString(String uri) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet get = new HttpGet(TestConstants.COUCHDB_HOST + "/" + TestConstants.CHURNDB + uri);
		
		try {
			return httpClient.execute(get, new BasicResponseHandler());
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
