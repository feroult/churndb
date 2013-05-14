package churndb.bot;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import churndb.couch.CouchClient;
import churndb.couch.DesignDocument;
import churndb.git.GIT;
import churndb.model.Project;
import churndb.model.Source;
import churndb.utils.ResourceUtils;
import churndb.utils.TestConstants;

public class SourceBotTest {

	private static final String PROJECT_PATH = ResourceUtils.tempPath(TestConstants.PROJECT_PATH);
	
	private static final String COUCHDB_HOST = "http://127.0.0.1:5984";

	private static final String CHURNDB = "churndbtest";	
	
	private CouchClient couch = new CouchClient(COUCHDB_HOST, CHURNDB);
	
	@Before
	public void before() {
		couch.dropIfExists();		
		couch.create();	
		deployViews();
		
		ResourceUtils.copyToTemp(TestConstants.PROJECT_COMMIT_0_PATH, TestConstants.PROJECT_PATH, true);				
	}

	private void deployViews() {
		DesignDocument core = new DesignDocument("core");		
				
		core.addViewMap("projects", ResourceUtils.asString("/couch/core/views/projects/map.js"));		
		core.addViewMap("sources", ResourceUtils.asString("/couch/core/views/sources/map.js"));
		couch.put(core);
	}
	
	@After
	public void after() {
		couch.drop();
	}
		
	@Test
	public void testLoadProjectFirstTime() {			
		
		Project project = fakeProject();		
		
		SourceBot bot = new SourceBot(project);
				
		GIT git = fakeGIT();
		
		bot.fromTo(git, couch);			

		Project projectFromCouch = couch.viewGet("core/projects", project.getName()).bean(Project.class);		
		assertEquals(project.getRepoUrl(), projectFromCouch.getRepoUrl());
		
		Source source = couch.viewGet("core/sources", project.getName(), "/Product.java").bean(Source.class);
		assertEquals("/Product.java", source.getPath());
	}

	private GIT fakeGIT() {
		GIT git = new GIT(PROJECT_PATH);
		
		git.init();

		git.add("Product.java");
		git.add("Customer.java");
		git.add("Address.java");
		
		git.commit("xpto");
				
		return git;
	}

	private Project fakeProject() {
		Project project = new Project();
		
		project.setName("fake_project");
		project.setRepoUrl("https://github.com/feroult/churndb.git");
		project.setRoot(PROJECT_PATH);
		return project;
	}
}
