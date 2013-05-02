package churndb.sourcebot;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import churndb.sourcebot.couchdb.CouchClient;
import churndb.sourcebot.couchdb.DesignDocument;
import churndb.sourcebot.model.Project;
import churndb.sourcebot.model.Source;
import churndb.sourcebot.repository.svn.SVN;
import churndb.sourcebot.utils.ResourceUtils;

public class SourceBotTest  {

	public class SVNMock extends SVN {
		public SVNMock(String base_url) {
			super(base_url);
		}

		@Override
		protected String exec(String command) {
			if(command.contains(" co ")) {
				try {
					FileUtils.copyDirectory(new File(PROJECT_PATH), new File(TMP_PROJECT_PATH));
				} catch (IOException e) {					
					throw new RuntimeException(e);
				}
				
				return "";
			}
			
			if(command.contains("log")) {
				return ResourceUtils.asString("/churndb/sourcebot/importer/svn/simple_log.xml");	
			}			
			
			return "";
		}
	}

	private static final String PROJECT_PATH = ResourceUtils.realPath("/churndb/sourcebot/importer/project/");
	
	private static final String TMP_PROJECT_PATH = ResourceUtils.tempPath("/test_project/");

	private static final String COUCHDB_HOST = "http://127.0.0.1:5984";

	private static final String CHURNDB = "churndbtest";	
	
	private CouchClient couch = new CouchClient(COUCHDB_HOST, CHURNDB);
	
	@Before
	public void before() {
		couch.dropIfExists();		
		couch.create();	
		deployViews();
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
		Project project = createProjectForTest();
		
		SourceBot bot = new SourceBot(project);
		
		SVN svn = new SVNMock(project.getRepoUrl());	
		
		bot.fromTo(svn, couch);			

		Assert.assertTrue(new File(TMP_PROJECT_PATH + "Product.java_").exists());
		Assert.assertTrue(new File(TMP_PROJECT_PATH + "Customer.java_").exists());
		Assert.assertTrue(new File(TMP_PROJECT_PATH + "Address.java_").exists());
		
		Project projectFromCouch = couch.viewGet("core/projects", project.getName()).bean(Project.class);		
		Assert.assertEquals(project.getRepoUrl(), projectFromCouch.getRepoUrl());
		
		Source source = couch.viewGet("core/sources", project.getName(), "/Product.java_").bean(Source.class);
		Assert.assertEquals("/Product.java_", source.getPath());
	}

	private Project createProjectForTest() {
		FileUtils.deleteQuietly(new File(TMP_PROJECT_PATH));
		
		Project project = new Project();
		
		project.setName("fake_project");
		project.setRepoUrl("http://xpto.com/svn");
		project.setRoot(TMP_PROJECT_PATH);
		return project;
	}
}
