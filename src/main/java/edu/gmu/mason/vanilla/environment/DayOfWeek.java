package edu.gmu.mason.vanilla.environment;

import org.joda.time.DateTimeConstants;

/**
 * General description_________________________________________________________
 * An enumeration to represent the days of the week
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu)
 * 
 */
public enum DayOfWeek {
	Monday(DateTimeConstants.MONDAY), Tuesday(DateTimeConstants.TUESDAY), Wednesday(
			DateTimeConstants.WEDNESDAY), Thursday(DateTimeConstants.THURSDAY), Friday(
			DateTimeConstants.FRIDAY), Saturday(DateTimeConstants.SATURDAY), Sunday(
			DateTimeConstants.SUNDAY);

	private int index;

	private DayOfWeek(int index) {
		this.index = index;
	}

	public static DayOfWeek valueOf(int index) {
		for (DayOfWeek b : DayOfWeek.values()) {
			if (b.index == index) {
				return b;
			}
		}
		return null;
	}
}
