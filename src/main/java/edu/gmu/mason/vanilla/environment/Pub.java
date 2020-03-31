package edu.gmu.mason.vanilla.environment;

import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;

import edu.gmu.mason.vanilla.ExpenseType;
import edu.gmu.mason.vanilla.Person;
import edu.gmu.mason.vanilla.WorldParameters;
import edu.gmu.mason.vanilla.log.Characteristics;
import edu.gmu.mason.vanilla.log.Referenceable;
import edu.gmu.mason.vanilla.log.Skip;

/**
 * General description_________________________________________________________
 * This is the class used for representing recreational facilities.
 *
 * @author Hamdi Kavak (hkavak at gmu.edu), Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
@Referenceable(keyMethod = "getId", keyType = Long.class)
public class Pub extends BuildingUnit {

	private static final long serialVersionUID = -1105053509837837886L;

	@Characteristics
	private double hourlyCost;
	@Characteristics
	private LocalTime startTime;
	@Characteristics
	private LocalTime endTime;
	@Skip
	private VisitorProfile visitorProfile;

	public Pub(long id, Building building) {
		super(id, building, "Pub");
		WorldParameters params = model.params;
		visitorProfile = new VisitorProfile(params.maxNumberOfVisitorLogs,
				params.minNumberOfVisitorLogsRequiredForPlaceProfile,
				params.numberOfInterestsToConsider);
	}

	public void housekeeping() {
		visitorProfile.updateProfile();
	}

	public VisitorProfile getVisitorProfile() {
		if (visitorProfile.isCalculated()) {
			return visitorProfile;
		}
		return null;
	}

	public void setVisitorProfile(VisitorProfile visitorProfile) {
		this.visitorProfile = visitorProfile;
	}

	@Override
	public void agentLeaves(Person agent) {
		// total time and its associated cost is calculated.
		LocalDateTime entryTime = getAgentArrival(agent.getAgentId());
		int minuteDiff = Minutes.minutesBetween(entryTime,
				agent.getSimulationTime()).getMinutes();
		double differenceInHour = (double) minuteDiff / 60.0;
		double cost = hourlyCost * differenceInHour;

		// agent pays it and got removed from arrival list.
		agent.getFinancialSafetyNeed().withdrawMoney(cost, ExpenseType.Recreation);
		agent.getFinancialSafetyNeed().reduceWeeklyExtraBudget(cost);

		// log the agent while leaving
		visitorProfile.addVisitor(agent);

		// remove from the building as well.
		super.agentLeaves(agent);
	}

	@Override
	public void agentArrives(Person agent, double visitLength) {
		super.agentArrives(agent, visitLength);
		
		if (model.getQuantitiesOfInterest() != null) {
			// counter at the bar
			model.getQuantitiesOfInterest().incrementPubVisitCount();
		}
	}

	public double getHourlyCost() {
		return hourlyCost;
	}

	public void setHourlyCost(double hourlyCost) {
		this.hourlyCost = hourlyCost;
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

}
