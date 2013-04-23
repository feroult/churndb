package churndb.sourcebot.importer.svn;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import churndb.sourcebot.importer.svn.LogEntry;
import churndb.sourcebot.utils.XMLUtils;

public class LogEntryTest {

	@Test
	public void testParse() {
		List<LogEntry> logs = LogEntry.parse(XMLUtils.xmlFromResource("/churndb/sourcebot/importer/svn/simple_log.xml"));
		
		LogEntry firstLog = logs.get(0);
		Assert.assertEquals("1", firstLog.getRevision());
		Assert.assertEquals("/Customer.java", firstLog.getPaths().get(0).getPath());
		Assert.assertEquals("/Address.java", firstLog.getPaths().get(1).getPath());		
		
		LogEntry secondLog = logs.get(1);
		Assert.assertEquals("2", secondLog.getRevision());
	}

}
