package churndb.sourcebot.couchdb;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import churndb.sourcebot.utils.ResourceUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class CouchClientViewsTest extends CouchTestBase {

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
		core.addViewMap("sources", ResourceUtils.asString("/couch/views/simple/map.js"));		
		couch.put(core);
	}
	
	@After
	public void after() {
		couch.drop();
	}
	
	@Test
	public void testGetFromView() {						
		Document doc = new Document();
		
		doc.setName("/Product.java_");
		doc.setType("source");		
		
		couch.put("123", doc.json());
		JsonObject jsonView = couch.view("core/sources", "/Product.java_").rows(0);
		
		
		
		JsonObject json = couch.get(jsonView.get("id").getAsString()).json();
		
		Assert.assertEquals("/Product.java_", json.get("name").getAsString());
		Assert.assertEquals("source", json.get("type").getAsString());		
	}
}
