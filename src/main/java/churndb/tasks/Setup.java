package churndb.tasks;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import churndb.model.Source;

public class Setup {
	
	private static final String ROOT = "root";

	private static final String HOST = "host";

	private static final String DATABASE = "database";
	
	private static final Setup me = homeFolderSetup();	
	
	private Properties properties;

	public Setup(Properties properties) {
		this.properties = properties;
	}

	public static String getRoot(String code) {
		return me.properties.getProperty(ROOT) + "/" + code;
	}
	
	public static String getHost() {
		return me.properties.getProperty(HOST);
	}

	public static String getDatabase() {
		return me.properties.getProperty(DATABASE);
	}


	public static File getSourceFile(Source source) {
		return new File(getRoot(source.getProjectCode() + "/" + source.getPath()));
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
