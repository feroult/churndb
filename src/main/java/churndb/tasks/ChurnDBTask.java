package churndb.tasks;

import churndb.utils.ChurnClient;

public abstract class ChurnDBTask {

	protected Setup setup = Setup.homeFolderSetup();
	
	protected ChurnClient churn;

	public ChurnDBTask() {
		this.churn = new ChurnClient(setup.getHost(), setup.getDatabase());
	}
		
}
