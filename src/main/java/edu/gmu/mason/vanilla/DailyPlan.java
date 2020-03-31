package edu.gmu.mason.vanilla;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

/**
 * General description_________________________________________________________
 * This class is used to keep daily plans of agents. Certain flags are set
 * throughout the day to keep track of the execution of activities including
 * sleeping, and waking up.
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu)
 * 
 */
public class DailyPlan implements java.io.Serializable {
	private static final long serialVersionUID = -2159971601157705964L;
	private LocalDate day;
	private LocalTime wakeUpTime;
	private LocalTime leaveTimeForWork;
	private LocalTime leaveTimeFromWork;
	private boolean workDay;
	private boolean beenAtWork;
	private boolean cameBackFromWork;

	public DailyPlan() {
		workDay = false;
		beenAtWork = false;
		cameBackFromWork = false;
	}

	public boolean hasBeenAtWork() {
		return beenAtWork;
	}

	public void setBeenAtWork(boolean beenAtWork) {
		this.beenAtWork = beenAtWork;
	}

	public boolean cameBackFromWork() {
		return cameBackFromWork;
	}

	public void setCameBackFromWork(boolean cameBackFromWork) {
		this.cameBackFromWork = cameBackFromWork;
	}

	public LocalTime getWakeUpTime() {
		return wakeUpTime;
	}

	public void setWakeUpTime(LocalTime wakeUpTime) {
		this.wakeUpTime = wakeUpTime;
	}

	public boolean isWorkDay() {
		return workDay;
	}

	public void setWorkDay(boolean workDay) {
		this.workDay = workDay;
	}

	public LocalTime getLeaveTimeForWork() {
		return leaveTimeForWork;
	}

	public void setLeaveTimeForWork(LocalTime leaveTime) {
		this.leaveTimeForWork = leaveTime;
	}

	public LocalTime getLeaveTimeFromWork() {
		return leaveTimeFromWork;
	}

	public void setLeaveTimeFromWork(LocalTime leaveTimeFromWork) {
		this.leaveTimeFromWork = leaveTimeFromWork;
	}

	public LocalDate getDay() {
		return day;
	}

	public void setDay(LocalDate day) {
		this.day = day;
	}

}
