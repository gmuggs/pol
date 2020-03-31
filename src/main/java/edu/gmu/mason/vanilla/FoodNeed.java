package edu.gmu.mason.vanilla;

import org.joda.time.Days;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;

import edu.gmu.mason.vanilla.environment.AgentMobility;
import edu.gmu.mason.vanilla.environment.Pub;
import edu.gmu.mason.vanilla.environment.Restaurant;
import edu.gmu.mason.vanilla.environment.Travel;
import edu.gmu.mason.vanilla.log.Characteristics;
import edu.gmu.mason.vanilla.log.ExtLogger;
import edu.gmu.mason.vanilla.log.Skip;
import edu.gmu.mason.vanilla.log.State;

/**
 * General description_________________________________________________________
 * A class to handle food need functions such as hunger, fullness, choosing
 * where to eat and so on. Each agent has one {@code FoodNeed} object.
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu)
 * 
 */
public class FoodNeed implements Need, java.io.Serializable {
	private static final long serialVersionUID = 1247800092072951966L;
	private static double MAX_FULLNESS = 100;

	@State
	private double fullness;
	@State
	private FoodNeedStatus status;
	@State
	private int numberOfMealsTaken;
	@Characteristics
	private double fullnessReachTimeInMinutes;
	@Characteristics
	private double keepingFullTimeInMinutes;
	@Skip
	private double fullnessDecreasePerStep;
	@Skip
	private double fullnessIncreasePerStep;
	@Characteristics
	private double hungryTreshold;
	@Characteristics
	private double starvingTreshold;
	@Characteristics
	private double appetite;
	@Skip
	private LocalDateTime lastTimeAte;
	@Skip
	private Person agent;

	// java utils
	private final static ExtLogger logger = ExtLogger.create(FoodNeed.class);

	/**
	 * The constructor to initialize the food need with the default appetite
	 * value.
	 * 
	 * @param agent
	 *            a reference to the agent.
	 */
	public FoodNeed(Person agent) {
		this(agent, agent.getModel().params.DEFAULT_APPETITE_VALUE);
	}

	/**
	 * The constructor to initialize the food need with a specific appetite
	 * value.
	 * 
	 * @param agent
	 * @param appetite
	 */
	public FoodNeed(Person agent, double appetite) {
		this.agent = agent;
		this.fullness = MAX_FULLNESS;
		this.setAppetite(appetite);
		numberOfMealsTaken = 0;
		justAte();
	}

	/**
	 * This is how food need is updated.
	 */
	@Override
	public void update() {
		
		// calculate the minute difference since last time eaten which will update the fullness
		int minuteDiff = Minutes.minutesBetween(lastTimeAte,
				agent.getSimulationTime()).getMinutes();
		
		//// (if statements below shows the effect of eating characteristics on Food Need Status and fullness)
		switch (this.status) {
		case JustAte:

			if (minuteDiff <= fullnessReachTimeInMinutes) {
				// incrementally increase fullness until reaching max fullness
				this.fullness += fullnessIncreasePerStep;
				this.fullness = Math.min(this.fullness, MAX_FULLNESS);
			} else {
				this.status = FoodNeedStatus.BecameFull;
				this.fullness = MAX_FULLNESS;
			}
			break;

		case BecameFull:

			if (minuteDiff >= (fullnessReachTimeInMinutes + keepingFullTimeInMinutes)) {
				this.status = FoodNeedStatus.BecomingHungry;
			}
			break;

		case BecomingHungry:

			fullness -= fullnessDecreasePerStep;

			if (fullness <= hungryTreshold) {
				this.status = FoodNeedStatus.Hungry;
			}

			break;

		case Hungry:

			fullness -= fullnessDecreasePerStep;
			fullness = Math.max(fullness, starvingTreshold); // underflow
																// protection

			if (fullness <= starvingTreshold) {
				this.status = FoodNeedStatus.Starving;
			}

			break;
		case Starving:
			break;
		}
	}

	@Override
	public void satisfy() {
		PersonMode currentMode = agent.getCurrentMode();
		WorldModel model = agent.getModel();
		AgentMobility mobility = agent.getMobility();
		DailyPlan dailyPlanForToday = agent.getTodaysPlan();

		if (status == FoodNeedStatus.Starving
				&& Days.daysBetween(lastTimeAte, model.getSimulationTime())
						.getDays() > model.params.maxDaysToBeStarving
				&& currentMode != PersonMode.AtRestaurant) { // don't let the
																// agent die if
																// at a
																// restaurant
																// already.
			agent.exitTheWorld(LifeStatus.DiedDueStarving); // RIP
			return;
		}

		if (isSatisfied() == false && currentMode != PersonMode.Transport
				&& currentMode != PersonMode.AtRestaurant) { // if agent is
																// hungry and
																// not in
																// transport and
																// nor at a
																// restaurant
			
			// identify eat location. there are three options
			// 1) eat at home on work day mornings
			// 2) eat at a restaurant at work or home (after coming back from
			// work or on non-work) days
			// 3) eat while at sites

			if (currentMode == PersonMode.AtHome) {

				boolean preConditionToEatHome = agent.getShelterNeed()
						.isSatisfied()
						&& agent.getFinancialSafetyNeed()
								.getRemainingFoodBudgetForToday() >= model.params.mealCostAtHome;
				boolean isWorkdayMorning = dailyPlanForToday.isWorkDay() == true
						&& dailyPlanForToday.hasBeenAtWork() == false;

				if (preConditionToEatHome
						&& (isWorkdayMorning || model.random.nextBoolean())) {
					eatAtHome();
				} else {
					
					double budget = agent.getFinancialSafetyNeed().getRemainingFoodBudgetForToday();
					
					Restaurant restaurant = agent.getCurrentUnit().getNearestRestaurant(budget);

					// eat at a restaurant if there is one
					if (restaurant == null && preConditionToEatHome == true) {
						eatAtHome();
					} else if (restaurant != null) {
						Travel travel = new Travel(agent.getLocation(),
								restaurant.getLocation(),
								model.params.minuteSpentAtRestaurant,
								VisitReason.Restaurant_WantToEatOutside);
						mobility.beginToTransport(travel,
								PersonMode.AtRestaurant, restaurant, true);
					}
				}

			} else if (currentMode == PersonMode.AtRecreation) {

				// cost at the current pub
				Pub pub = (Pub) agent.getCurrentUnit();
				if (pub.getHourlyCost() <= agent.getFinancialSafetyNeed().getRemainingFoodBudgetForToday()) {
					agent.getFinancialSafetyNeed().withdrawMoney(pub.getHourlyCost(), ExpenseType.Food);
					justAte();
					
				} else {
					double budget = agent.getFinancialSafetyNeed().getRemainingFoodBudgetForToday();
					Restaurant restaurant = agent.getCurrentUnit().getNearestRestaurant(budget);

					// eat at a restaurant if there is one
					if (restaurant != null) {
					Travel travel = new Travel(agent.getLocation(),
								restaurant.getLocation(),
								model.params.minuteSpentAtRestaurant,
								VisitReason.Restaurant_WantToEatOutside);
						mobility.beginToTransport(travel,
								PersonMode.AtRestaurant, restaurant, false);
					}
				}

			} else if (currentMode == PersonMode.AtWork) {
				double budget = agent.getFinancialSafetyNeed().getRemainingFoodBudgetForToday();
				Restaurant restaurant = agent.getCurrentUnit().getNearestRestaurant(budget);

				if (restaurant != null) {
					
					Travel travel = new Travel(agent.getLocation(),
							restaurant.getLocation(),
							model.params.minuteSpentAtRestaurant,
							VisitReason.Restaurant_WantToEatOutside);

					// if the agent is currently at work, it needs to compensate
					// the time that's spent on restaurant travel

					if (agent.getCurrentMode() == PersonMode.AtWork) {

						int restaurant_distance_as_ticks = model
								.getSpatialNetwork().getDistanceAsTicks(
										agent.getLocation(),
										restaurant.getLocation(),
										model.getMinutePerStep(),
										mobility.getSpeed());
						int eating_at_restaurant_time_as_ticks = (int) (model.params.minuteSpentAtRestaurant / model
								.getMinutePerStep());
						int total_ticks_to_pass = restaurant_distance_as_ticks
								* 2 + eating_at_restaurant_time_as_ticks; // we
																			// consider
																			// round
																			// trip

						LocalTime normalLeaveTime = agent.getTodaysPlan()
								.getLeaveTimeFromWork();
						if (normalLeaveTime != null) {
							LocalTime updatedLeaveTime = normalLeaveTime
									.plusMinutes(total_ticks_to_pass * model.getMinutePerStep());
							agent.getTodaysPlan().setLeaveTimeFromWork(updatedLeaveTime);
						}
					}
					mobility.beginToTransport(travel, PersonMode.AtRestaurant,
							restaurant, true);
				}
			}
		}

		if (currentMode == PersonMode.AtRestaurant) { // means agent was hungry
														// and went to a
														// restaurant

			if (((Restaurant) agent.getCurrentUnit())
					.isTimeToLeaveForAgent(agent) == true) {
				// we use the last available origin as our destination
				Travel travel = new Travel(agent.getLocation(), mobility
						.getOriginUnit().getLocation(),
						mobility.getPlannedVisitLength(),
						VisitReason.ComingBackFromRestaurant);
				mobility.beginToTransport(travel, mobility.getOrigin(),
						mobility.getOriginUnit(), false);
			}
		}
	}

	@Override
	public boolean isSatisfied() {
		return status != FoodNeedStatus.Hungry
				&& status != FoodNeedStatus.Starving;
	}

	public void resetNumberOfMealsTaken() {
		this.numberOfMealsTaken = 0;
	}

	public void justAte() {
		lastTimeAte = agent.getSimulationTime();
		this.status = FoodNeedStatus.JustAte;
		fullnessIncreasePerStep = (MAX_FULLNESS - fullness)
				/ (fullnessReachTimeInMinutes / (double) this.agent.getModel()
						.getMinutePerStep());
		numberOfMealsTaken++;
	}

	private void eatAtHome() {
		agent.getFinancialSafetyNeed().withdrawMoney(agent.getModel().params.mealCostAtHome, ExpenseType.Food);
		justAte();
	}
	
	public void kill() {
		this.agent = null;
		this.lastTimeAte = null;
	}
	// Getters/Setters

	public double getFullness() {
		return fullness;
	}

	/**
	 * 0: doesn't have a big desire for food. 1: very strong desire for food.
	 * 
	 * @param appetite
	 */
	public void setAppetite(double appetite) {
		this.appetite = appetite;
		if (appetite > agent.getModel().params.appetiteUpperBound
				|| appetite < agent.getModel().params.appetiteLowerBound) {
			logger.error("Appetite value should be within ["
					+ agent.getModel().params.appetiteLowerBound + ","
					+ agent.getModel().params.appetiteUpperBound + ")");
			return;
		}
		// low appetite slow mechanism to reach fullness
		fullnessReachTimeInMinutes = 60 - appetite * 30;
		keepingFullTimeInMinutes = 180 - appetite * 60;

		fullnessDecreasePerStep = this.agent.getModel().getMinutePerStep()
				* appetite * 0.65;
		hungryTreshold = 30 + 20 * appetite;
		starvingTreshold = 0;
	}

	public double getAppetite() {
		return appetite;
	}

	public int getNumberOfMealsTaken() {
		return numberOfMealsTaken;
	}
	
	public FoodNeedStatus getStatus() {
		return status;
	}
}
