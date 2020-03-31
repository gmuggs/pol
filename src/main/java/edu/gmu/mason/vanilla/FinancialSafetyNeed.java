package edu.gmu.mason.vanilla;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import edu.gmu.mason.vanilla.environment.Job;
import edu.gmu.mason.vanilla.environment.SpatialNetwork;
import edu.gmu.mason.vanilla.environment.Travel;
import edu.gmu.mason.vanilla.log.Skip;
import edu.gmu.mason.vanilla.log.State;
import edu.gmu.mason.vanilla.utils.ChangeApartmentInstruction;
import edu.gmu.mason.vanilla.utils.ChangeJobInstruction;
import edu.gmu.mason.vanilla.utils.CollectionUtil;
import edu.gmu.mason.vanilla.utils.DateTimeUtil;

/**
 * General description_________________________________________________________
 * A class to handle agent's financial conditions including calculating
 * projected income, finding a job, payments, deposits etc. Each agent has one
 * {@code FinancialSafetyNeed} object.
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu)
 * 
 */
public class FinancialSafetyNeed implements Need, java.io.Serializable {
	private static final long serialVersionUID = 7737077737722625990L;
	@Skip
	private Person agent;
	@State
	private double availableBalance;
	@State
	private Job job;
	@Skip
	private LocalDateTime workingInThisJobSince;
	@Skip
	private List<Charge> currentCharges;
	
	@State
	private FinancialStatus status = FinancialStatus.Unknown;
	@State
	private double dailyFoodBudget;
	@State
	private double weeklyExtraBudget;

	public FinancialSafetyNeed(Person agent) {
		this.agent = agent;
		currentCharges = new ArrayList<>(); // first time resetting variables.
	}

	/**
	 * Calculates financial stability and budgets
	 */
	@Override
	public void update() {

		WorldParameters params = agent.getModel().params;

		// daily food budget based on agent characteristic
		if (agent.getCharacteristic() == AgentCharacteristic.Croesus) {
			dailyFoodBudget = params.mealCostAtHome * 3;
		} else if (agent.getCharacteristic() == AgentCharacteristic.Balancus) {
			dailyFoodBudget = params.mealCostAtHome * 4;
		} else if (agent.getCharacteristic() == AgentCharacteristic.Sociolus) {
			dailyFoodBudget = params.mealCostAtHome * 5;
		}
		
		weeklyExtraBudget = 0; // resets weekly budget

		if (isEmployed() == true
				&& agent.getShelterNeed().isSatisfied() == true) { // calculate
																	// financial
																	// stability

			
			
			// projected money by the end of the month
			double projectedMoney = availableBalance
					+ projectedMonthlyIncomeForRemainingDays();
			
			int numOfDaysRemaining = agent.getSimulationTime().dayOfMonth()
					.getMaximumValue()
					- agent.getSimulationTime().getDayOfMonth() + 1;

			// most basic and required 3 cost below
			double projectedFoodCost = numOfDaysRemaining * dailyFoodBudget;

			double education = agent.hasFamily() == true
					&& agent.getFamily().haveKids() ? agent.getFamily()
					.getClassroom().getMonthlyCost() : 0;
					
					
			double rent = agent.getShelter().getRentalCostPerPerson();

			// targeted saving amount with respect to rent
			double targetedSaving = params.targetedSavingAmountWithRespectToRent
					* rent;

			// the below is the extra money that can be spent on site visits
			double projectedExtraMoney = projectedMoney - projectedFoodCost
					- education - rent - targetedSaving;

			if (projectedExtraMoney <= 0 || availableBalance <= 0) {
				status = FinancialStatus.Unstable;
			} else {
				status = FinancialStatus.Stable;

				// recreation budget
				double numberOfWeeksRemainingThisMonth = Math.max(
						numOfDaysRemaining / 7.0, 1);
				weeklyExtraBudget = projectedExtraMoney / numberOfWeeksRemainingThisMonth;
				
				// calculate weekly budget based on agent characteristic
				
				// following statements check if the agent is not Sociolus and adjust weekly balance accordingly
				switch (agent.getCharacteristic()) {
				case Croesus:
					// if too greedy but love need is below okay
					if (agent.getLoveNeed().getStatus().getIndex() < LoveNeedStatus.OK.getIndex()) {
						weeklyExtraBudget = weeklyExtraBudget / 2.0;
					} else {
						weeklyExtraBudget = weeklyExtraBudget / 4.0;
					}

					break;
					
				case Balancus:
					weeklyExtraBudget = weeklyExtraBudget / 2.0;
					break;
					
				}
				
			}

			
		} else if (DateTimeUtil.isSameDay(
				agent.getModel().params.initialSimulationTime,
				agent.getSimulationTime())) { // if simulation has just started
			status = FinancialStatus.Unknown;
		} else { // unemployed so the financial status is unstable
			status = FinancialStatus.Unstable;
		}
		
		// delete unnecessary charge objects
		Iterator<Charge> listIter = currentCharges.iterator();
		LocalDateTime today = agent.getSimulationTime().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
		
		while(listIter.hasNext()) {
			if (listIter.next().getDate().isBefore(today) == true) {
				listIter.remove();
			}
		}
	}

	@Override
	public void satisfy() {

		// skip if agent is not alive or transporting
		if (agent.getLifeStatus() != LifeStatus.Alive
				|| agent.getCurrentMode() == PersonMode.Transport) {
			return;
		}

		// financial safety need is checked here.
		// if agent finance is not stable, find a new job or move to a
		// cheaper house.
		// There are two reasons why financial safety would be an issue
		// 1 - unemployment
		// 2 - low paying job
		
		if (isSatisfied() == false) {


			if (isEmployed() == false) { // this is case 1 - unemployment

				// there are three types of job searching strategies, based on
				// agents' characteristic values
				// 1. Sociolus: closest in the neighborhood if has shelter or
				// first available if no shelter (same education-level req'ed)
				// 2. Balancus: highest paying job in the neighborhood (same
				// education-level req'ed)
				// 3. Croesus: highest paying job in the world (regardless of
				// education-level)
				Job job = searchJobBasedOnAgentCharacteristic(false);

				if (job != null) {
					acceptTheJob(job);
					return;
				}

			} else { // this is case 2 - low paying job or expensive apartment
				
				/*
				 *  This means the agent is employed but it is not satisfactory enough to survive.
				 *  There are two possible options to do here:
				 *  1. Find a better paying job
				 *  2. Move to a cheaper house
				 *  
				 *  We flip a coin and choose one of these options 
				 *  but first make sure none of these two are already requested.
				 */
				
				boolean changeJobInstructed = agent.getInstructionQueue().instructionTypeExists(new ChangeJobInstruction(null));
				boolean changeApartmentInstructed = agent.getInstructionQueue().instructionTypeExists(new ChangeApartmentInstruction(false));
				
				
				if (changeJobInstructed == false && changeApartmentInstructed == false) {
					boolean coin = agent.getModel().random.nextBoolean();
					
					if (coin == true) { // option 1.
						
						Job job = searchJobBasedOnAgentCharacteristic(false);
						
						if (job != null) {
							ChangeJobInstruction changeJobInstruction = new ChangeJobInstruction(job);
							agent.getInstructionQueue().add(changeJobInstruction);
						}
					}
					else {  // option 2.
						ChangeApartmentInstruction changeApartmentInstruction = new ChangeApartmentInstruction(true);
						agent.getInstructionQueue().add(changeApartmentInstruction);
					}
				}
				
				
				if (agent.getTodaysPlan().cameBackFromWork() == true) {
					
					Job job = searchJobBasedOnAgentCharacteristic(true);
					if (job != null) {
						// we found a better paying job. Let's quit the current
						// one and accept this one.
						quitCurrentJob();
						acceptTheJob(job);
					}
				}
			}
		}
		
		if (agent.isInInitializationMode() == false) {
			checkWorkSchedule();
		}
	}

	private void checkWorkSchedule() {
		DailyPlan dailyPlanForToday = agent.getTodaysPlan();
		PersonMode currentMode = agent.getCurrentMode();
		// if this is a work day, agent needs to go to work unless already in
		// transport
		if (isEmployed() && dailyPlanForToday.isWorkDay()) { // if this is a
																// work day for
																// agent

			// let's first check if agent needs to go to work
			LocalTime currentLocalTime = agent.getSimulationTime()
					.toLocalTime();
			LocalTime timeToLeaveForWork = dailyPlanForToday
					.getLeaveTimeForWork();
			LocalTime timeToLeaveFromWork = dailyPlanForToday
					.getLeaveTimeFromWork();

			// if the agent is currently NOT at work and it is time to leave for
			// work
			if (currentMode != PersonMode.AtWork
					&& dailyPlanForToday.hasBeenAtWork() == false
					&& DateTimeUtil.isBetween(currentLocalTime,
							timeToLeaveForWork, timeToLeaveFromWork)) {

				// logger.info("Traveling to work." + getAgentId() + "-" +
				// model.getSimulationTime().toString("E - Hm"));
				Travel travel = new Travel(agent.getLocation(), agent
						.getWorkplace().getLocation(), getJob()
						.getDailyWorkLengthHour(), VisitReason.Workplace_Work);
				agent.getMobility().beginToTransport(travel, PersonMode.AtWork,
						agent.getWorkplace(), true);

			} else if (currentMode == PersonMode.AtWork // if the agent is at
														// work and needs to
														// leave for home
					&& (currentLocalTime.isEqual(timeToLeaveFromWork) || currentLocalTime
							.isAfter(timeToLeaveFromWork))) {

				agent.travelToHome(VisitReason.Home_ComingBackFromWork);
				agent.getTodaysPlan().setCameBackFromWork(true);
			}
		}
	}

	/**
	 * A method that finds jobs based on agent's characteristics.
	 * 
	 * @param betterPayingJob
	 *            if this is set to true, agent might choose a better paying job
	 *            even though it requires lower education
	 * @return
	 */
	private Job searchJobBasedOnAgentCharacteristic(boolean betterPayingJob) {
		Job job = null;
		switch (agent.getCharacteristic()) {
		case Sociolus:

			if (agent.getShelterNeed().isSatisfied() == true) {
				job = findJob(JobSearchCriteria.Proximity, false,
						agent.getNeighborhoodId(), betterPayingJob);
			} else {
				job = findJob(JobSearchCriteria.FirstAvailable, false,
						agent.getNeighborhoodId(), betterPayingJob);
			}

			if (job == null) {
				// now look for jobs globally since we couldn't find anything in
				// the neighborhood, we still look for proximity if agent has
				// home
				if (agent.getShelterNeed().isSatisfied() == true) {
					job = findJob(JobSearchCriteria.Proximity, false,
							betterPayingJob);
				} else {
					job = findJob(JobSearchCriteria.FirstAvailable, false,
							betterPayingJob);
				}
			}

			break;

		case Balancus:

			job = findJob(JobSearchCriteria.HighestPaying, false,
					agent.getNeighborhoodId(), betterPayingJob);

			if (job == null) {
				// now look for jobs globally since we couldn't find anything in
				// the neighborhood
				job = findJob(JobSearchCriteria.HighestPaying, false,
						betterPayingJob);
			}

			break;

		case Croesus:
			job = findJob(JobSearchCriteria.HighestPaying, true,
					betterPayingJob);
			break;
		}
		return job;
	}

	@Override
	public boolean isSatisfied() {
		return isEmployed() && status != FinancialStatus.Unstable;
	}

	public void acceptTheJob(Job job) {

		// if the agent has another job, first quit from that job
		if (isEmployed() == true) {
			quitCurrentJob();
		}

		// find other jobs in the same place that are filled by other agents
		List<Job> filledJobsInTheWorklace = job.getWorkplace().getJobs()
				.stream().filter(p -> p.getWorker() != null)
				.collect(Collectors.toList());

		if (agent.getModel().getVisualWorkGraph()
				.getNode(String.valueOf(agent.getAgentId())) == null) {
			agent.getModel().getVisualWorkGraph()
					.addNode(String.valueOf(agent.getAgentId()));
		}

		for (Job jb : filledJobsInTheWorklace) {
			
			agent.getModel()
					.getWorkNetwork()
					.addEdge(agent.getAgentId(), jb.getWorker().getAgentId(),
							agent.getModel().params.initialNetworkEdgeWeight);
			agent.getModel()
					.getWorkNetwork()
					.addEdge(jb.getWorker().getAgentId(), agent.getAgentId(),
							agent.getModel().params.initialNetworkEdgeWeight);

			String me = String.valueOf(agent.getAgentId());
			String other = String.valueOf(jb.getWorker().getAgentId());

			if (agent.getModel().getVisualWorkGraph()
					.getEdge(me + "--" + other) == null) {
				agent.getModel().getVisualWorkGraph()
						.addEdge(me + "--" + other, me, other, true);
			}

			if (agent.getModel().getVisualWorkGraph()
					.getEdge(other + "--" + me) == null) {
				agent.getModel().getVisualWorkGraph()
						.addEdge(other + "--" + me, other, me, true);
			}
		}

		// make job assignments
		job.assignWorker(agent);

		setJob(job);
		// logging
		agent.writeInterventionLog("GetNewJob", agent.currentJobStatus());
	}

	public void quitCurrentJob() {
		// break up all work friendships
		agent.getModel().getWorkNetwork().removeNode(agent.getAgentId());
		agent.getModel().getWorkNetwork().addNode(agent.getAgentId());

		agent.getModel().getVisualWorkGraph()
				.removeNode(agent.getAgentId() + "");
		agent.getModel().getVisualWorkGraph().addNode(agent.getAgentId() + "");

		this.job.releaseWorker();
		setJob(null);
	}

	public double getMaximumAllowedRental() {

		return projectedMonthlyIncome()
				* agent.getModel().params.maximumAllowedRentalSalaryRatio;
	}

	/**
	 * Provides the monthly income based on hourly rate of agent's current job.
	 * 
	 * @return
	 */
	public double projectedMonthlyIncome() {
		LocalDateTime now = agent.getSimulationTime();
		LocalDateTime firstDayOfMonth = now.withDayOfMonth(1);

		return calculateProjectedIncomeFromGivenDate(firstDayOfMonth);
	}

	public double projectedMonthlyIncomeForRemainingDays() {
		LocalDateTime now = agent.getSimulationTime();

		return calculateProjectedIncomeFromGivenDate(now);
	}

	private double calculateProjectedIncomeFromGivenDate(LocalDateTime date) {
		if (job == null) {
			return 0;
		}
		int days = date.dayOfMonth().getMaximumValue();
		int totalDays = 0;

		for (int i = date.getDayOfMonth(); i <= days; i++) {
			if (job.isWorkDay(date.withDayOfMonth(i)) == true) {
				totalDays++;
			}
		}
		return totalDays * job.getDailyWorkLengthHour() * job.getHourlyRate();
	}

	public void depositMoney(double amount) {
		depositMoney(amount, false);

	}

	public void depositMoney(double amount, boolean rentalAdjustment) {
		if (rentalAdjustment == true) { // this means, somebody became a
										// roommate so the agent receives money
										// to share the rest of the rental
			// significantFinancialChangeHappened = true;
			depositMoney(amount, "RentAdjustment");
		}
		else {
			depositMoney(amount, "Wage");
		}
	}
	
	public void depositMoney(double amount, String reason) {
		availableBalance += amount;
		agent.writeIncomeToFile(amount, reason);
	}

	public void withdrawMoney(double amount, ExpenseType type) {

		agent.writeExpenseToFile(amount, type);

		availableBalance -= amount;
		currentCharges.add(new Charge(type, amount, agent.getSimulationTime()));
	}

	public double getRemainingFoodBudgetForToday() {
		double foodExpenseSoFar = getTotalChargesToday(ExpenseType.Food);
		return dailyFoodBudget - foodExpenseSoFar;
	}

	public boolean canAfford(double amount) {
		return availableBalance > amount;
	}
	/*
	public double getTotalChargesSinceBeginningOfThisMonth() {
		LocalDateTime now = agent.getSimulationTime();
		LocalDateTime firstDayOfMonth = now.withDayOfMonth(1);
		return getTotalChargesBetweenDates(firstDayOfMonth, now);
	}

	public double getTotalChargesSinceBeginningOfThisMonth(ExpenseType type) {
		return currentCharges
				.stream()
				.filter(p -> DateTimeUtil.isSameMonth(p.getDate(),
						agent.getSimulationTime())
						&& p.getExpenseType() == type)
				.mapToDouble(Charge::getAmount).sum();
	}
*/
	/**
	 * Returns charges made between dates (inclusive)
	 * 
	 * @param startDate
	 *            start date inclusive
	 * @param endDate
	 *            end date inclusive
	 * @return
	 */
	/*public double getTotalChargesBetweenDates(LocalDateTime startDate,
			LocalDateTime endDate) {
		LocalDateTime firstMomentOfDay = startDate.withMillisOfDay(0);
		LocalDateTime lastMomentOfDay = endDate.withTime(23, 59, 59, 999);
		return currentCharges
				.stream()
				.filter(p -> (p.getDate().isEqual(firstMomentOfDay) || p
						.getDate().isAfter(firstMomentOfDay))
						&& (p.getDate().isEqual(lastMomentOfDay) || p.getDate()
								.isBefore(lastMomentOfDay)))
				.mapToDouble(Charge::getAmount).sum();
	}*/

	/**
	 * Returns charges on given types, made between dates (inclusive)
	 * 
	 * @param startDate
	 * @param endDate
	 * @param type
	 * @return
	 */
	/*public double getTotalChargesBetweenDates(LocalDateTime startDate,
			LocalDateTime endDate, ExpenseType type) {

		LocalDateTime firstMomentOfDay = startDate.withMillisOfDay(0);
		LocalDateTime lastMomentOfDay = endDate.withTime(23, 59, 59, 999);
		return currentCharges
				.stream()
				.filter(p -> (p.getDate().isEqual(firstMomentOfDay) || p
						.getDate().isAfter(firstMomentOfDay))
						&& (p.getDate().isEqual(lastMomentOfDay) || p.getDate()
								.isBefore(lastMomentOfDay))
						&& p.getExpenseType() == type)
				.mapToDouble(Charge::getAmount).sum();

	}*/

	public double getTotalChargesByDay(LocalDateTime dateTime, ExpenseType type) {
		return currentCharges
				.stream()
				.filter(p -> DateTimeUtil.isSameDay(p.getDate(), dateTime)
						&& p.getExpenseType() == type)
				.mapToDouble(Charge::getAmount).sum();
	}

	public double getTotalChargesByDay(LocalDateTime dateTime) {
		return currentCharges.stream()
				.filter(p -> DateTimeUtil.isSameDay(p.getDate(), dateTime))
				.mapToDouble(Charge::getAmount).sum();
	}
	
	public double getTotalChargesToday(ExpenseType type) {
		return getTotalChargesByDay(agent.getSimulationTime(), type);
	}
	/*
	public double getTotalChargesToday() {
		return getTotalChargesByDay(agent.getSimulationTime());
	}*/

	private Job findJob(JobSearchCriteria criteria,
			boolean acceptLowerEducation, boolean betterPayingJobs) {

		return findJob(criteria, acceptLowerEducation, -1, betterPayingJobs);
	}

	private Job findJob(JobSearchCriteria criteria,
			boolean acceptLowerEducation, int neighborhoodId,
			boolean betterPayingJobs) {

		List<Job> allAvailableJobs = null;

		// a stream to get all available jobs
		List<Job> jobs = agent.getModel().getAvailableUnfilledJobs();

		// eliminate some jobs based on agent's education-level requirement
		// strategy
		if (acceptLowerEducation == true) {
			jobs = jobs
					.stream()
					.filter(p -> p.getEducationRequirement().getValue() <= agent
							.getEducationLevel().getValue())
					.collect(Collectors.toList());
		} else {
			jobs = jobs
					.stream()
					.filter(p -> p.getEducationRequirement().getValue() == agent
							.getEducationLevel().getValue())
					.collect(Collectors.toList());
		}

		if (neighborhoodId >= 0) { // this means a neighborhood is specified, so
									// let's filter jobs on that neighborhood
			jobs = jobs.stream()
					.filter(p -> p.getNeighborhoodId() == neighborhoodId)
					.collect(Collectors.toList());
		}

		if (betterPayingJobs == true) { // get the job pays better than now
			jobs = jobs
					.stream()
					.filter(p -> p.getHourlyRate() > agent.getJob()
							.getHourlyRate()).collect(Collectors.toList());
		}

		allAvailableJobs = jobs;
		CollectionUtil.shuffle(allAvailableJobs, agent.getModel().random);

		switch (criteria) {
		case FirstAvailable:
			if (allAvailableJobs.size() > 0) {
				return allAvailableJobs.get(0);
			}
			break;
		case Proximity:
			SpatialNetwork spatialNetwork = agent.getModel()
					.getSpatialNetwork();
			double shortestDistance = 999999999;
			Job closestJob = null;

			for (Job jb : allAvailableJobs) {
				double dist = spatialNetwork.getDistance(agent.getShelter()
						.getLocation(), jb.getWorkplace().getLocation(), true);

				if (dist < shortestDistance) {
					shortestDistance = dist;
					closestJob = jb;
				}
			}

			return closestJob;
		case HighestPaying:
			return allAvailableJobs.stream()
					.max(Comparator.comparing(Job::getHourlyRate)).orElse(null);
		}

		return null;
	}

	public FinancialStatus getStatus() {
		return status;
	}

	public boolean isEmployed() {
		return job != null;
	}

	public void setJob(Job job) {
		this.workingInThisJobSince = agent.getSimulationTime();
		this.job = job;
	}

	public Job getJob() {
		return job;
	}

	public LocalDateTime getWorkingInThisJobSince() {
		return workingInThisJobSince;
	}

	public double getAvailableBalance() {
		return availableBalance;
	}

	public double getWeeklyExtraBudget() {
		return weeklyExtraBudget;
	}
	
	public void reduceWeeklyExtraBudget(double amount) {
		weeklyExtraBudget -= amount;
	}
	
	public void kill() {
		this.agent = null;
		this.job = null;
		this.workingInThisJobSince = null;
	}

	/**
	 * An inner enumeration to represent job search criteria.
	 * 
	 * @author Hamdi Kavak (hkavak at gmu.edu)
	 *
	 */
	protected enum JobSearchCriteria {
		FirstAvailable, Proximity, HighestPaying
	}

}
