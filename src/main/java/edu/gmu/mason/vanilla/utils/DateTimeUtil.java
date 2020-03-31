package edu.gmu.mason.vanilla.utils;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;

import edu.gmu.mason.vanilla.log.ExtLogger;

/**
 * General description_________________________________________________________
 * A class to handle common time functions and keep track of execution time for
 * events.
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu), Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public final class DateTimeUtil implements java.io.Serializable {
	private static final long serialVersionUID = 4949366831332413542L;
	private final static ExtLogger logger = ExtLogger
			.create(DateTimeUtil.class);
	private Map<SimulationEvent, DateTime> timeLog;

	public DateTimeUtil() {
		timeLog = new HashMap<>();
	}

	public DateTime getEventTime(SimulationEvent event) {
		return timeLog.get(event);
	}

	public void addEventTime(SimulationEvent event, DateTime date) {
		timeLog.put(event, date);
	}

	public void logTimeSpent(SimulationEvent startEvent,
			SimulationEvent endEvent, String timeType) {

		DateTimeUtil.logTimeSpent(timeLog.get(startEvent),
				timeLog.get(endEvent), timeType);
	}

	public static boolean isBetween(LocalTime time, LocalTime from, LocalTime to) {

		if (from.isAfter(to) == true) { // this means 'from' is before midnight
										// and 'to' is in the morning
			return time.isEqual(from) || time.isAfter(from) || time.isEqual(to)
					|| time.isBefore(to);
		} else { // this means 'from' and 'to' is sequenced normally
			return (time.isEqual(from) || time.isAfter(from))
					&& (time.isEqual(to) || time.isBefore(to));
		}
	}

	public static void logTimeSpent(DateTime start, DateTime end,
			String timeType) {
		long diff = end.getMillis() - start.getMillis();
		logger.info(timeType + ": " + diff + " ms.");
	}

	public static int calculateMinuteDifference(LocalDateTime currentTime,
			LocalTime targetTime) {
		LocalTime currentLocalTime = currentTime.toLocalTime();
		int diff = Minutes.minutesBetween(currentLocalTime, targetTime)
				.getMinutes();

		if (diff < 0) {
			diff += 60 * 24;
		}
		return diff;
	}

	public static boolean isSameDay(LocalDateTime date1, LocalDateTime date2) {
		return date1.getYear() == date2.getYear()
				&& date1.getMonthOfYear() == date2.getMonthOfYear()
				&& date1.getDayOfMonth() == date2.getDayOfMonth();
	}

	public static boolean isSameMonth(LocalDateTime date1, LocalDateTime date2) {
		return date1.getYear() == date2.getYear()
				&& date1.getMonthOfYear() == date2.getMonthOfYear();
	}

	public static String getDateString(LocalDateTime dt) {
		return dt.toString(org.joda.time.format.DateTimeFormat
				.forPattern("MM-dd-yyyy"));
	}
}
