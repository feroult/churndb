package churndb.git;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.lib.PersonIdent;

import churndb.utils.TestConstants;
import churndb.utils.TestResourceUtils;

public class TestRepository extends GIT {
	
	private String projectPath;

	public TestRepository(String projectPath) {
		super(TestResourceUtils.tempPath(projectPath));
		this.projectPath = projectPath;
		FileUtils.deleteQuietly(getPath());
		getPath().mkdirs();
		init();
	}
	
	public TestRepository() {
		this(TestConstants.PROJECT_PATH);
	}
	
	public String commit0() {
		TestResourceUtils.copyToTemp(TestConstants.PROJECT_COMMIT_0_PATH, projectPath);
		
		add("Address.java");
		add("Customer.java");
		add("Product.java");
		add("Order.java");
		
		Calendar calendar = new GregorianCalendar(2013, Calendar.MAY, 10, 14, 0);		
		return commit("commit 0", calendar.getTime());							
	}

	public String commit1() {
		TestResourceUtils.copyToTemp(TestConstants.PROJECT_COMMIT_1_PATH, projectPath);
					
		add("Address.java");
		
		// rename but delete only on file system
		FileUtils.deleteQuietly(new File(projectPath + "Order.java"));
		add("OrderRename.java");
		
		Calendar calendar = new GregorianCalendar(2013, Calendar.MAY, 15, 8, 25);
		return commit("commit 1", calendar.getTime());
	}
	
	public String commit2() {
		TestResourceUtils.copyToTemp(TestConstants.PROJECT_COMMIT_2_PATH, projectPath);
		
		rm("Product.java");
		add("ProductRename.java");
		
		Calendar calendar = new GregorianCalendar(2013, Calendar.MAY, 20, 11, 25);
		return commit("commit 2", calendar.getTime());
	}
	

	public String commit3() {				
		rm("Address.java"); // rename but forget to add the new file
		
		Calendar calendar = new GregorianCalendar(2013, Calendar.MAY, 22, 10, 30);
		return commit("commit 3", calendar.getTime());				
	}
		
	public String commit4() {
		TestResourceUtils.copyToTemp(TestConstants.PROJECT_COMMIT_4_PATH, projectPath);
		
		add("AddressRename.java"); // added the new file rename -- rename across commits
		rm("Order.java"); // delete the old file renamed -- rename across commits
		
		Calendar calendar = new GregorianCalendar(2013, Calendar.MAY, 22, 11, 12);
		return commit("commit 4", calendar.getTime());		
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

	public void doAllCommits() {
		commit0();
		commit1();		
		commit2();
		commit3();
		commit4();
	}
	
}
