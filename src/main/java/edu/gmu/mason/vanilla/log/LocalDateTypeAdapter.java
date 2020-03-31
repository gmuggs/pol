package edu.gmu.mason.vanilla.log;

import java.io.IOException;

import org.joda.time.LocalDate;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * General description_________________________________________________________
 * LocalDateTime adapter class
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class LocalDateTypeAdapter extends TypeAdapter<LocalDate>  {

	@Override
	public void write(JsonWriter out, LocalDate value) throws IOException {
		if (value == null) {
			out.nullValue();
			return;
		}
		out.value(value.toString());
	}

	@Override
	public LocalDate read(JsonReader in) throws IOException {
		String str = in.nextString();
		if (str == null)
			return null;
		return LocalDate.parse(str);
	}

}
