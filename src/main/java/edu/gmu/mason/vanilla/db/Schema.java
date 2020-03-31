package edu.gmu.mason.vanilla.db;

import java.sql.Array;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import com.vividsolutions.jts.geom.Geometry;

/**
 * General description_________________________________________________________
 * DB schema class
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class Schema {
	public int index;
	public String name;
	public transient Class<?> type;
	private String rawType;
	private String typeName;

	public void setType(String rawType) {
		setType(rawType, null);
	}

	public void setType(String rawType, String typeName) {
		this.rawType = rawType;
		this.typeName = typeName;
		if (rawType.equalsIgnoreCase("integer")) {
			type = Integer.class;
		} else if (rawType.equalsIgnoreCase("boolean")) {
			type = Boolean.class;
		} else if (rawType.equalsIgnoreCase("character")) {
			type = String.class;
		} else if (rawType.equalsIgnoreCase("double precision")) {
			type = Double.class;
		} else if (rawType.equalsIgnoreCase("array")) {
			type = Array.class;
		} else if (rawType.equalsIgnoreCase("timestamp with time zone")) {
			type = DateTime.class;
		} else if (rawType.equalsIgnoreCase("timestamp without time zone")) {
			type = LocalDateTime.class;
		} else if (rawType.equalsIgnoreCase("time without time zone")) {
			type = LocalTime.class;
		} else if (rawType.equalsIgnoreCase("date")) {
			type = LocalDate.class;
		} else if (rawType.equalsIgnoreCase("user-defined")) {
			if (typeName.equalsIgnoreCase("geometry"))
				type = Geometry.class;
		} else
			throw new RuntimeException("Not supported type " + rawType
					+ " type");
	}

	public void update() {
		setType(rawType, typeName);
	}
}
