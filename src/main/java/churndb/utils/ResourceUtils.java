package churndb.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class ResourceUtils {

	public static String asString(String uri) {
		try {
			return FileUtils.readFileToString(FileUtils.toFile(TestResourceUtils.class.getResource(uri)));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static File asFile(String uri) {
		return FileUtils.toFile(TestResourceUtils.class.getResource(uri));
	}
}
