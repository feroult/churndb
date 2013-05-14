package churndb.git;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import churndb.utils.FakeProjectGIT;

public class GitBasicTest {
	
	@Test
	public void testCommits() {

		FakeProjectGIT git = new FakeProjectGIT();
		
		git.init();
		git.commit0();				
		git.commit1();
		
		List<Commit> commits = git.log(); 
			
		assertCommit(commits.get(0), mockChange(Type.MODIFY, "Address.java"));
		assertCommit(commits.get(1), mockChange(Type.ADD, "Address.java"), mockChange(Type.ADD, "Customer.java"), mockChange(Type.ADD, "Product.java"));	
	}

	private Change mockChange(Type type, String path) {
		return new Change(type, null, path);
	}

	private void assertCommit(Commit commit, Change ... expectedChanges) {
		Map<String, Change> changes = commit.getChangesAsMap();
		
		for(Change expectedChange : expectedChanges) {
			Change change = changes.get(expectedChange.getPath());
			assertEquals(expectedChange.getType(), change.getType());
		}		
	}

}
