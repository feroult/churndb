package churndb.tasks;

import java.io.FileInputStream;
import java.util.Properties;

public class Setup {
	
	private static final String ROOT = "root";

	private static final String HOST = "host";

	private static final String DATABASE = "database";
	
	private Properties properties;

	public Setup(Properties properties) {
		this.properties = properties;
	}

	public String getRoot(String code) {
		return properties.getProperty(ROOT) + "/" + code;
	}
	
	public String getHost() {
		return properties.getProperty(HOST);
	}

	public String getDatabase() {
		return properties.getProperty(DATABASE);
	}

	public static Setup homeFolderSetup() {		
		try {			
			FileInputStream file = new FileInputStream(System.getProperty("user.home") + "/.churndb");
			
			Properties properties = new Properties();
			properties.load(file);
			
			return new Setup(properties);
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
	}

}
