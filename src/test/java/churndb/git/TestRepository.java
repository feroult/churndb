package churndb.git;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.lib.PersonIdent;

import churndb.git.GIT;
import churndb.utils.TestResourceUtils;
import churndb.utils.TestConstants;

public class TestRepository extends GIT {

	public TestRepository() {
		super(TestResourceUtils.tempPath(TestConstants.PROJECT_PATH));
		FileUtils.deleteQuietly(getPath());
		getPath().mkdirs();
		init();
	}
	
	public String commit0() {
		TestResourceUtils.copyToTemp(TestConstants.PROJECT_COMMIT_0_PATH, TestConstants.PROJECT_PATH);
		
		add("Address.java");
		add("Customer.java");
		add("Product.java");		
		
		Calendar calendar = new GregorianCalendar(2013, Calendar.MAY, 10, 14, 0);		
		return commit("commit 0", calendar.getTime());							
	}

	public String commit1() {
		TestResourceUtils.copyToTemp(TestConstants.PROJECT_COMMIT_1_PATH, TestConstants.PROJECT_PATH);
		
		add("Address.java");		
		
		Calendar calendar = new GregorianCalendar(2013, Calendar.MAY, 15, 8, 25);
		return commit("commit 1", calendar.getTime());
	}
	
	private String commit(String message, Date date) {
		try {
			CommitCommand commit = getGit().commit();
			commit.setMessage(message);
			commit.setAuthor(new PersonIdent("test", "test@xpto.com", date, TimeZone.getTimeZone("GMT-03:00")));
			return commit.call().getName();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
