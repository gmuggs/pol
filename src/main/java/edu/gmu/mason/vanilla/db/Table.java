package edu.gmu.mason.vanilla.db;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * General description_________________________________________________________
 * Table class to represent tabular data.
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class Table extends Entity {
	public Column[] getColumns() {
		List<Column> columns = new ArrayList<Column>();
		Field[] fields = getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			try {
				Object child = fields[i].get(this);
				if (child instanceof Column) {
					columns.add((Column) child);
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return columns.toArray(new Column[0]);
	}

	public Table init(Object parent) {
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