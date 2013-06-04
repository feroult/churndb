package churndb.git;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.Test;


public class GITTest {
	
	@Test
	public void testCommits() {
		TestRepository git = new TestRepository();
		
		git.commit0();				
		git.commit1();
		git.commit2();
		
		List<Commit> commits = git.log(); 
			
		assertCommit(commits.get(0), mockChange(Type.ADD, "Address.java"), mockChange(Type.ADD, "Customer.java"), mockChange(Type.ADD, "Product.java"));	
		assertCommit(commits.get(1), mockChange(Type.MODIFY, "Address.java"));
		assertCommit(commits.get(2), mockChange(Type.RENAME, "ProductRename.java"));
	}
	
	@Test
	public void testRenameAcrossCommits() {
		TestRepository git = new TestRepository();
		
		git.commit0();
		git.commit1();
		git.commit2();
		git.commit3();
		String commit = git.commit4();
		
		String oldPath = git.findRenameAcrossCommits(commit, "AddressRename.java");
		
		assertEquals("Address.java", oldPath);
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
