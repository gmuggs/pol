package edu.gmu.mason.vanilla.log;

import java.util.Collection;
import java.util.function.Supplier;

import org.apache.logging.log4j.Level;

import sim.engine.SimState;

/**
 * General description_________________________________________________________
 * Log scheduling class
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class IterativeLogSchedule extends LogSchedule {
	private static final long serialVersionUID = 7142990266125475300L;
	private final static ExtLogger logger = ExtLogger.create();

	public IterativeLogSchedule(long startStep, long period, String level, Supplier<Collection> supplier,
			OutputFormatter format, int priority) {
		super(startStep, period, level, supplier, format, priority);
	}

	public IterativeLogSchedule(long startStep, String level, Supplier<Collection> supplier, OutputFormatter format,
			int priority) {
		super(startStep, level, supplier, format, priority);
	}

	public void step(SimState state) {
		Collection collection = (Collection) supplier.get();
		if (collection != null) {
			for (Object ele : collection) {
				String output = format.print(ele);
				logger.log(Level.getLevel(level), output);
			}
		}

		if (repeat) {
			steps += period;
		}
	}
}
