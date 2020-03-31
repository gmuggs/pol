package edu.gmu.mason.vanilla.environment;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.Hours;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import edu.gmu.mason.vanilla.EducationLevel;
import edu.gmu.mason.vanilla.Person;
import edu.gmu.mason.vanilla.log.Characteristics;
import edu.gmu.mason.vanilla.log.Skip;

/**
 * General description_________________________________________________________
 * This class represents jobs provided in workplaces. Agents can take these jobs
 * if conditions fit.
 *
 * @author Hamdi Kavak (hkavak at gmu.edu)
 * 
 */
public class Job implements java.io.Serializable {
	private static final long serialVersionUID = -2407569376769252144L;
	private long id;
	@Skip
	private Person worker;
	@Characteristics
	private Workplace workplace;
	@Characteristics
	private double hourlyRate;
	@Characteristics
	private LocalTime startTime;
	@Characteristics
	private LocalTime endTime;
	@Characteristics
	private List<DayOfWeek> daysToWork;
	@Characteristics
	private EducationLevel educationRequirement;
	@Characteristics
	private int neighborhoodId;

	public Job(Workplace workplace, long id) {
		this.workplace = workplace;
		this.id = id;
		daysToWork = new ArrayList<DayOfWeek>();
	}

	public boolean isWorkDay(LocalDateTime dt) {
		int dayId = dt.getDayOfWeek();
		return checkWorkDay(DayOfWeek.valueOf(dayId));
	}

	public boolean isTodayWorkDay() {
		return isWorkDay(worker.getSimulationTime());
	}

	public void addWorkDay(DayOfWeek day) {
		daysToWork.add(day);
	}

	public void addWorkDays(List<DayOfWeek> days) {
		for (DayOfWeek day : days) {
			daysToWork.add(day);
		}
	}

	public int numberOfWorkdaysAWeek() {
		return daysToWork.size();
	}

	private boolean checkWorkDay(DayOfWeek day) {
		for (DayOfWeek dow : daysToWork) {
			if (dow == day) {
				return true;
			}
		}
		return false;
	}

	public int getDailyWorkLengthHour() {
		return Hours.hoursBetween(startTime, endTime).getHours();
	}

	public Person getWorker() {
		return worker;
	}

	public void assignWorker(Person worker) {
		this.worker = worker;
	}

	public void releaseWorker() {
		this.worker = null;
	}

	public double getHourlyRate() {
		return hourlyRate;
	}

	public void setHourlyRate(double hourlyRate) {
		this.hourlyRate = hourlyRate;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public LocalTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}

	public EducationLevel getEducationRequirement() {
		return educationRequirement;
	}

	public void setEducationRequirement(EducationLevel educationRequirement) {
		this.educationRequirement = educationRequirement;
	}

	public Workplace getWorkplace() {
		return workplace;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getNeighborhoodId() {
		return neighborhoodId;
	}

	public void setNeighborhoodId(int neighborhoodId) {
		this.neighborhoodId = neighborhoodId;
	}

	public boolean isAvailable() {
		return this.workplace.isUsable() == true;
	}

}
