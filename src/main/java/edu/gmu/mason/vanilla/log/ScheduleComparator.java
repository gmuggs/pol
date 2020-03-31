package edu.gmu.mason.vanilla.log;

import java.util.Comparator;

import edu.gmu.mason.vanilla.utils.Schedulable;

/**
 * General description_________________________________________________________
 * Schedule comparator class
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class ScheduleComparator implements Comparator<Schedulable>, java.io.Serializable {
	private static final long serialVersionUID = -2411643572194267031L;

	@Override
	public int compare(Schedulable s1, Schedulable s2) {
		if (s1.getSteps() == s2.getSteps()) {
			return Integer.compare(s1.getPriority(), s2.getPriority());
		}
		return Long.compare(s1.getSteps(), s2.getSteps());
	}
}
