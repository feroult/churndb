package churndb.model;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import churndb.utils.ResourceUtils;
import churndb.utils.TestConstants;

public class SourceTest {

	@Before
	public void before() {	
		ResourceUtils.copyToTemp(TestConstants.PROJECT_COMMIT_0_PATH, TestConstants.PROJECT_PATH, true);				
	}
	
	@Test
	public void testNewSource() {
		Source source = new Source("Address.java");
		
		assertEquals("Address.java", source.getPath());
	}
	
	@Test
	public void testCCN() {
		Source source = loadJavaSourceAndMetrics();
		
		assertEquals("25", source.getMetric(SourceMetrics.CCN));
		assertEquals("61", source.getMetric(SourceMetrics.LOC));		
	}

	private Source loadJavaSourceAndMetrics() {
		File file = new File(ResourceUtils.tempPath(TestConstants.PROJECT_PATH + "Product.java"));
			
		Source source = new Source("Product.java", file);		
		new SourceMetrics().apply(source);
		return source;
	}	
}
