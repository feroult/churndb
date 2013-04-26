package churndb.sourcebot.model;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import churndb.sourcebot.utils.ResourceUtils;

public class JavaSourceTest {

	@Test
	public void testNewSource() {
		Source source = new Source("/Address.java_");
		
		Assert.assertNotNull(source.getId());
		Assert.assertEquals("/Address.java_", source.getPath());
	}
	
	@Test
	public void testCCN() {
		Source source = loadJavaSourceAndMetrics();
		
		Assert.assertEquals("25", source.getMetric(JavaSourceMetrics.CCN));
		Assert.assertEquals("61", source.getMetric(JavaSourceMetrics.LOC));		
	}

	private Source loadJavaSourceAndMetrics() {
		File file = ResourceUtils.asFile("/churndb/sourcebot/importer/project/Product.java_");
			
		Source source = new Source("/Product.java_", file);		
		new JavaSourceMetrics().apply(source);
		return source;
	}	
}
