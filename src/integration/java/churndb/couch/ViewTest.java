package churndb.couch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import churndb.couch.response.CouchResponseView;
import churndb.utils.ResourceUtils;
import churndb.utils.TestConstants;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class ViewTest extends CouchTestBase {

	public class Document {
		private String code;
		
		private String type;
		
		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
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
		putDocument("123", "Product.java", "source");
		
		JsonObject jsonView = couch.view("core/simple", "Product.java").first();
		Document doc = couch.get(jsonView.get("id")).bean(Document.class);
		
		assertEquals("Product.java", doc.getCode());
		assertEquals("source", doc.getType());		
	}
	
	@Test
	public void testViewTotalRows() {
		putDocument("1", "Product.java", "source");
		putDocument("2", "Address.java", "source");
		
		CouchResponseView response = couch.view("core/simple");
		
		assertEquals(2, response.totalRows());
	}
	
	@Test
	public void testViewResponseSize() {
		putDocument("1", "Product.java", "source");
		putDocument("2", "Address.java", "source");
		
		CouchResponseView response = couch.view("core/simple", "Product.java");
		assertEquals(1, response.size());		
	}
	
	
	@Test
	public void testViewGet() {
		putDocument("123", "Product.java", "source");
		
		Document doc = couch.viewGet("core/simple", "Product.java").bean(Document.class);
		
		assertEquals("Product.java", doc.getCode());
		assertEquals("source", doc.getType());
	}
	
	@Test
	public void testViewDelete() {
		putDocument("1", "Product.java", "source");
		putDocument("2", "Address.java", "source");
		
		couch.viewDelete("core/simple");
		
		assertTrue(couch.get("1").objectNotFound());
		assertTrue(couch.get("2").objectNotFound());				
	}
	
	private void putDocument(String id, String name, String type) {
		Document doc = new Document();		
		doc.setCode(name);
		doc.setType(type);				
		couch.put(id, doc.json());
	}	
}

