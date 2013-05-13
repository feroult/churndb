package churndb.sourcebot.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class ResourceUtils {

	private static final String SIMPLE_PROJECT_PATH = "/churndb/sourcebot/importer/project/";

	public static String asString(String uri) {
		try {
			return FileUtils.readFileToString(FileUtils.toFile(ResourceUtils.class.getResource(uri)));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static File asFile(String uri) {
		return FileUtils.toFile(ResourceUtils.class.getResource(uri));
	}

	public static String realPath(String uri) {
		return ResourceUtils.class.getResource(uri).getPath();
	}

	public static String tempPath(String path) {
		return realPath("/churndb") + "/../../../../tmp" + path;
	}

	public static String getSimpleProjectPath() {
		return realPath(SIMPLE_PROJECT_PATH);
	}

	public static String getSimpleProjectRealPath(String sufix) {
		return getSimpleProjectPath() + sufix;
	}

	public static String getSimpleProjectPath(String sufix) {
		return SIMPLE_PROJECT_PATH + sufix;
	}
}
