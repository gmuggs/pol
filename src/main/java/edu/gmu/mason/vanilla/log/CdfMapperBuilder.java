package edu.gmu.mason.vanilla.log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import edu.gmu.mason.vanilla.utils.JsonHelper;

/**
 * General description_________________________________________________________
 * A class used for CDF mapper builder
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class CdfMapperBuilder {
	// variable1->variable2->
	public static ReflectionValueExtractor functionBuilder(String map, Object instance) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		String[] tokens = map.split(":");
		List<Field> accessor = new ArrayList<Field>();
		Object currentInstance = instance; 
		for (int i = 0; i < tokens.length; i++) {
			Field field = currentInstance.getClass().getField(tokens[i]);
			field.setAccessible(true);
			currentInstance = field.get(currentInstance);
			accessor.add(field);
		}		
		return new ReflectionValueExtractor(accessor);
	}
	
	public static Map<String, ReflectionValueExtractor> functionBuilder(Gson gson, Object instance) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		 Map<String, ReflectionValueExtractor> mapper = new HashMap<String, ReflectionValueExtractor>();
		JsonElement ele = gson.toJsonTree(instance);
		String json = JsonHelper.getSchema(ele);
		String[] tokens = json.split("\t");
		for (int i = 0; i < tokens.length; i++) {
			mapper.put(tokens[i], functionBuilder(tokens[i], instance));
		}
		
		return mapper;
	}
}