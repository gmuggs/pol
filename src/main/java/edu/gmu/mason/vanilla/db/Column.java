package edu.gmu.mason.vanilla.db;

import java.lang.reflect.Field;

/**
 * General description_________________________________________________________
 * This is a class for representing data column.
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class Column extends Entity {
	public Column init(Object parent) {
		Field[] fields = parent.getClass().getFields();
		for (int i = 0; i < fields.length; i++) {
			try {
				Object child = fields[i].get(parent);
				if (child == this) {
					name = fields[i].getName();
					index = i;
					break;
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
			}
		}
		return this;
	}
}
