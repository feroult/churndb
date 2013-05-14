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
		ResourceUtils.copyToTemp(TestConstants.SIMPLE_PROJECT_PATH);				
	}
	
	@Test
	public void testNewSource() {
		Source source = new Source("/Address.java");
		
		assertEquals("/Address.java", source.getPath());
	}
	
	@Test
	public void testCCN() {
		Source source = loadJavaSourceAndMetrics();
		
		assertEquals("25", source.getMetric(SourceMetrics.CCN));
		assertEquals("61", source.getMetric(SourceMetrics.LOC));		
	}

	private Source loadJavaSourceAndMetrics() {
		File file = new File(ResourceUtils.tempPath(TestConstants.SIMPLE_PROJECT_PATH + "/Product.java"));
			
		Source source = new Source("/Product.java", file);		
		new SourceMetrics().apply(source);
		return source;
	}	
}
