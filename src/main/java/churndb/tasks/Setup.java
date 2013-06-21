package churndb.tasks;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import churndb.model.Source;

public class Setup {

	public static final String HOME = "churndb.home";

	private static final String REPOS = "repos";

	private static final String LOGS = "logs";

	private static final String HOST = "host";

	private static final String DATABASE = "database";

	private static Setup me;

	private Properties properties;

	public Setup(Properties properties) {
		this.properties = properties;
	}

	public static String home(String code) {
		return getHome() + "/" + code;
	}

	public static String repository(String projectCode) {
		return getHome() + "/" + REPOS + "/" + projectCode;
	}

	public static String getDatabase() {
		return getProperty(DATABASE);
	}

	public static String getHost() {
		return getProperty(HOST);
	}

	private static String getProperty(String key) {
		return getMe().properties.getProperty(key);
	}

	private static Setup getMe() {
		if(me == null) {
			 me = homeFolderSetup();
			 mkdirs();
		}
		return me;
	}

	public static File getSourceFile(Source source) {
		return new File(repository(source.getProjectCode() + "/" + source.getPath()));
	}

	public static Setup homeFolderSetup() {
		if (getHome() == null) {
			System.err.println("missing environment variable " + HOME);
			throw new RuntimeException("missing environment variable " + HOME);
		}

		try {
			FileInputStream file = new FileInputStream(home("churndb.ini"));

			Properties properties = new Properties();
			properties.load(file);

			return new Setup(properties);

		} catch (Exception e) {
			System.err.println("can't load " + home("churndb.ini"));
			throw new RuntimeException(e);
		}
	}

	private static String getHome() {
		return System.getProperty(HOME);
	}

	public static void mkdirs() {
		mkdirsIfNotExists(REPOS);
		mkdirsIfNotExists(LOGS);
	}

	private static void mkdirsIfNotExists(String dir) {
		File file = new File(home(dir));
		if (!file.exists()) {
			file.mkdirs();
		}
	}
}
