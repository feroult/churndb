package churndb.tasks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
		System.setProperty("user.home", TestResourceUtils.realPath(TestConstants.HOME_FOLDER));

		deleteProjectFolders();

		applicationTask = new ApplicationTask();
		applicationTask.undeploy();
		applicationTask.deploy();
	}

	private void deleteProjectFolders() {
		FileUtils.deleteQuietly(new File(TestResourceUtils.tempPath(TestConstants.PROJECT_PATH)));
	}

	@After
	public void after() {
		applicationTask.undeploy();
	}

	@Test
	public void testReloadCommitByCommit() {
		ProjectTask task = new ProjectTask();
		TestRepository git = new TestRepository();

		addProject(createTestProject());

		String commit0 = git.commit0();
		task.reload(TestConstants.PROJECT_CODE);
		assertProject(TestConstants.PROJECT_CODE, commit0);
		assertCommit0(git, commit0);

		String commit1 = git.commit1();
		task.reload(TestConstants.PROJECT_CODE);
		assertProject(TestConstants.PROJECT_CODE, commit1);
		assertCommit1(git, commit1);

		String commit2 = git.commit2();
		task.reload(TestConstants.PROJECT_CODE);
		assertProject(TestConstants.PROJECT_CODE, commit2);
		assertCommit2(git, commit2);

		String commit3 = git.commit3();
		task.reload(TestConstants.PROJECT_CODE);
		assertProject(TestConstants.PROJECT_CODE, commit3);
		assertCommit3(git, commit3);

		String commit4 = git.commit4();
		task.reload(TestConstants.PROJECT_CODE);
		assertProject(TestConstants.PROJECT_CODE, commit4);
		assertCommit4(git, commit4);
	}

	@Test
	public void testReloadAfterLastCommit() {
		TestRepository git = new TestRepository();

		addProject(createTestProject());

		String commit0 = git.commit0();
		String commit1 = git.commit1();
		String commit2 = git.commit2();
		String commit3 = git.commit3();
		String commit4 = git.commit4();

		new ProjectTask().reload(TestConstants.PROJECT_CODE);

		assertProject(TestConstants.PROJECT_CODE, commit4);

		assertCommit0(git, commit0);
		assertCommit1(git, commit1);
		assertCommit2(git, commit2);
		assertCommit3(git, commit3);
		assertCommit4(git, commit4);
	}

	@Test
	public void testSameSourceId() {
		TestRepository git = new TestRepository();

		addProject(createTestProject());

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

	private void assertCommit0(TestRepository git, String commit0) {
		assertSource(TestConstants.PROJECT_CODE, "Address.java", commit0, 1, 0, 5);
		assertSource(TestConstants.PROJECT_CODE, "Customer.java", commit0, 1, 0, 5);
		assertSource(TestConstants.PROJECT_CODE, "Product.java", commit0, 1, 25, 61);
		assertSource(TestConstants.PROJECT_CODE, "Order.java", commit0, 1, 4, 25);
	}

	private void assertCommit1(TestRepository git, String commit1) {
		assertSource(TestConstants.PROJECT_CODE, "Address.java", commit1, 2, 2, 14);
		assertSource(TestConstants.PROJECT_CODE, "OrderRename.java", commit1, 1, 4, 25);
	}

	private void assertCommit2(TestRepository git, String commit2) {
		assertSource(TestConstants.PROJECT_CODE, "ProductRename.java", commit2, 2, 25, 61);
	}

	private void assertCommit3(TestRepository git, String commit3) {
		Source source = churn.getActiveSource(TestConstants.PROJECT_CODE, "Address.java");
		assertNull(source);
	}

	private void assertCommit4(TestRepository git, String commit4) {

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
		FileUtils.deleteQuietly(new File(TestResourceUtils.tempPath(TestConstants.PROJECT_CLONE_PATH)));
		new TestRepository().doAllCommits();

		Project project = new Project();
		project.setCode(TestConstants.PROJECT_CLONE_CODE);
		project.setRepoUrl("file:///" + TestResourceUtils.tempPath(TestConstants.PROJECT_PATH));
		addProject(project);

		// when
		ProjectTask projectTask = new ProjectTask();
		projectTask.cloneRepository(TestConstants.PROJECT_CLONE_CODE);

		// then
		GIT git = new GIT(TestResourceUtils.tempPath(TestConstants.PROJECT_CLONE_PATH));
		List<Commit> log = git.log();
		assertEquals(5, log.size());
	}

	@Test
	@Ignore
	public void testCloneRemote() {
		// given
		System.setProperty("user.home", "/home/fernando");

		Project project = new Project();
		project.setCode(TestConstants.PROJECT_CLONE_REMOTE_CODE);
		// project.setRepoUrl("git@github.com:feroult/churndb.git");
		// project.setRepoUrl("git@github.com:dextra/bicbanco_sgc.git");
		project.setRepoUrl("git@github.com:dextra/a4c.git");
		addProject(project);

		// when
		ProjectTask projectTask = new ProjectTask();
		// projectTask.cloneRepository(TestConstants.PROJECT_CLONE_CODE);
		projectTask.reload(TestConstants.PROJECT_CLONE_REMOTE_CODE);

		// then
		System.out.println("reloaded =)");
	}

	private void addProject(Project project) {
		ProjectTask task = new ProjectTask();
		task.add(project.getCode(), project.getRepoUrl());
	}

	private Project createTestProject() {
		Project project = new Project();
		project.setCode(TestConstants.PROJECT_CODE);
		project.setRepoUrl("https://github.com/feroult/churndb.git");
		return project;
	}
}
