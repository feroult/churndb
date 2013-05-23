package churndb.tasks;

import churndb.tasks.Setup;
import churndb.utils.TestResourceUtils;
import churndb.utils.TestConstants;

public class TestSetup extends Setup {

	public TestSetup() {
		super(null);
	}
	
	@Override
	public String getRoot(String code) {
		return TestResourceUtils.tempPath(TestConstants.PROJECT_PATH);
	}

	@Override
	public String getHost() {
		return TestConstants.COUCHDB_HOST;
	}

	@Override
	public String getDatabase() {
		return TestConstants.CHURNDB;
	}
	
}
