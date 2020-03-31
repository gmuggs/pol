package edu.gmu.mason.vanilla.utils;

import java.util.List;
import java.util.PriorityQueue;
import java.util.function.Predicate;

import edu.gmu.mason.vanilla.log.ScheduleComparator;
import sim.engine.SimState;
import sim.engine.Steppable;

/**
 * General description_________________________________________________________
 * A master scheduler class.
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class MasterScheduler<T extends Schedulable> implements Steppable {
	private static final long serialVersionUID = 1773165732136689244L;
	private PriorityQueue<T> scheduler;

	public MasterScheduler() {
		scheduler = new PriorityQueue<T>(new ScheduleComparator());
	}

	public void add(T item) {
		scheduler.add(item);
	}

	public void add(List<T> schedule) {
		scheduler.addAll(schedule);
	}
	
	public boolean remove(Predicate<? super T> filter) {
		return scheduler.removeIf(filter);
	}

	@Override
	public void step(SimState state) {
		long currentStep = state.schedule.getSteps();
		T s = null;
		while ((s = scheduler.peek()) != null) {
			if(s.getSteps() < currentStep) {
				// pass
				scheduler.poll();
			}
			else if (s.getSteps() == currentStep) {
				// execute
				s.step(state);
				s = scheduler.poll();
				if(s.getSteps() > currentStep)
					// repeat
					scheduler.add(s);
			}
			else break;
		}
	}	
}
