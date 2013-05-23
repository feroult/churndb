package churndb.tasks;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import churndb.couch.CouchClient;
import churndb.couch.response.CouchResponseView;
import churndb.model.Churn;
import churndb.model.Metrics;
import churndb.model.Project;
import churndb.model.Source;
import churndb.tasks.ApplicationTask;
import churndb.tasks.Setup;
import churndb.utils.FakeProjectGIT;
import churndb.utils.ResourceUtils;
import churndb.utils.TestConstants;

public class ProjectTaskTest {

	private CouchClient couch = new CouchClient(TestConstants.COUCHDB_HOST, TestConstants.CHURNDB);

	private Setup testSetup = new TestSetup();
	
	private ApplicationTask applicationTask = new ApplicationTask() {
		@Override
		protected Setup setup() {
			return testSetup;
		}		
	};

	@Before
	public void before() {		
		applicationTask.undeploy(); // if exists
		applicationTask.deploy();

		ResourceUtils.copyToTemp(TestConstants.PROJECT_COMMIT_0_PATH, TestConstants.PROJECT_PATH, true);
	}

	@After
	public void after() {
		applicationTask.undeploy();
	}

	@Test
	public void testReloadProjectFromGIT() {
		FakeProjectGIT git = new FakeProjectGIT();

		String commit0 = git.commit0();
		String commit1 = git.commit1();

		Project project = fakeProject();
				
		ProjectTask task = createProjectTask(project);

		task.reload(git);

		project = couch.viewGetFirst("core/projects", project.getCode()).as(Project.class);
		assertEquals(commit1, project.getHead());		
		
		CouchResponseView view = couch.view("core/sources", project.getCode(), "Address.java");		

		Source sourceCommit0 = view.get(0).as(Source.class);
		assertEquals(commit0, sourceCommit0.getChurn().getCommit());
		asssertChurnDate(sourceCommit0.getChurn(), 2013, Calendar.MAY, 10, 14, 0);
		assertEquals((Integer)0, sourceCommit0.getMetric(Metrics.CCN));
		assertEquals((Integer)5, sourceCommit0.getMetric(Metrics.LOC));		
				
		Source sourceCommit1 = view.get(1).as(Source.class);
		assertEquals(commit1, sourceCommit1.getChurn().getCommit());
		asssertChurnDate(sourceCommit1.getChurn(), 2013, Calendar.MAY, 15, 8, 25);
		assertEquals((Integer)2, sourceCommit1.getMetric(Metrics.CCN));
		assertEquals((Integer)14, sourceCommit1.getMetric(Metrics.LOC));										
	}

	private ProjectTask createProjectTask(Project project) {
		return new ProjectTask(project) {
			@Override
			protected Setup setup() {
				return testSetup;
			}
			
		};
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
