package edu.gmu.mason.vanilla.log;

import java.io.IOException;

import org.joda.time.LocalTime;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * General description_________________________________________________________
 * Local time adapter class
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class LocalTimeTypeAdapter extends TypeAdapter<LocalTime> {

	@Override
	public void write(JsonWriter out, LocalTime value) throws IOException {
		if (value == null) {
			out.nullValue();
			return;
		}
		out.value(value.toString());
	}

	@Override
	public LocalTime read(JsonReader in) throws IOException {
		String str = in.nextString();
		if (str == null)
			return null;
		return LocalTime.parse(str);
	}

}
