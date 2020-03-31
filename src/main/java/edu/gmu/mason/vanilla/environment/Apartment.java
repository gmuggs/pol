package edu.gmu.mason.vanilla.environment;

import java.util.List;

import org.joda.time.LocalDateTime;

import edu.gmu.mason.vanilla.Person;
import edu.gmu.mason.vanilla.log.Referenceable;
import edu.gmu.mason.vanilla.log.Skip;

/**
 * General description_________________________________________________________
 * A class to represent shelters that agents live.
  * @author Hamdi Kavak (hkavak at gmu.edu)
 * 
 */
@Referenceable(keyMethod = "getId", keyType = Long.class)
public class Apartment extends BuildingUnit {

	private static final long serialVersionUID = -2153189207690107338L;
	private double rentalCost;
	
	@Skip
	private Household household;

	public Apartment(long id, Building building) {
		super(id, building, "Apartment");
		household = new Household();
	}

	public int getRemainingPersonCapacity() {
		if (household.getMembers().size() == 1
				&& household.getMembers().get(0).hasFamily()) {
			return 0;
		}
		return this.getPersonCapacity() - household.getMembers().size();
	}

	public double getRentalCostPerPerson() {
		List<Person> households = this.household.getMembers();
		
		double costPerPerson = rentalCost / (double) households.size();

		return costPerPerson;
	}

	public double getAdjustedRentalCostPerPerson() {
		List<Person> households = this.household.getMembers();
		double costPerPerson = getRentalCostAdjusted()
				/ (double) households.size();

		return costPerPerson;
	}

	public Household getHousehold() {
		return household;
	}

	public double getRentalCost() {
		return rentalCost;
	}

	public double getRentalCostAdjusted() { // adjusted based on the day of the
											// month

		LocalDateTime now = model.getSimulationTime();
		int numberOfDaysThisMonth = now.dayOfMonth().getMaximumValue();

		// for instance, today is 29th day of the month and this month is 30 day
		// long.
		// the agent needs to pay for 29th and 30th so just two days out of 30.
		int dayToBeSpent = numberOfDaysThisMonth - now.getDayOfMonth() + 1;

		return rentalCost
				* ((double) dayToBeSpent / (double) numberOfDaysThisMonth);
	}

	public void setRentalCost(double rentalCost) {
		this.rentalCost = rentalCost;
	}
}
