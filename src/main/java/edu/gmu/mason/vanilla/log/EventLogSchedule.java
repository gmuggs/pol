package edu.gmu.mason.vanilla.log;

import java.util.function.Supplier;

import org.apache.logging.log4j.Level;

import edu.gmu.mason.vanilla.log.EventList.UpdateStatus;
import sim.engine.SimState;

/**
 * General description_________________________________________________________
 * Log schedule class
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class EventLogSchedule extends LogSchedule {
	private static final long serialVersionUID = 929650394597477331L;
	private final static ExtLogger logger = ExtLogger.create();

	public EventLogSchedule(long startStep, long period, String level, Supplier<EventList> supplier, OutputFormatter format,
			int priority) {
		super(startStep, period, level, supplier, format, priority);
	}

	public void step(SimState state) {
		EventList eventList = (EventList) supplier.get();
		String output;
		if (eventList != null && eventList.update(state.schedule.getSteps())==UpdateStatus.UPDATED && (output = format.print(eventList)) != null) {
			logger.log(Level.getLevel(level), output);
		}

		if (repeat) {
			steps += period;
		}
	}
}
