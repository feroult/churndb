package churndb.sourcebot.model;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import churndb.sourcebot.utils.ResourceUtils;


public class JavaSourceTest {

	@Test
	public void testNewSource() {
		JavaSource source = new JavaSource("/Address.java_");
		
		Assert.assertNotNull(source.getId());
		Assert.assertEquals("/Address.java_", source.getPath());
	}
	
	@Test
	public void testCCN() {
		JavaSource source = loadJavaSourceAndMetrics();
		
		Assert.assertEquals("25", source.getMetric(JavaSourceMetrics.CCN));
		Assert.assertEquals("61", source.getMetric(JavaSourceMetrics.LOC));		
	}

	private JavaSource loadJavaSourceAndMetrics() {
		File file = ResourceUtils.asFile("/churndb/sourcebot/importer/project/Product.java_");
			
		JavaSource source = new JavaSource("/Product.java_", file);		
		new JavaSourceMetrics().apply(source);
		return source;
	}
	
	@Test
	public void testJson() {		
		JavaSource source = loadJavaSourceAndMetrics();
		
		System.out.println(source.json());
		
	}
}
