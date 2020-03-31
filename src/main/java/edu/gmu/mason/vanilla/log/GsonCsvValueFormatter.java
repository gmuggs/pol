package edu.gmu.mason.vanilla.log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import edu.gmu.mason.vanilla.utils.JsonHelper;

/**
 * General description_________________________________________________________
 * CSV value formatter class
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class GsonCsvValueFormatter extends GsonFormatter {
	private static final long serialVersionUID = -5606533594380171367L;

	public GsonCsvValueFormatter(Gson gson) {
		super(gson);
	}

	protected String format(Object value) {
		JsonElement element = getGson().toJsonTree(value);
		return JsonHelper.getTabSeparateValue(element);
	}
}
