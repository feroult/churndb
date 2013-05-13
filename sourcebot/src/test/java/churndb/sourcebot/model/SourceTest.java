package churndb.sourcebot.model;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import churndb.sourcebot.utils.ResourceUtils;

public class SourceTest {

	@Test
	public void testNewSource() {
		Source source = new Source("/Address.java_");
		
		Assert.assertEquals("/Address.java_", source.getPath());
	}
	
	@Test
	public void testCCN() {
		Source source = loadJavaSourceAndMetrics();
		
		Assert.assertEquals("25", source.getMetric(SourceMetrics.CCN));
		Assert.assertEquals("61", source.getMetric(SourceMetrics.LOC));		
	}

	private Source loadJavaSourceAndMetrics() {
		File file = ResourceUtils.asFile(ResourceUtils.getSimpleProjectPath("/Product.java_"));
			
		Source source = new Source("/Product.java_", file);		
		new SourceMetrics().apply(source);
		return source;
	}	
}
