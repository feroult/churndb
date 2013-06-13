package churndb.tasks;

import java.io.PrintWriter;
import java.text.MessageFormat;

public class Help {

	private PrintWriter pw;
	
	public Help() {
		this.pw = new PrintWriter(System.out, true);
	}

	protected void println(String pattern, Object... arguments) {
		pw.println(MessageFormat.format(pattern, arguments));
	}

}
