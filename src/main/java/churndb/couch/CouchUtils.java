package churndb.couch;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;

public class CouchUtils {

	public static String keys(boolean descending, String... keys) {
		/*
		if (keys.length == 1) {
			// TODO not working for some cases
			return "key=" + urlEncode(new Gson().toJson(keys[0]));
		}*/
		
		// FIXME to work with viewDelete a view need to export its key as an array		
		return keysForRange(descending, keys);
	}

	private static String keysForRange(boolean descending, String... keys) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(descending ? "endkey=" : "startkey=");		
		List<Object> keysList = new ArrayList<Object>(Arrays.asList(keys));		
		sb.append(urlEncode(new Gson().toJson(keysList)));
				
		keysList.add(new HashMap<String, String>()); // add an {} element at endkey to match the key range
		sb.append(descending ? "&startkey=" : "&endkey=");
		sb.append(urlEncode(new Gson().toJson(keysList)));
				
		return sb.toString();
	}
	
	private static String urlEncode(String key) {
		try {
			return URLEncoder.encode(key, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
}
