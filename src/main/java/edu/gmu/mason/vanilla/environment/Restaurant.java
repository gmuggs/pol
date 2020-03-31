package edu.gmu.mason.vanilla.environment;

import org.joda.time.LocalTime;

import edu.gmu.mason.vanilla.ExpenseType;
import edu.gmu.mason.vanilla.Person;
import edu.gmu.mason.vanilla.log.Characteristics;
import edu.gmu.mason.vanilla.log.Referenceable;
/**
 * General description_________________________________________________________
 * Restaurant class for serving agents food.
 *
 * @author Hamdi Kavak (hkavak at gmu.edu)
 * 
 */
@Referenceable(keyMethod = "getId", keyType = Long.class)
public class Restaurant extends BuildingUnit {

	private static final long serialVersionUID = 3151976049564919545L;
	
	@Characteristics
	private double foodCost;
	private LocalTime startTime;
	private LocalTime endTime;
	
	public Restaurant(long id, Building building) {
		super(id, building, "Restaurant");
	}

	public double getFoodCost() {
		return foodCost;
	}

	public void setFoodCost(double foodCost) {
		this.foodCost = foodCost;
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

	@Override
	public void agentLeaves(Person agent) {
	
		// agent pays the cost
		agent.getFinancialSafetyNeed().withdrawMoney(foodCost, ExpenseType.Food);
		agent.getFoodNeed().justAte();

		// remove from the building as well.
		super.agentLeaves(agent);
	}
	
}
