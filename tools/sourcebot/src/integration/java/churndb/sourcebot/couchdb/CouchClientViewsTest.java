package churndb.sourcebot.couchdb;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class CouchClientViewsTest extends CreateDropCouchTest {

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
	}
	
	@After
	public void after() {
		couch.drop();
	}
	
	@Test
	@Ignore
	public void testGetFromView() {						
		Document doc = new Document();
		
		doc.setName("/Product.java_");
		doc.setType("source");		
		
		couch.put("123", doc.json());
		JsonObject json = couch.view("sources", "/Product.java_").json();
		
		Assert.assertEquals("/Product.java_", json.get("name").getAsString());
		Assert.assertEquals("source", json.get("type").getAsString());		
	}
}
