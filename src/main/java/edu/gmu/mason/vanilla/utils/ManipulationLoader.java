package edu.gmu.mason.vanilla.utils;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Modifier;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import edu.emory.mathcs.backport.java.util.Arrays;
import edu.gmu.mason.vanilla.AgentGeometry;
import edu.gmu.mason.vanilla.WorldModel;
import edu.gmu.mason.vanilla.environment.Apartment;
import edu.gmu.mason.vanilla.environment.Building;
import edu.gmu.mason.vanilla.environment.Classroom;
import edu.gmu.mason.vanilla.environment.Pub;
import edu.gmu.mason.vanilla.environment.Restaurant;
import edu.gmu.mason.vanilla.environment.Workplace;
import edu.gmu.mason.vanilla.log.ExtLogger;
import edu.gmu.mason.vanilla.log.MasonGeometryTypeAdapter;
import edu.gmu.mason.vanilla.log.ReferenceTypeAdapter;
import edu.gmu.mason.vanilla.log.Skip;
import sim.util.geo.MasonGeometry;

/**
 * General description_________________________________________________________
 * A class used for loading a manipulation file
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class ManipulationLoader {
	private static final ExtLogger logger = ExtLogger.create();

	public static List<Manipulation> loadFromJson(String json) {
		Exclusion exclusion = new Exclusion(Building.class, WorldModel.class);
		exclusion.addSkipField(Skip.class);
		Gson gson = new GsonBuilder()
				.setExclusionStrategies(exclusion)
				.serializeSpecialFloatingPointValues()
				.serializeNulls()
				.excludeFieldsWithModifiers(Modifier.STATIC,
						Modifier.TRANSIENT, Modifier.VOLATILE)
				.registerTypeAdapter(AgentGeometry.class,
						new MasonGeometryTypeAdapter())
				.registerTypeAdapter(MasonGeometry.class,
						new MasonGeometryTypeAdapter())
				.registerTypeAdapter(Apartment.class,
						new ReferenceTypeAdapter())
				.registerTypeAdapter(Classroom.class,
						new ReferenceTypeAdapter())
				.registerTypeAdapter(Pub.class, new ReferenceTypeAdapter())
				.registerTypeAdapter(Restaurant.class,
						new ReferenceTypeAdapter())
				.registerTypeAdapter(Workplace.class,
						new ReferenceTypeAdapter()).create();
		List<Manipulation> events = gson.fromJson(json,
				new TypeToken<List<Manipulation>>() {
				}.getType());
		return events;
	}

	public static List<Manipulation> loadFromJson(Reader reader) {
		Exclusion exclusion = new Exclusion(Building.class, WorldModel.class);
		exclusion.addSkipField(Skip.class);
		Gson gson = new GsonBuilder()
				.setExclusionStrategies(exclusion)
				.serializeSpecialFloatingPointValues()
				.serializeNulls()
				.excludeFieldsWithModifiers(Modifier.STATIC,
						Modifier.TRANSIENT, Modifier.VOLATILE)
				.registerTypeAdapter(AgentGeometry.class,
						new MasonGeometryTypeAdapter())
				.registerTypeAdapter(MasonGeometry.class,
						new MasonGeometryTypeAdapter())
				.registerTypeAdapter(Apartment.class,
						new ReferenceTypeAdapter())
				.registerTypeAdapter(Classroom.class,
						new ReferenceTypeAdapter())
				.registerTypeAdapter(Pub.class, new ReferenceTypeAdapter())
				.registerTypeAdapter(Restaurant.class,
						new ReferenceTypeAdapter())
				.registerTypeAdapter(Workplace.class,
						new ReferenceTypeAdapter()).create();
		List<Manipulation> events = gson.fromJson(reader,
				new TypeToken<List<Manipulation>>() {
				}.getType());
		return events;
	}

	public static String saveToJson(List<Manipulation> events) {
		Exclusion exclusion = new Exclusion(Building.class, WorldModel.class);
		exclusion.addSkipField(Skip.class);
		Gson gson = new GsonBuilder()
				.setExclusionStrategies(exclusion)
				.serializeSpecialFloatingPointValues()
				.serializeNulls()
				.excludeFieldsWithModifiers(Modifier.STATIC,
						Modifier.TRANSIENT, Modifier.VOLATILE)
				.registerTypeAdapter(AgentGeometry.class,
						new MasonGeometryTypeAdapter())
				.registerTypeAdapter(MasonGeometry.class,
						new MasonGeometryTypeAdapter())
				.registerTypeAdapter(Apartment.class,
						new ReferenceTypeAdapter())
				.registerTypeAdapter(Classroom.class,
						new ReferenceTypeAdapter())
				.registerTypeAdapter(Pub.class, new ReferenceTypeAdapter())
				.registerTypeAdapter(Restaurant.class,
						new ReferenceTypeAdapter())
				.registerTypeAdapter(Workplace.class,
						new ReferenceTypeAdapter()).create();
		String json = gson.toJson(events);
		return json;
	}

	public static String saveToJson(Manipulation mani) {
		return saveToJson(Arrays.asList(new Manipulation[] { mani }));
	}

	public static List<Manipulation> loadFromConfig(String configFileName) {
		if (configFileName == null)
			return null;
		List<Manipulation> events = null;
		try {
			File f = new File(configFileName);
			if (!f.exists())
				return null;
			FileReader reader = new FileReader(configFileName);
			events = loadFromJson(reader);
			reader.close();
		} catch (Exception cex) {
			logger.error(
					"Error occured during loading manipulation config file.",
					cex);
		}
		return events;
	}
}
