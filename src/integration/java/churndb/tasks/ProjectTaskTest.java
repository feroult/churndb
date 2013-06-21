package churndb.tasks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import churndb.git.Commit;
import churndb.git.GIT;
import churndb.git.TestRepository;
import churndb.model.Metrics;
import churndb.model.Project;
import churndb.model.Source;
import churndb.utils.TestConstants;
import churndb.utils.TestResourceUtils;

public class ProjectTaskTest {

	private ChurnClient churn = new ChurnClient(TestConstants.COUCHDB_HOST, TestConstants.CHURNDB);

	private ApplicationTask applicationTask;

	@Before
	public void before() {
		TestResourceUtils.setupTempHomeFolder();

		deleteProjectFolders();

		applicationTask = new ApplicationTask();
		applicationTask.undeploy();
		applicationTask.deploy();
	}

	private void deleteProjectFolders() {
		FileUtils.deleteQuietly(new File(Setup.repository(TestConstants.PROJECT_CODE)));
		FileUtils.deleteQuietly(new File(Setup.repository(TestConstants.PROJECT_TO_CLONE_CODE)));
	}

	@After
	public void after() {
		applicationTask.undeploy();
	}

	@Test
	public void testReloadCommitByCommit() {
		ProjectTask task = new ProjectTask();
		TestRepository git = new TestRepository();

		addProject(TestConstants.PROJECT_CODE, "https://github.com/feroult/churndb.git");

		String commit0 = git.commit0();
		task.reload(TestConstants.PROJECT_CODE);
		assertProject(TestConstants.PROJECT_CODE, commit0);
		assertCommit0(commit0);

		String commit1 = git.commit1();
		task.reload(TestConstants.PROJECT_CODE);
		assertProject(TestConstants.PROJECT_CODE, commit1);
		assertCommit1(commit1);

		String commit2 = git.commit2();
		task.reload(TestConstants.PROJECT_CODE);
		assertProject(TestConstants.PROJECT_CODE, commit2);
		assertCommit2(commit2);

		String commit3 = git.commit3();
		task.reload(TestConstants.PROJECT_CODE);
		assertProject(TestConstants.PROJECT_CODE, commit3);
		assertCommit3(commit3);

		String commit4 = git.commit4();
		task.reload(TestConstants.PROJECT_CODE);
		assertProject(TestConstants.PROJECT_CODE, commit4);
		assertCommit4(commit4);
	}

	@Test
	public void testReloadAfterLastCommit() {
		TestRepository git = new TestRepository();

		addProject(TestConstants.PROJECT_CODE, "https://github.com/feroult/churndb.git");

		String commit0 = git.commit0();
		String commit1 = git.commit1();
		String commit2 = git.commit2();
		String commit3 = git.commit3();
		String commit4 = git.commit4();

		new ProjectTask().reload(TestConstants.PROJECT_CODE);

		assertProject(TestConstants.PROJECT_CODE, commit4);

		assertCommit0(commit0);
		assertCommit1(commit1);
		assertCommit2(commit2);
		assertCommit3(commit3);
		assertCommit4(commit4);
	}

	@Test
	public void testPull() {
		// given		
		TestRepository gitServer = new TestRepository(Setup.repository(TestConstants.PROJECT_TO_CLONE_CODE));
		String commit0 = gitServer.commit0();
		
		addProject(TestConstants.PROJECT_CODE, "file://" + Setup.repository(TestConstants.PROJECT_TO_CLONE_CODE));
		ProjectTask task = new ProjectTask();
		task.cloneRepository(TestConstants.PROJECT_CODE);
		
		// when / then
		String commit1 = gitServer.commit1();
		task.pull(TestConstants.PROJECT_CODE);
		assertCommit0(commit0);			
		assertCommit1(commit1);
		
		String commit2 = gitServer.commit2();
		task.pull(TestConstants.PROJECT_CODE);
		assertCommit2(commit2);
		
		String commit3 = gitServer.commit3();
		task.pull(TestConstants.PROJECT_CODE);
		assertCommit3(commit3);
		
		String commit4 = gitServer.commit4();
		task.pull(TestConstants.PROJECT_CODE);
		assertCommit4(commit4);					
	}
	
	@Test
	public void testSameSourceId() {
		TestRepository git = new TestRepository();

		addProject(TestConstants.PROJECT_CODE, "https://github.com/feroult/churndb.git");

		String commit0 = git.commit0();
		String commit1 = git.commit1();

		new ProjectTask().reload(TestConstants.PROJECT_CODE);

		Source sourceInCommit0 = churn.getSourceInCommit(TestConstants.PROJECT_CODE, commit0, "Address.java");
		Source sourceInCommit1 = churn.getSourceInCommit(TestConstants.PROJECT_CODE, commit1, "Address.java");

		assertNotEquals(sourceInCommit0.get_id(), sourceInCommit1.get_id());

		assertNotNull(sourceInCommit0.getSourceId());
		assertNotNull(sourceInCommit1.getSourceId());
		assertEquals(sourceInCommit0.getSourceId(), sourceInCommit1.getSourceId());
	}

	@Test
	public void testCommitTree() {
		TestRepository git = new TestRepository();

		addProject(TestConstants.PROJECT_CODE, "https://github.com/feroult/churndb.git");

		String commit0 = git.commit0();
		String commit1 = git.commit1();
		String commit2 = git.commit2();
		String commit3 = git.commit3();
		String commit4 = git.commit4();

		new ProjectTask().reload(TestConstants.PROJECT_CODE);

		assertCommitTree(TestConstants.PROJECT_CODE, commit0, "Address.java", "Customer.java", "Product.java",
				"Order.java");
		assertCommitTree(TestConstants.PROJECT_CODE, commit1, "Address.java", "Customer.java", "Product.java",
				"Order.java", "OrderRename.java");
		assertCommitTree(TestConstants.PROJECT_CODE, commit2, "Address.java", "Customer.java", "ProductRename.java",
				"Order.java", "OrderRename.java");
		assertCommitTree(TestConstants.PROJECT_CODE, commit3, "Customer.java", "ProductRename.java", "Order.java",
				"OrderRename.java");
		assertCommitTree(TestConstants.PROJECT_CODE, commit4, "Customer.java", "ProductRename.java",
				"OrderRename.java", "AddressRename.java");

	}

	private void assertCommitTree(String projectCode, String commit, String... paths) {
		List<String> pathsList = Arrays.asList(paths);
		List<Source> sources = churn.getSourcesInCommit(projectCode, commit);

		assertEquals(paths.length, sources.size());
		for (Source source : sources) {
			assertTrue(pathsList.contains(source.getPath()));
		}
	}

	private void assertCommit0(String commit0) {
		assertSource(TestConstants.PROJECT_CODE, "Address.java", commit0, 1, 0, 5);
		assertSource(TestConstants.PROJECT_CODE, "Customer.java", commit0, 1, 0, 5);
		assertSource(TestConstants.PROJECT_CODE, "Product.java", commit0, 1, 25, 61);
		assertSource(TestConstants.PROJECT_CODE, "Order.java", commit0, 1, 4, 25);
	}

	private void assertCommit1(String commit1) {
		assertSource(TestConstants.PROJECT_CODE, "Address.java", commit1, 2, 2, 14);
		assertSource(TestConstants.PROJECT_CODE, "OrderRename.java", commit1, 1, 4, 25);
	}

	private void assertCommit2(String commit2) {
		assertSource(TestConstants.PROJECT_CODE, "ProductRename.java", commit2, 2, 25, 61);
	}

	private void assertCommit3(String commit3) {
		Source source = churn.getActiveSource(TestConstants.PROJECT_CODE, "Address.java");
		assertNull(source);
	}

	private void assertCommit4(String commit4) {

		assertSource(TestConstants.PROJECT_CODE, "AddressRename.java", commit4, 3, 2, 14);
		assertNull(churn.getActiveSource(TestConstants.PROJECT_CODE, "Address.java"));

		assertSource(TestConstants.PROJECT_CODE, "OrderRename.java", commit4, 2, 4, 25);
		assertNull(churn.getActiveSource(TestConstants.PROJECT_CODE, "Order.java"));
	}

	private void assertProject(String projectCode, String lastCommit) {
		Project project = churn.getProject(TestConstants.PROJECT_CODE);
		assertEquals(lastCommit, project.getLastCommit());
	}

	private void assertSource(String projectCode, String path, String commit, int churnCount, int ccn, int loc) {
		Source source = churn.getSourceInCommit(projectCode, commit, path);
		assertEquals(commit, source.getCommit());
		assertEquals(churnCount, source.getChurnCount());
		assertEquals((Integer) ccn, source.getMetric(Metrics.CCN));
		assertEquals((Integer) loc, source.getMetric(Metrics.LOC));
	}

	@Test
	public void testClone() {
		// given
		new TestRepository(Setup.repository(TestConstants.PROJECT_TO_CLONE_CODE)).doAllCommits();

		addProject(TestConstants.PROJECT_CODE, "file://" + Setup.repository(TestConstants.PROJECT_TO_CLONE_CODE));

		// when
		ProjectTask projectTask = new ProjectTask();
		projectTask.cloneRepository(TestConstants.PROJECT_CODE);

		// then
		GIT git = new GIT(Setup.repository(TestConstants.PROJECT_CODE));
		List<Commit> log = git.log();
		assertEquals(5, log.size());
	}

	@Test
	@Ignore
	public void testCloneRemote() {
		// given
		System.setProperty(Setup.HOME, "/home/fernando/churndb_home");

		addProject(TestConstants.PROJECT_TO_CLONE_REMOTE_CODE, "git@github.com:dextra/a4c.git");

		// when
		new ProjectTask().reload(TestConstants.PROJECT_TO_CLONE_REMOTE_CODE);

		// then
		System.out.println("reloaded =)");
	}

	private Project addProject(String projectCode, String repoUrl) {
		Project project = new Project();
		project.setCode(projectCode);
		project.setRepoUrl(repoUrl);
		new ProjectTask().add(project.getCode(), project.getRepoUrl());
		return project;
	}	
}
