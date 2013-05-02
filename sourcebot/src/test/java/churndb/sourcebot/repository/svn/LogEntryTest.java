package churndb.sourcebot.repository.svn;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import churndb.sourcebot.repository.svn.LogEntry;
import churndb.sourcebot.utils.ResourceUtils;

public class LogEntryTest {

	@Test
	public void testParse() {
		List<LogEntry> logs = LogEntry.parse(ResourceUtils.asString("/churndb/sourcebot/importer/svn/simple_log.xml"));
		
		LogEntry firstLog = logs.get(0);
		Assert.assertEquals("1", firstLog.getRevision());
		Assert.assertEquals("/Customer.java_", firstLog.getPaths().get(0).getPath());
		Assert.assertEquals("/Address.java_", firstLog.getPaths().get(1).getPath());		
		
		LogEntry secondLog = logs.get(1);
		Assert.assertEquals("2", secondLog.getRevision());
	}

}
