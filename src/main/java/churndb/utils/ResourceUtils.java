package churndb.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import com.sun.org.apache.xml.internal.security.utils.Base64;

public class ResourceUtils {

	public static String asBase64(String uri) {
		try {
			return Base64.encode(FileUtils.readFileToByteArray(asFile(uri)));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String asString(String uri) {
		try {
			return FileUtils.readFileToString(asFile(uri));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static File asFile(String uri) {
		return FileUtils.toFile(getResourceURL(uri));
	}

	private static URL getResourceURL(String uri) {
		return ResourceUtils.class.getResource(uri);
	}
}
