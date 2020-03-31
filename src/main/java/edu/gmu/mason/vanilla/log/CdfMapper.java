package edu.gmu.mason.vanilla.log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import edu.gmu.mason.vanilla.db.Column;
import edu.gmu.mason.vanilla.db.Table;

/**
 * General description_________________________________________________________
 * A class used for CDF mapping
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class CdfMapper implements java.io.Serializable {
	private static final long serialVersionUID = -2540706423704468329L;

	private Table table;
	private Column[] codomainColumns;
	private Map<String, List<Column>> map;
	private Map<Column, Function<Object, Object>> extractor;

	public CdfMapper(Table table) {
		this.table = table;
		codomainColumns = table.getColumns();
		map = new HashMap<String, List<Column>>();
		extractor = new HashMap<Column, Function<Object, Object>>();

		for (int i = 0; i < codomainColumns.length; i++) {
			map.put(codomainColumns[i].name, new ArrayList<Column>());
		}
	}

	public void addMap(String codomain, Column newDomain,
			Function<Object, Object> function) {
		map.get(codomain).add(newDomain);
		extractor.put(newDomain, function);
	}

	public Table getTable() {
		return table;
	}

	public Column[] getCodomainColumns() {
		return codomainColumns;
	}

	public Map<String, List<Column>> getMap() {
		HashMap<String, List<Column>> copy = new HashMap<String, List<Column>>();
		map.forEach((a, b) -> copy.put(a, new ArrayList<Column>(b)));
		return copy;
	}

	public Object getDomainValue(Column domain, Object object) {
		return extractor.get(domain).apply(object);
	}

}
