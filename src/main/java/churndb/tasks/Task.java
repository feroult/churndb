package churndb.tasks;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class Task {

	private static Logger logger = LoggerFactory.getLogger(Task.class);

	protected ChurnClient churn;

	protected Help help;

	private long time;

	public Task() {
		this.churn = new ChurnClient(Setup.getHost(), Setup.getDatabase());
		this.help = new Help();
	}

	public void run() {
		// default task behavior, do nothing or extend
	}

	protected final void clockStart() {
		time = System.currentTimeMillis();
	}

	protected final void logSeconds(String... info) {
		long elapsed = System.currentTimeMillis() - time;

		StringBuilder msg = new StringBuilder();

		if (info.length > 0) {
			msg.append(StringUtils.join(info, " "));
			msg.append(" ");
		}

		msg.append("elapsed time " + (elapsed / 1000) + " seconds");

		logger.info(msg.toString());
	}

}
