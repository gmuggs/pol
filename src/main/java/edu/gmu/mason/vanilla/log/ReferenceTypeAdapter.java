package edu.gmu.mason.vanilla.log;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * General description_________________________________________________________
 * Reference type adapter class
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class ReferenceTypeAdapter extends TypeAdapter<Object> {

	@Override
	public void write(JsonWriter out, Object value) throws IOException {
		if(value == null) {
			out.nullValue();
			return;
		}
		Referenceable reference = value.getClass().getAnnotation(Referenceable.class);
		try {
			Method f = value.getClass().getMethod(reference.keyMethod(), null);
			Object obj = f.invoke(value, null);
			if(obj instanceof Number)
				out.value((Number)obj);
			else
				out.value(obj.toString());
//		} catch (NoSuchFieldException e) {
//			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	@Override
	public Object read(JsonReader in) throws IOException {
		return null;
	}

}
