package churndb.bot;

import churndb.utils.ResourceUtils;
import churndb.utils.TestConstants;

public class FakeSourceBotSetup extends SourceBotSetup {

	@Override
	public String getRoot(String code) {
		return ResourceUtils.tempPath(TestConstants.PROJECT_PATH);
	}

}
