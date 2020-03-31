package edu.gmu.mason.vanilla.log;

import java.io.IOException;

import org.joda.time.DateTime;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * General description_________________________________________________________
 * A class used for datetime type of data handling
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class DateTimeTypeAdapter extends TypeAdapter<DateTime> {

	@Override
	public void write(JsonWriter out, DateTime value) throws IOException {
		// TODO Auto-generated method stub
		if (value == null) {
			out.nullValue();
			return;
		}
		out.value(value.toString());
	}

	@Override
	public DateTime read(JsonReader in) throws IOException {
		String str = in.nextString();
		if(str==null)
			return null;
		return DateTime.parse(str);
	}

}
