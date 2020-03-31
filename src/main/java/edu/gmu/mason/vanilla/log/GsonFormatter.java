package edu.gmu.mason.vanilla.log;

import com.google.gson.Gson;

/**
 * General description_________________________________________________________
 * Gson formatter class
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class GsonFormatter extends AbstractFormatter {
	private static final long serialVersionUID = 5042590669406551171L;
	private transient Gson gson;

	public GsonFormatter(Gson gson) {
		this.setGson(gson);
	}

	@Override
	protected String format(Object value) {
		return getGson().toJson(value);
	}

	public Gson getGson() {
		return gson;
	}

	public void setGson(Gson gson) {
		this.gson = gson;
	}
}