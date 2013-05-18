package churndb.bot;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import churndb.couch.CouchClient;
import churndb.couch.DesignDocument;
import churndb.couch.response.CouchResponseView;
import churndb.model.Churn;
import churndb.model.Metrics;
import churndb.model.Project;
import churndb.model.Source;
import churndb.utils.FakeProjectGIT;
import churndb.utils.ResourceUtils;
import churndb.utils.TestConstants;

public class SourceBotTest {

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
	public void testReloadProjectFromGIT() {
		FakeProjectGIT git = new FakeProjectGIT();

		String commit0 = git.commit0();
		String commit1 = git.commit1();

		Project project = fakeProject();
		SourceBotSetup setup = new FakeSourceBotSetup();
		
		SourceBot bot = new SourceBot(project, setup);

		bot.reload(git, couch);

		project = couch.viewGetFirst("core/projects", project.getCode()).bean(Project.class);
		assertEquals(commit1, project.getHead());		
		
		CouchResponseView view = couch.view("core/sources", project.getCode(), "Address.java");		

		Source sourceCommit0 = view.get(0).bean(Source.class);
		assertEquals(commit0, sourceCommit0.getChurn().getCommit());
		asssertChurnDate(sourceCommit0.getChurn(), 2013, Calendar.MAY, 10, 14, 0);
		assertEquals("0", sourceCommit0.getMetric(Metrics.CCN));
		assertEquals("5", sourceCommit0.getMetric(Metrics.LOC));		
				
		Source sourceCommit1 = view.get(1).bean(Source.class);
		assertEquals(commit1, sourceCommit1.getChurn().getCommit());
		asssertChurnDate(sourceCommit1.getChurn(), 2013, Calendar.MAY, 15, 8, 25);
		assertEquals("2", sourceCommit1.getMetric(Metrics.CCN));
		assertEquals("14", sourceCommit1.getMetric(Metrics.LOC));										
	}

	private void asssertChurnDate(Churn churn, int year, int month, int dayOfMonth, int hourOfDay, int minute) {
		assertEquals(churn.getYear(), year);
		assertEquals(churn.getMonth(), month);
		assertEquals(churn.getDayOfMonth(), dayOfMonth);
		assertEquals(churn.getHourOfDay(), hourOfDay);
		assertEquals(churn.getMinute(), minute);
	}

	private Project fakeProject() {
		Project project = new Project();

		project.setCode("fake_project");
		project.setRepoUrl("https://github.com/feroult/churndb.git");
		return project;
	}
}
