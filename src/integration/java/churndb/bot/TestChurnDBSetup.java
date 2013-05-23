package churndb.bot;

import churndb.tasks.ChurnDBSetup;
import churndb.utils.ResourceUtils;
import churndb.utils.TestConstants;

public class TestChurnDBSetup extends ChurnDBSetup {

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
