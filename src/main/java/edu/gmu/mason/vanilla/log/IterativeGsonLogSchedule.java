package edu.gmu.mason.vanilla.log;

import java.util.Collection;
import java.util.function.Supplier;

import com.google.gson.Gson;

/**
 * General description_________________________________________________________
 * Gson log scheduling class
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class IterativeGsonLogSchedule extends IterativeLogSchedule {
	private static final long serialVersionUID = 43120076614947916L;

	public IterativeGsonLogSchedule(long startStep, long period, String level,
			Supplier<Collection> supplier, Gson gson, int priority) {
		super(startStep, period, level, supplier, new GsonFormatter(gson),
				priority);
	}

	public IterativeGsonLogSchedule(long startStep, String level,
			Supplier<Collection> supplier, Gson gson, int priority) {
		super(startStep, level, supplier, new GsonFormatter(gson), priority);
	}
}
