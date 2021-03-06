package churndb.couch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

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

		private int value;
		
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

		public void setValue(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}		
	}

	@Before
	public void before() {
		createSimpleDesignDocument();				
	}

	private void createSimpleDesignDocument() {
		DesignDocument core = new DesignDocument("core");		
		core.addViewMap("simple", ResourceUtils.asString(TestConstants.COUCH_SIMPLE_VIEW_MAP));		
		core.addViewReduce("simple", ResourceUtils.asString(TestConstants.COUCH_SIMPLE_VIEW_REDUCE));		
		core.addList("simple", ResourceUtils.asString(TestConstants.COUCH_SIMPLE_LIST));		
		couch.put(core);
	}
		
	@Test
	public void testGetFirstFromView() {						
		putDocument("simple", "123", "Product.java");
		
		JsonObject jsonView = couch.view("core/simple", "Product.java").first();
		Document doc = couch.get(jsonView.get("id")).as(Document.class);
		
		assertEquals("Product.java", doc.getCode());
		assertEquals("simple", doc.getType());		
	}
	
	@Test
	public void testGetAtFromView() {
		putDocument("simple", "1", "A");
		putDocument("simple", "2", "Z");
		
		CouchResponseView view = couch.view("core/simple");
		
		Document docA = view.get(0).as(Document.class);
		Document docZ = view.get(1).as(Document.class);
		
		assertEquals(docA.getCode(), "A");
		assertEquals(docZ.getCode(), "Z");		
	}
	
	@Test
	public void testViewResponseSize() {
		putDocument("simple", "1", "Product.java");
		putDocument("simple", "2", "Address.java");
		
		CouchResponseView response = couch.view("core/simple", "Product.java");
		assertEquals(1, response.size());		
	}
	
	
	@Test
	public void testViewGetFirst() {
		putDocument("simple", "123", "Product.java");
		
		Document doc = couch.viewGetFirst("core/simple", "Product.java").as(Document.class);
		
		assertEquals("Product.java", doc.getCode());
		assertEquals("simple", doc.getType());
	}
	
	@Test
	public void testViewGetAt() {
		putDocument("simple", "1", "A");
		putDocument("simple", "2", "Z");
				
		Document docA = couch.viewGetAt("core/simple", 0).as(Document.class);
		Document docZ = couch.viewGetAt("core/simple", 1).as(Document.class);
		
		assertEquals(docA.getCode(), "A");
		assertEquals(docZ.getCode(), "Z");
	}
	
	@Test
	public void testViewDeleteCase1() {
		testViewDeleteFor(TestConstants.COUCH_DELETION_VIEW_MAP1);				
	}
	
	@Test
	public void testViewDeleteCase2() {
		testViewDeleteFor(TestConstants.COUCH_DELETION_VIEW_MAP2);				
	}
	
	@Test
	public void testViewDeleteCase3() {
		testViewDeleteFor(TestConstants.COUCH_DELETION_VIEW_MAP3);				
	}
	
	@Test
	public void testViewDeleteCase4() {
		DesignDocument dd = new DesignDocument("deletion");		
		dd.addViewMap("map", ResourceUtils.asString(TestConstants.COUCH_DELETION_VIEW_MAP4));		
		couch.put(dd);
		
		putDocument("deletion", "1", "Product.java");
		putDocument("deletion", "2", "Address.java");
		
		couch.viewDelete("deletion/map", "deletion");
		
		assertTrue(couch.get("1").objectNotFound());
		assertTrue(couch.get("2").objectNotFound());				
	}

	private void testViewDeleteFor(String mapFunction) {
		DesignDocument dd = new DesignDocument("deletion");		
		dd.addViewMap("map", ResourceUtils.asString(mapFunction));		
		couch.put(dd);
		
		putDocument("deletion", "1", "Product.java");
		putDocument("deletion", "2", "Address.java");
		
		couch.viewDelete("deletion/map");
		
		assertTrue(couch.get("1").objectNotFound());
		assertTrue(couch.get("2").objectNotFound());
	}
	
	
	@Test
	public void testReduce() {
		putDocument("simple", "1", "A", 10);
		putDocument("simple", "2", "B", 20);
		putDocument("simple", "3", "C", 30);
		
		Integer total = couch.reduce("core/simple").as(Integer.class);
		
		assertEquals(new Integer(60), total);
	}
	
	@Test
	public void testDescending() {
		putDocument("simple", "1", "A", 10);
		putDocument("simple", "2", "B", 20);
		putDocument("simple", "3", "C", 30);

		Document doc = couch.view("core/simple", ViewOptions.DESCENDING).firstAs(Document.class);
		
		assertEquals("C", doc.getCode());
	}
	
	@Test
	public void testViewValuesAs() {
		putDocument("simple", "1", "A", 10);
		putDocument("simple", "2", "B", 20);
		putDocument("simple", "3", "C", 30);
			
		List<Document> documents = couch.view("core/simple").valuesAs(Document.class);
		
		assertEquals("A", documents.get(0).getCode());
		assertEquals("B", documents.get(1).getCode());
		assertEquals("C", documents.get(2).getCode());		
	}

	@Test
	public void testViewDocsAs() {
		putDocument("simple", "1", "A", 10);
		putDocument("simple", "2", "B", 20);
		putDocument("simple", "3", "C", 30);
			
		List<Document> documents = couch.view("core/simple", ViewOptions.INCLUDE_DOCS).docsAs(Document.class);
		
		assertEquals("A", documents.get(0).getCode());
		assertEquals("B", documents.get(1).getCode());
		assertEquals("C", documents.get(2).getCode());		
	}
	
	@Test
	public void listView() {		
		putDocument("simple", "1", "A", 10);
		putDocument("simple", "2", "B", 20);
		putDocument("simple", "3", "C", 30);
		
		System.out.println();
		//couch.list("core/simple", ViewOptions.INCLUDE_DOCS).docsAs(Document.class);
		
	}
	

	private void putDocument(String type, String id, String code) {
		putDocument(type, id, code, 0);
	}
	
	private void putDocument(String type, String id, String code, int value) {
		Document doc = new Document();		
		doc.setCode(code);
		doc.setType(type);
		doc.setValue(value);
		couch.put(id, doc.json());
	}	
}

