package edu.gmu.mason.vanilla.log;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;

import sim.util.geo.MasonGeometry;

/**
 * General description_________________________________________________________
 * MasonGeometry type adapter class
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class MasonGeometryTypeAdapter extends TypeAdapter<MasonGeometry> {

	public MasonGeometry read(JsonReader reader) throws IOException {
		if (reader.peek() == JsonToken.NULL) {
			reader.nextNull();
			return null;
		}
		String xy = reader.nextString();
		
		WKTReader wkt = new WKTReader();
		Geometry geo = null;
		try {
			geo = wkt.read(xy);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return new MasonGeometry(geo);
	}

	public void write(JsonWriter writer, MasonGeometry value) throws IOException {
		if (value == null) {
			writer.nullValue();
			return;
		}
		WKTWriter wkt = new WKTWriter();
		String xy = wkt.write(value.geometry);
		writer.value(xy);
	}
}
