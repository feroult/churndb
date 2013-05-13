package churndb.sourcebot.git;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import churndb.sourcebot.git.Change;
import churndb.sourcebot.git.Commit;
import churndb.sourcebot.git.GIT;
import churndb.sourcebot.git.Type;
import churndb.sourcebot.utils.ResourceUtils;

public class GitBasicTest extends Assert {

	private static final String GIT_DIR = ResourceUtils.getSimpleProjectPath();

	@Test
	public void testInit() {

		FileUtils.deleteQuietly(new File(GIT_DIR + ".git"));

		GIT git = new GIT(GIT_DIR);
		
		git.init();

		git.add("Product.java_");
		git.commit("xpto");

		git.add("Customer.java_");
		git.commit("xpto");		
		
		List<Commit> commits = git.log(); 
				
		assertCommit(commits.get(0), Type.ADD, "Customer.java_");
		assertCommit(commits.get(1), Type.ADD, "Product.java_");
	}

	private void assertCommit(Commit commit, Type type, String path) {
		Change change = commit.getChanges().get(0);
		assertEquals(change.getType(), type);
		assertEquals(change.getPath(), path);		
	}

}
