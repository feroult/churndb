package churndb.tasks;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import churndb.couch.CouchClient;
import churndb.couch.response.CouchResponseView;
import churndb.git.TestRepository;
import churndb.model.Churn;
import churndb.model.Metrics;
import churndb.model.Project;
import churndb.model.Source;
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
	}

	@After
	public void after() {
		applicationTask.undeploy();
	}

	@Test
	public void testReloadProjectFromGIT() {
		TestRepository git = new TestRepository();

		String commit0 = git.commit0();
		String commit1 = git.commit1();

		Project project = reloadProjectFromGIT();

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

	private Project reloadProjectFromGIT() {
		Project project = createTestProject();				
		ProjectTask task = createProjectTask(project);
		task.reload();
		return project;
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

	private Project createTestProject() {
		Project project = new Project();

		project.setCode("fake_project");
		project.setRepoUrl("https://github.com/feroult/churndb.git");
		return project;
	}
}
