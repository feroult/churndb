package churndb.utils;

import com.google.gson.Gson;

public class JsonUtils {

	public static String key(String... keys) {
		if (keys.length == 1) {
			return new Gson().toJson(keys[0]);
		}
		return new Gson().toJson(keys);

	}
}
