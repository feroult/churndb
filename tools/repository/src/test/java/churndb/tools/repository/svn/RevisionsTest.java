package churndb.tools.repository.svn;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import churndb.tools.repository.utils.XMLUtils;

public class RevisionsTest {

	private static final String BASE_REPO_URL = "http://fake.com/svn/project";

	private SVN svn = new SVN(BASE_REPO_URL) {
		@Override
		protected String exec(String command) {
			return XMLUtils.xmlFromResource("/churndb/tools/repository/svn/simple_log.xml");
		}
	};

	@Test
	public void testRevisionsByFile() {
		Map<String, List<String>> revisions = svn.revisionsByFile("{2013-04-01}:HEAD");	
		
		Assert.assertArrayEquals(new String[] {"1", "2"}, revisions.get("/Customer.java").toArray(new String[] {}));		
		Assert.assertArrayEquals(new String[] {"2", "3"}, revisions.get("/Product.java").toArray(new String[] {}));
	}
}
