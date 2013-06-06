package churndb.tasks;

import java.io.PrintWriter;
import java.text.MessageFormat;

import churndb.utils.ChurnClient;

public abstract class ChurnDBTask {

	protected ChurnClient churn;

	private PrintWriter pw;

	public ChurnDBTask() {
		this.churn = new ChurnClient(Setup.getHost(), Setup.getDatabase());
	}

	public ChurnDBTask(PrintWriter pw) {
		this();
		this.pw = pw;
	}

	public void run() {
		// default task behavior, do nothing or extend
	}

	protected void println(String pattern, Object... arguments) {
		if (pw == null) {
			return;
		}
		pw.println(MessageFormat.format(pattern, arguments));
		pw.flush();
	}
}
