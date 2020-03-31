package edu.gmu.mason.vanilla.utils;

import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * General description_________________________________________________________
 * A class used for making the operation on JSon objects easier
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class JsonHelper {
	public static String getSchema(JsonElement element) {
		if (element.isJsonObject()) {
			return recursiveSchema(element.getAsJsonObject(), "");
		}
		return "";
	}

	private static String recursiveSchema(JsonObject jObj, String prefix) {
		StringBuffer builder = new StringBuffer();

		Set<String> set = jObj.keySet();
		for (String key : set) {
			JsonElement element = jObj.get(key);
			if (element.isJsonObject())
				builder.append(recursiveSchema(element.getAsJsonObject(),
						prefix + key + ":"));
			else {
				builder.append(prefix + key);
				builder.append("\t");
			}
		}
		return builder.toString();
	}

	public static String getTabSeparateValue(JsonElement element) {
		if (element.isJsonObject()) {
			return recursiveValue(element.getAsJsonObject());
		}
		return "";
	}

	private static String recursiveValue(JsonObject jObj) {
		StringBuffer builder = new StringBuffer();

		Set<String> set = jObj.keySet();
		for (String key : set) {
			JsonElement element = jObj.get(key);
			if (element.isJsonObject())
				builder.append(recursiveValue(element.getAsJsonObject()));
			else if (element.isJsonArray()) {
				JsonArray array = element.getAsJsonArray();
				builder.append("[");
				for (JsonElement jsonElement : array) {
					builder.append(jsonElement.getAsString());
					builder.append(",");
				}
				if (array.size() > 0)
					builder.deleteCharAt(builder.length() - 1);
				builder.append("]\t");
			} else if (element.isJsonNull()) {
				builder.append(element.getAsJsonNull().toString());
				builder.append("\t");
			} else {
				builder.append(element.getAsString());
				builder.append("\t");
			}
		}
		return builder.toString();
	}
}
