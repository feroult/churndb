package churndb.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import churndb.tasks.Setup;
import churndb.utils.TestConstants;
import churndb.utils.TestResourceUtils;

public class SourceTest {
	
	@Before
	public void before() {	
		TestResourceUtils.setupTempHomeFolder();
		
		TestResourceUtils.copy(TestConstants.PROJECT_COMMIT_0_PATH, Setup.repository(TestConstants.PROJECT_CODE), true);				
	}
		
	@Test
	public void testCCN() {
		Source source = loadJavaSourceAndMetrics();
		
		assertEquals((Integer)25, source.getMetric(Metrics.CCN));
		assertEquals((Integer)61, source.getMetric(Metrics.LOC));		
	}

	private Source loadJavaSourceAndMetrics() {
		Source source = new Source(TestConstants.PROJECT_CODE, "Product.java");				
		new Metrics().apply(source);
		return source;
	}	
}
