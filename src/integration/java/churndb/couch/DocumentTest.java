package churndb.couch;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


import com.google.gson.JsonObject;

public class DocumentTest extends CouchTestBase {

	public class Person extends CouchBean {
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	private static final String DOC = "doc";
	
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
	public void testCreateDeleteDocument() {
		couch.put(DOC, "{\"field\": \"blah\"}");
		
		JsonObject doc = couch.get(DOC).json();		
		Assert.assertEquals("blah", doc.get("field").getAsString());
	}
	
	@Test
	public void testCreateWithGeneratedId() {
		String id = couch.id();
		couch.put(id, "{\"teste\": \"x\"}");
		
		JsonObject doc = couch.get(id).json();		
		Assert.assertEquals("x", doc.get("teste").getAsString());		
	}
	
	@Test
	public void testGetBean() {
		Person person = new Person();		
		person.setName("James");
		
		couch.put(DOC, person.json());
		person = couch.get(DOC).as(Person.class);
		
		Assert.assertEquals("James", person.getName());
	}
	
	@Test 
	public void testGetAndChangeBean() {
		Person person = new Person();
		
		person.setName("James");		
		couch.put(DOC, person.json());
		person = couch.get(DOC).as(Person.class);		
		Assert.assertEquals("James", person.getName());
		
		person.setName("Peter");
		couch.put(person);		
		person = couch.get(DOC).as(Person.class);
		Assert.assertEquals("Peter", person.getName());
	}
}
