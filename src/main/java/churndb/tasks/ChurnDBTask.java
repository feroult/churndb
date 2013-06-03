package churndb.tasks;

import churndb.utils.ChurnClient;

public abstract class ChurnDBTask {
	
	protected ChurnClient churn;

	public ChurnDBTask() {
		this.churn = new ChurnClient(Setup.getHost(), Setup.getDatabase());
	}
		
	public void run() {
		// default task behavior, do nothing or extend
	}
}
