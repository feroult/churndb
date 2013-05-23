package churndb.tasks;

import churndb.tasks.Setup;
import churndb.utils.ResourceUtils;
import churndb.utils.TestConstants;

public class TestSetup extends Setup {

	@Override
	public String getRoot(String code) {
		return ResourceUtils.tempPath(TestConstants.PROJECT_PATH);
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
