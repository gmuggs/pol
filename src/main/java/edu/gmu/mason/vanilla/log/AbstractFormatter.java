package edu.gmu.mason.vanilla.log;

import java.util.function.Supplier;

/**
 * General description_________________________________________________________
 * Abstract data formatter class
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public abstract class AbstractFormatter implements OutputFormatter,
		java.io.Serializable {
	private static final long serialVersionUID = 2266279758742414504L;

	private Supplier<String> prefix;
	private Supplier<String> suffix;

	public String print(Object value) {
		String text = format(value);
		String tmp;
		if (prefix != null && (tmp = prefix.get()) != null)
			text = tmp + text;
		if (suffix != null && (tmp = suffix.get()) != null)
			text += tmp;
		return text;
	}

	protected abstract String format(Object value);

	public Supplier<String> getPrefix() {
		return prefix;
	}

	public AbstractFormatter setPrefix(Supplier<String> prefix) {
		this.prefix = prefix;
		return this;
	}

	public Supplier<String> getSuffix() {
		return suffix;
	}

	public AbstractFormatter setSuffix(Supplier<String> suffix) {
		this.suffix = suffix;
		return this;
	}
}
