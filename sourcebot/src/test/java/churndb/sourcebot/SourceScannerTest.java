package churndb.sourcebot;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import churndb.sourcebot.model.Source;
import churndb.sourcebot.model.SourceMetrics;
import churndb.sourcebot.utils.ResourceUtils;
import churndb.sourcebot.utils.TestConstants;

public class SourceScannerTest {

	@Test
	public void testImportJavaFolder() {		
		SourceScanner scanner = new SourceScanner(ResourceUtils.realPath(TestConstants.SIMPLE_PROJECT_PATH));				
		List<Source> sources = scanner.apply(new SourceMetrics());		
		Assert.assertEquals(3, sources.size());
	}
}
