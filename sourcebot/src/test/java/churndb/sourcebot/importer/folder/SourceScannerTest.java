package churndb.sourcebot.importer.folder;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import churndb.sourcebot.model.Source;
import churndb.sourcebot.model.SourceMetrics;
import churndb.sourcebot.utils.ResourceUtils;

public class SourceScannerTest {

	@Test
	public void testImportJavaFolder() {		
		SourceScanner scanner = new SourceScanner(ResourceUtils.realPath("/churndb/sourcebot/importer/project/"));				
		List<Source> sources = scanner.apply(new SourceMetrics());		
		Assert.assertEquals(3, sources.size());
	}
}
