package churndb.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class ResourceUtils {

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
		return realPath("/churndb") + "/../../../tmp" + path;
	}

	public static String resourcesPath(String path) {
		return realPath("/churndb") + "/../../../src/test/resources" + path;
	}

	public static void copyToTemp(String srcUri, String destUri) {
		try {
			FileUtils.copyDirectory(new File(resourcesPath(srcUri)), new File(tempPath(destUri)));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void copyToTemp(String srcUri, String destUri, boolean deleteBeforeCopy) {
		if (deleteBeforeCopy) {
			FileUtils.deleteQuietly(new File(tempPath(destUri)));
		}
		copyToTemp(srcUri, destUri);
	}
}
