package churndb.sourcebot.model;

import org.junit.Assert;
import org.junit.Test;


public class JavaSourceTest {

	@Test
	public void loadTest() {
		JavaSource source = new JavaSource("/Address.java");		
		Assert.assertNotNull(source.getId());
		Assert.assertEquals("/Address.java", source.getPath());
	}
}
