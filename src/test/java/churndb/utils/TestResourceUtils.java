package churndb.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import churndb.tasks.Setup;

public class TestResourceUtils {


	public static String realPath(String uri) {
		return TestResourceUtils.class.getResource(uri).getPath();
	}
		

	public static String tempPath(String path) {
		return "/tmp" + path;
	}

	public static String resourcesPath(String path) {
		return realPath("/churndb") + "/../../../src/test/resources" + path;
	}

	public static void copy(String resourceUri, String destPath) {
		try {
			FileUtils.copyDirectory(new File(resourcesPath(resourceUri)), new File(destPath));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void copy(String resourceUri, String destPath, boolean deleteBeforeCopy) {
		if (deleteBeforeCopy) {
			FileUtils.deleteQuietly(new File(destPath));
		}
		copy(resourceUri, destPath);
	}


	public static void setupTempHomeFolder() {				
		System.setProperty(Setup.HOME, TestConstants.HOME_FOLDER);
		try {
			FileUtils.copyFileToDirectory(new File(resourcesPath("/churndb/home/churndb.ini")), new File(TestConstants.HOME_FOLDER));
			Setup.mkdirs();			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
