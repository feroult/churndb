package churndb.sourcebot.importer.scanner;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import churndb.sourcebot.importer.scanner.java.JavaProjectScanner;

public class ScannerTest {

	@Test
	@Ignore
	public void testProjectScanner() {		
		ProjectScanner scanner = new JavaProjectScanner();		
		scanner.addFolder("/tmp/project");
		
		scanner.scan();
		
		Assert.assertEquals("/Address.java", scanner.getFiles().get(0).getPath());
		Assert.assertEquals("/Customer.java", scanner.getFiles().get(1).getPath());
		Assert.assertEquals("/Product.java", scanner.getFiles().get(2).getPath());
	}
}
