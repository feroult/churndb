package churndb.model;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.FileReader;

import org.junit.Test;

import churndb.utils.TestConstants;
import churndb.utils.TestResourceUtils;

public class SourceTest {
			
	@Test
	public void testCCN() throws FileNotFoundException {
		Source source = loadJavaSourceAndMetrics();
		
		assertEquals((Integer)25, source.getMetric(Metrics.CCN));
		assertEquals((Integer)61, source.getMetric(Metrics.LOC));		
	}

	private Source loadJavaSourceAndMetrics() throws FileNotFoundException {
		Source source = new Source(TestConstants.PROJECT_CODE, "Product.java");				
		new Metrics().apply(source, new FileReader(TestResourceUtils.resourcesPath(TestConstants.PROJECT_COMMIT_0_PATH) + "/" + source.getPath()));
		return source;
	}	
}
