package edu.gmu.mason.vanilla.db;

import java.util.HashMap;
import java.util.Map;

/**
 * General description_________________________________________________________
 * DB schema map
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class SchemaMap {
	private Map<String, Schema> nameMapping = new HashMap<String, Schema>();
	private Map<Integer, Schema> indexMapping = new HashMap<Integer, Schema>();
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Schema getSchemaByIndex(int index) {
		return indexMapping.get(index);
	}

	public int size() {
		return indexMapping.size();
	}

	public Schema getSchemaByName(String name) {
		return nameMapping.get(name);
	}

	public void add(Schema schema) {
		nameMapping.put(schema.name, schema);
		indexMapping.put(schema.index, schema);
	}

	public void updateCascade() {
		nameMapping.values().forEach(schema -> schema.update());
	}

}
