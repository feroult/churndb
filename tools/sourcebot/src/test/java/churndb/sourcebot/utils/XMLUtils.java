package churndb.sourcebot.utils;

import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class XMLUtils {

	public static String xmlFromResource(String uri) {
		try {
			return FileUtils.readFileToString(FileUtils.toFile(XMLUtils.class.getResource(uri)));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
}
