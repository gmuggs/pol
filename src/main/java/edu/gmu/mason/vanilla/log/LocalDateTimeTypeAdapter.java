package edu.gmu.mason.vanilla.log;

import java.io.IOException;

import org.joda.time.LocalDateTime;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
/**
 * General description_________________________________________________________
 * Used to adapt JSON date and time
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime>  {

	@Override
	public void write(JsonWriter out, LocalDateTime value) throws IOException {
		if (value == null) {
			out.nullValue();
			return;
		}
		out.value(value.toString());
	}

	@Override
	public LocalDateTime read(JsonReader in) throws IOException {
		String str = in.nextString();
		if (str == null)
			return null;
		return LocalDateTime.parse(str);
	}

}
