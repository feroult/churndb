package churndb.sourcebot.couchdb;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import churndb.sourcebot.utils.ResourceUtils;

import com.google.gson.JsonObject;

public class CouchClientDesignDocumentTest extends CreateDropCouchTest {

	@Before
	public void before() {
		couch.dropIfExists();		
		couch.create();		
	}
	
	@After
	public void after() {
		couch.drop();
	}
	
	@Test
	public void testCreateDesignDocumentWithView() {		
		DesignDocument core = new DesignDocument("core");		
		core.addViewMap("sources", ResourceUtils.asString("/couch/views/simple/map.js"));
		
		couch.put(core);		
		JsonObject json = couch.get("_design/core").json();
		
		Assert.assertNotNull("sources", json.get("views").getAsJsonObject().get("sources"));
	}
}
