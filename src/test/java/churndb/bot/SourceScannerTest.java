package churndb.bot;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import churndb.bot.SourceScanner;
import churndb.model.Source;
import churndb.model.SourceMetrics;
import churndb.utils.ResourceUtils;
import churndb.utils.TestConstants;

public class SourceScannerTest {

	@Test
	public void testImportJavaFolder() {		
		SourceScanner scanner = new SourceScanner(ResourceUtils.realPath(TestConstants.SIMPLE_PROJECT_PATH));				
		List<Source> sources = scanner.apply(new SourceMetrics());		
		Assert.assertEquals(3, sources.size());
	}
}
