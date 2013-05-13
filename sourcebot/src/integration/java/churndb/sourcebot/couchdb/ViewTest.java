package churndb.sourcebot.couchdb;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import churndb.couchdb.DesignDocument;
import churndb.utils.ResourceUtils;
import churndb.utils.TestConstants;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class ViewTest extends CouchTestBase {

	public class Document {
		private String name;
		
		private String type;
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String json() {
			return new Gson().toJson(this);
		}
	}

	@Before
	public void before() {
		couch.dropIfExists();		
		couch.create();		
		createSimpleView();				
	}

	private void createSimpleView() {
		DesignDocument core = new DesignDocument("core");		
		core.addViewMap("simple", ResourceUtils.asString(TestConstants.COUCH_SIMPLE_VIEW_MAP));		
		couch.put(core);
	}
	
	@After
	public void after() {
		couch.drop();
	}
	
	@Test
	public void testGetFirstFromView() {						
		putDocument("123", "/Product.java_", "source");
		
		JsonObject jsonView = couch.view("core/simple", "/Product.java_").first();
		JsonObject json = couch.get(jsonView.get("id")).json();
		
		Assert.assertEquals("/Product.java_", json.get("name").getAsString());
		Assert.assertEquals("source", json.get("type").getAsString());		
	}
	
	@Test
	public void testViewGet() {
		putDocument("123", "/Product.java_", "source");
		
		Document doc = couch.viewGet("core/simple", "/Product.java_").bean(Document.class);
		
		Assert.assertEquals("/Product.java_", doc.getName());
		Assert.assertEquals("source", doc.getType());
	}
	
	private void putDocument(String id, String name, String type) {
		Document doc = new Document();		
		doc.setName(name);
		doc.setType(type);				
		couch.put(id, doc.json());
	}	
}

