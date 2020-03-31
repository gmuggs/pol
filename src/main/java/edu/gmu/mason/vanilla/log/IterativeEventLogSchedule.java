package edu.gmu.mason.vanilla.log;

import java.util.ArrayList;
import java.util.Collection;
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
public class IterativeEventLogSchedule extends LogSchedule {
	private static final long serialVersionUID = -5224531072794829020L;
	private final static ExtLogger logger = ExtLogger.create();

	public IterativeEventLogSchedule(long startStep, long period, String level, Supplier<Collection<EventList>> supplier,
			OutputFormatter format, int priority) {
		super(startStep, period, level, supplier, format, priority);
	}
	
	public void step(SimState state) {
		Collection<EventList> collection = (Collection<EventList>) supplier.get();
		if (collection != null) {
			Collection<EventList> removeList = new ArrayList<EventList>(); 
			for (EventList ele : collection) {
				String output;
				UpdateStatus status = ele.update(state.schedule.getSteps());
				if(status == UpdateStatus.UPDATED) {
					if ((output = format.print(ele)) != null) {
						logger.log(Level.getLevel(level), output);
					}
				}
				else if (status == UpdateStatus.REMOVED) {
					removeList.add(ele);
					logger.info(ele.getId() + ": removed from event log schedule");
				}
			}
			collection.removeAll(removeList);
		}

		if (repeat) {
			steps += period;
		}
	}

}
