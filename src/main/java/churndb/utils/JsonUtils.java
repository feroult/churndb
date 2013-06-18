package churndb.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class JsonUtils {

	private static final String JSON_DATE_FORMAT = "yyyy/MM/dd HH:mm:ss.SSS";

	public static <T> T from(JsonObject json, Class<T> clazz) {
		Gson gson = new GsonBuilder().setDateFormat(JSON_DATE_FORMAT).create();
		return gson.fromJson(json, clazz);
	}

	public static String to(Object o) {
		Gson gson = new GsonBuilder().setDateFormat(JSON_DATE_FORMAT).create();
		return gson.toJson(o);
	}
}
