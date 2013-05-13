package churndb.couch;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import churndb.couch.DesignDocument;
import churndb.utils.ResourceUtils;
import churndb.utils.TestConstants;

import com.google.gson.JsonObject;

public class DesignDocumentTest extends CouchTestBase {

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
		core.addViewMap("sources", ResourceUtils.asString(TestConstants.COUCH_SIMPLE_VIEW_MAP));
		
		couch.put(core);		
		JsonObject json = couch.get("_design/core").json();
		
		Assert.assertNotNull("sources", json.get("views").getAsJsonObject().get("sources"));
	}
}
