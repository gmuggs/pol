package edu.gmu.mason.vanilla.db;

import java.lang.reflect.Field;

/**
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */

public class PredefinedTables implements java.io.Serializable {
	private static final long serialVersionUID = -163164788300945435L;

	public CheckinDataset CheckinDataset = new CheckinDataset();

	public class CheckinDataset implements java.io.Serializable {
		public CheckinTable CheckinTable = new CheckinTable();

		public class CheckinTable extends Table {
			public Column UserId = new Column();
			public Column CheckinTime = new Column();
			public Column VenueId = new Column();
			public Column VenueType = new Column();
			public Column X = new Column();
			public Column Y= new Column();
		}

		public CheckoutTable CheckoutTable = new CheckoutTable();

		public class CheckoutTable extends Table {
			public Column UserId = new Column();
			public Column CheckoutTime = new Column();
			public Column VenueId = new Column();
			public Column VenueType = new Column();
			public Column X = new Column();
			public Column Y = new Column();
		}
	}

	public PredefinedTables() {
		try {
			recursiveInit(this);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private void recursiveInit(Object parent) throws IllegalArgumentException,
			IllegalAccessException {
		Field[] fields = parent.getClass().getFields();
		for (int i = 0; i < fields.length; i++) {
			Object child = fields[i].get(parent);
			if (child instanceof Column)
				((Column) child).init(parent);
			else if (child instanceof Table) {
				recursiveInit(child);
				((Table) child).init(parent);
			} else if (child != null && !(child instanceof String)
					&& !(child instanceof Integer))
				recursiveInit(child);
		}
	}
}