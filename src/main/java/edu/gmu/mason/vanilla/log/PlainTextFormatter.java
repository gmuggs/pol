package edu.gmu.mason.vanilla.log;
/**
 * General description_________________________________________________________
 * A text formatter
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */

public class PlainTextFormatter extends AbstractFormatter {
	private static final long serialVersionUID = 6834411891155299560L;

	@Override
	protected String format(Object value) {
		if(value == null)
			return null;
		return String.valueOf(value);
	}
}
