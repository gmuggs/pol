package edu.gmu.mason.vanilla.log;

import java.util.List;
import java.util.Map;

import edu.gmu.mason.vanilla.db.Column;

/**
 * General description_________________________________________________________
 * A class used for CDF value formatting
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class CdfValueFormatter extends AbstractFormatter {
	private static final long serialVersionUID = 8904201239979616202L;

	private CdfMapper mapper;

	public CdfValueFormatter(CdfMapper mapper) {
		this.mapper = mapper;
	}

	@Override
	protected String format(Object value) {
		StringBuilder sb = new StringBuilder();
		Column[] columns = mapper.getCodomainColumns();
		Map<String, List<Column>> map = mapper.getMap();
		Object[] currentValues = new Object[columns.length];

		boolean empty;
		do {
			empty = true;
			for (int i = 0; i < columns.length; i++) {
				List<Column> domain = map.get(columns[i].name);
				if (domain != null && !domain.isEmpty()) {
					currentValues[i] = mapper.getDomainValue(domain.remove(0),
							value);
					empty &= domain.isEmpty();
				}
			}

			for (int i = 0; i < columns.length; i++) {
				if (currentValues[i] != null)
					sb.append(currentValues[i]);
				sb.append("\t");
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append("\n");
		} while (!empty);
		sb.deleteCharAt(sb.length() - 1);

		return sb.toString();
	}

}
