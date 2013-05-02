package churndb.sourcebot.repository.svn;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import churndb.sourcebot.repository.svn.SVN;
import churndb.sourcebot.utils.ResourceUtils;

public class RevisionsTest {

	private static final String BASE_REPO_URL = "http://fake.com/svn/project";

	private SVN svn = new SVN(BASE_REPO_URL) {
		@Override
		protected String exec(String command) {
			return ResourceUtils.asString("/churndb/sourcebot/importer/svn/simple_log.xml");
		}
	};

	@Test
	public void testRevisionsByFile() {
		Map<String, List<String>> revisions = svn.revisionsByFile("{2013-04-01}:HEAD");	
		
		Assert.assertArrayEquals(new String[] {"1", "2"}, revisions.get("/Customer.java_").toArray(new String[] {}));		
		Assert.assertArrayEquals(new String[] {"2", "3"}, revisions.get("/Product.java_").toArray(new String[] {}));
	}
}
