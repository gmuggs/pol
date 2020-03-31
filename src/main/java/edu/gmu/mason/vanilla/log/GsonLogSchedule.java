package edu.gmu.mason.vanilla.log;

import java.util.function.Supplier;

import com.google.gson.Gson;

/**
 * General description_________________________________________________________
 * Gson log scheduling class
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class GsonLogSchedule extends LogSchedule {
	private static final long serialVersionUID = -9170657255461936408L;

	public GsonLogSchedule(long startStep, long period, String level,
			Supplier supplier, Gson gson, int priority) {
		super(startStep, period, level, supplier, new GsonFormatter(gson),
				priority);
	}

	public GsonLogSchedule(long startStep, String level, Supplier supplier,
			Gson gson, int priority) {
		super(startStep, level, supplier, new GsonFormatter(gson), priority);
	}
}
