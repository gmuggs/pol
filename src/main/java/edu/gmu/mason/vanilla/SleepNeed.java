package edu.gmu.mason.vanilla;

import org.joda.time.LocalTime;

import edu.gmu.mason.vanilla.log.Skip;
import edu.gmu.mason.vanilla.log.State;
import edu.gmu.mason.vanilla.utils.DateTimeUtil;

/**
 * General description_________________________________________________________
 * A class that handles agent's sleep need based on circadian rhythm of humans.
 * Each agent has one {@code SleepNeed} object.
  * 
* @author Hamdi Kavak (hkavak at gmu.edu)
 * 
 */
public class SleepNeed implements Need, java.io.Serializable {
	private static final long serialVersionUID = -241168855859043227L;
	@Skip
	private Person agent;
	
	@Skip
	private LocalTime sleepStartTime;
	@Skip
	private int sleepLengthInMinutes;
	@State
	private SleepStatus status;

	public SleepNeed(Person agent) {
		this.agent = agent;

		// default sleep start time between 22:00 and 23:59
		int hour = 22 + agent.getModel().random.nextInt(2);
		int minute = agent.getModel().random.nextInt(60);
		sleepStartTime = new LocalTime(hour, minute);

		// default sleep length;
		sleepLengthInMinutes = 7 * 60 + agent.getModel().random.nextInt(120); // 7-9
																				// hours
																				// sleep
																				// time

		status = SleepStatus.Awake;
	}

	@Override
	public void update() {
		if (status == SleepStatus.Awake && isTimeToSleep()) {
			status = SleepStatus.PrepareToSleep;
		} else if (status == SleepStatus.Sleeping && isTimeToWakeUp()) {
			wakeUp();
		}
	}

	@Override
	public void satisfy() {
		// if agent ready to sleep
		if (isSatisfied() == false && getStatus() == SleepStatus.PrepareToSleep) {
			
			// if at home, go to sleep
			if (agent.getCurrentMode() == PersonMode.AtHome) {
				sleep();

			} else if (agent.getCurrentMode() != PersonMode.Transport) {
				// if not traveling, send agent to home
				agent.travelToHome(VisitReason.Home_WantToSleep);
			}
		}
	}
	
	@Override
	public boolean isSatisfied() {
		return (isTimeToSleep() == true && status == SleepStatus.Sleeping)
				|| (isTimeToSleep() == false);
	}

	////GT_N#1: Sleep/Wake-up (following two parameters)
	public void wakeUp() {
		status = SleepStatus.Awake;
	}
	public void sleep() {
		status = SleepStatus.Sleeping;
	}

	private boolean isTimeToSleep() {
		LocalTime currentLocalTime = agent.getSimulationTime().toLocalTime();
		return DateTimeUtil
				.isBetween(currentLocalTime, this.getSleepStartTime(), agent
						.getTodaysPlan().getWakeUpTime());
	}

	private boolean isTimeToWakeUp() {
		LocalTime currentLocalTime = agent.getSimulationTime().toLocalTime();
		LocalTime wakeUpTime = agent.getTodaysPlan().getWakeUpTime();

		return DateTimeUtil.isBetween(currentLocalTime, wakeUpTime,
				this.getSleepStartTime());
	}
	
	public void kill() {
		this.agent = null;
		this.sleepStartTime = null;
	}

	// Getters/Setters
	public int getSleepLengthInMinutes() {
		return sleepLengthInMinutes;
	}

	public SleepStatus getStatus() {
		return status;
	}

	public void setSleepStartTime(LocalTime sleepStartTime) {
		this.sleepStartTime = sleepStartTime;
	}

	public LocalTime getSleepStartTime() {
		return sleepStartTime;
	}

}
