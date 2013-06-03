package churndb.tasks;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import churndb.git.Commit;
import churndb.git.GIT;
import churndb.git.TestRepository;
import churndb.model.Churn;
import churndb.model.Metrics;
import churndb.model.Project;
import churndb.model.Source;
import churndb.utils.ChurnClient;
import churndb.utils.TestConstants;
import churndb.utils.TestResourceUtils;

public class ProjectTaskTest {

	private ChurnClient churn = new ChurnClient(TestConstants.COUCHDB_HOST, TestConstants.CHURNDB);
	
	private ApplicationTask applicationTask;

	@Before
	public void before() {				
		System.setProperty("user.home", TestResourceUtils.realPath(TestConstants.HOME_FOLDER));
		
		deleteProjectFolders();
				
		applicationTask = new ApplicationTask();
		applicationTask.undeploy();
		applicationTask.deploy();
	}

	private void deleteProjectFolders() {
		FileUtils.deleteQuietly(new File(TestResourceUtils.tempPath(TestConstants.PROJECT_PATH)));
		FileUtils.deleteQuietly(new File(TestResourceUtils.tempPath(TestConstants.PROJECT_CLONE_PATH)));
	}

	@After
	public void after() {
		applicationTask.undeploy();
	}

	@Test
	public void testReload() {
		TestRepository git = new TestRepository();		
				
		// commit 0
		ProjectTask task = new ProjectTask(createTestProject());
		String commit0 = git.commit0();
		task.reload();

		Project project = churn.getProject(TestConstants.PROJECT_CODE);
		assertEquals(commit0, project.getLastCommit());		
		
		Source source = churn.getSource(project.getCode(), "Address.java");
		assertSource(source, commit0, 1, 0, 5);		
		
		// commit 1
		task = new ProjectTask(createTestProject());
		String commit1 = git.commit1();
		task.reload();

		project = churn.getProject(TestConstants.PROJECT_CODE);
		assertEquals(commit1, project.getLastCommit());		
		
		source = churn.getSource(project.getCode(), "Address.java");				
		assertSource(source, commit1, 2, 2, 14);		
	}

	private void assertSource(Source source, String commit, int churn, int ccn, int loc) {
		assertEquals(commit, source.getLastCommit());
		assertEquals(churn, source.getChurn());
		assertEquals((Integer)ccn, source.getMetric(Metrics.CCN));
		assertEquals((Integer)loc, source.getMetric(Metrics.LOC));
	}

	/*
	private void assertCommit1(String commit1, CouchResponseView view) {
		Source source = view.get(1).as(Source.class);
		assertEquals(commit1, source.getChurn().getCommit());
		asssertChurnDate(source.getChurn(), 2013, Calendar.MAY, 15, 8, 25);
		assertEquals((Integer)2, source.getMetric(Metrics.CCN));
		assertEquals((Integer)14, source.getMetric(Metrics.LOC));
	}

	private void assertCommit0(String commit0, CouchResponseView view) {
		Source sourceCommit0 = view.get(0).as(Source.class);
		assertEquals(commit0, sourceCommit0.getChurn().getCommit());
		asssertChurnDate(sourceCommit0.getChurn(), 2013, Calendar.MAY, 10, 14, 0);
		assertEquals((Integer)0, sourceCommit0.getMetric(Metrics.CCN));
		assertEquals((Integer)5, sourceCommit0.getMetric(Metrics.LOC));
	}*/

	private Project createTestProject() {
		Project project = new Project();
		project.setCode(TestConstants.PROJECT_CODE);
		project.setRepoUrl("https://github.com/feroult/churndb.git");
		return project;
	}
	
	@Test
	public void testClone() {		
		// given
		new TestRepository().doAllCommits();
		
		// when
		Project project = new Project();		
		project.setCode(TestConstants.PROJECT_CLONE_CODE);
		project.setRepoUrl("file:///" + TestResourceUtils.tempPath(TestConstants.PROJECT_PATH));			
		
		ProjectTask projectTask = new ProjectTask(project);
		projectTask.cloneRepository();
		
		// then
		GIT git = new GIT(TestResourceUtils.tempPath(TestConstants.PROJECT_CLONE_PATH));		
		List<Commit> log = git.log();
		assertEquals(3, log.size());
	}

	@Test
	@Ignore
	public void testCloneRemote() {

		Project project = new Project();		
		project.setCode(TestConstants.PROJECT_CLONE_CODE);		
		project.setRepoUrl("git://github.com/feroult/churndb.git");

		ProjectTask projectTask = new ProjectTask(project);
		
		projectTask.cloneRepository();
		projectTask.reload();
		
		System.out.println("reload churndb!");
	}
	
	private void asssertChurnDate(Churn churn, int year, int month, int dayOfMonth, int hourOfDay, int minute) {
		assertEquals(churn.getYear(), year);
		assertEquals(churn.getMonth(), month);
		assertEquals(churn.getDayOfMonth(), dayOfMonth);
		assertEquals(churn.getHourOfDay(), hourOfDay);
		assertEquals(churn.getMinute(), minute);
	}
}
