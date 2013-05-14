package churndb.git;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import churndb.utils.ResourceUtils;
import churndb.utils.TestConstants;

public class GitBasicTest {

	private static final String PROJECT_PATH = ResourceUtils.tempPath(TestConstants.SIMPLE_PROJECT_PATH);

	@Before
	public void before() {
		ResourceUtils.copyToTemp(TestConstants.SIMPLE_PROJECT_PATH);		
	}	
	
	@Test
	public void testInit() {

		GIT git = new GIT(PROJECT_PATH);
		
		git.init();

		git.add("Product.java");
		git.commit("xpto");

		git.add("Customer.java");
		git.commit("xpto");		
		
		List<Commit> commits = git.log(); 
				
		assertCommit(commits.get(0), Type.ADD, "Customer.java");
		assertCommit(commits.get(1), Type.ADD, "Product.java");
	}


	private void assertCommit(Commit commit, Type type, String path) {
		Change change = commit.getChanges().get(0);
		assertEquals(change.getType(), type);
		assertEquals(change.getPath(), path);		
	}

}
