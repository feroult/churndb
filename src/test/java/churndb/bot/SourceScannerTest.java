package churndb.bot;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import churndb.model.Source;
import churndb.model.SourceMetrics;
import churndb.utils.ResourceUtils;
import churndb.utils.TestConstants;

public class SourceScannerTest {

	private static final String PROJECT_PATH = ResourceUtils.tempPath(TestConstants.PROJECT_PATH);
	
	@Before
	public void before() {
		ResourceUtils.copyToTemp(TestConstants.PROJECT_COMMIT_0_PATH, TestConstants.PROJECT_PATH, true);
	}
	
	@Test
	public void testImportJavaFolder() {
		before();
		SourceScanner scanner = new SourceScanner(PROJECT_PATH);				
		List<Source> sources = scanner.apply(new SourceMetrics());		
		assertEquals(3, sources.size());
	}

}
