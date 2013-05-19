package churndb.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import churndb.utils.ResourceUtils;
import churndb.utils.TestConstants;

public class SourceTest {

	private static final String PROJECT_PATH = ResourceUtils.tempPath(TestConstants.PROJECT_PATH);
	
	@Before
	public void before() {	
		ResourceUtils.copyToTemp(TestConstants.PROJECT_COMMIT_0_PATH, TestConstants.PROJECT_PATH, true);				
	}
		
	@Test
	public void testCCN() {
		Source source = loadJavaSourceAndMetrics();
		
		assertEquals((Integer)25, source.getMetric(Metrics.CCN));
		assertEquals((Integer)61, source.getMetric(Metrics.LOC));		
	}

	private Source loadJavaSourceAndMetrics() {
		Source source = new Source(PROJECT_PATH, "Product.java");		
		new Metrics().apply(source);
		return source;
	}	
}
