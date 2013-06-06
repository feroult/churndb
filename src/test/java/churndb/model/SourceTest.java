package churndb.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import churndb.utils.TestConstants;
import churndb.utils.TestResourceUtils;

public class SourceTest {
	
	@Before
	public void before() {	
		System.setProperty("user.home", TestResourceUtils.realPath(TestConstants.HOME_FOLDER));		
		
		TestResourceUtils.copyToTemp(TestConstants.PROJECT_COMMIT_0_PATH, TestConstants.PROJECT_PATH, true);				
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
