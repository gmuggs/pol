package edu.gmu.mason.vanilla.log;

import edu.gmu.mason.vanilla.db.Column;
import edu.gmu.mason.vanilla.db.Table;


/**
 * General description_________________________________________________________
 * A class used for CDF schema formatting
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class CdfSchemaFormatter extends AbstractFormatter {
	private static final long serialVersionUID = 1852254379797589470L;

	@Override
	protected String format(Object value) {
		Table table = null;
		if (value instanceof Table) {
			table = (Table) value;
		}
		else if(value instanceof CdfMapper) {
			table = ((CdfMapper) value).getTable();
		}
		
		if(table != null) {
			StringBuilder sb = new StringBuilder();
			Column[] columns = table.getColumns();
			for (int i = 0; i < columns.length; i++) {
				sb.append(columns[i].name).append("\t");
			}
			sb.deleteCharAt(sb.length() - 1);
			return sb.toString();
		}
		throw new RuntimeException("The value must be either " + Table.class + " or " + CdfMapper.class + ".");
	}
}
