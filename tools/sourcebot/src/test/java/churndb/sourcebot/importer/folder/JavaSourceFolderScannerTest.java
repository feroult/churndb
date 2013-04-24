package churndb.sourcebot.importer.folder;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import churndb.sourcebot.model.JavaSource;
import churndb.sourcebot.model.JavaSourceMetrics;
import churndb.sourcebot.utils.ResourceUtils;

public class JavaSourceFolderScannerTest {

	@Test
	public void testImportFolder() {		
		JavaSourceFolderScanner scanner = new JavaSourceFolderScanner(ResourceUtils.realPath("/churndb/sourcebot/importer/project/"));				
		List<JavaSource> sources = scanner.apply(new JavaSourceMetrics());		
		Assert.assertEquals(3, sources.size());
	}
}
