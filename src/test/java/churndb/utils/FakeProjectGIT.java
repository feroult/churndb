package churndb.utils;

import org.apache.commons.io.FileUtils;

import churndb.git.GIT;

public class FakeProjectGIT extends GIT {

	public FakeProjectGIT() {
		super(ResourceUtils.tempPath(TestConstants.PROJECT_PATH));
		FileUtils.deleteQuietly(getPath());
		getPath().mkdirs();
		init();
	}
	
	public void commit0() {
		ResourceUtils.copyToTemp(TestConstants.PROJECT_COMMIT_0_PATH, TestConstants.PROJECT_PATH);
		
		add("Address.java");
		add("Customer.java");
		add("Product.java");		
		commit("commit 0");				
	}

	public void commit1() {
		ResourceUtils.copyToTemp(TestConstants.PROJECT_COMMIT_1_PATH, TestConstants.PROJECT_PATH);
		
		add("Address.java");		
		commit("commit 1");
	}		
}
