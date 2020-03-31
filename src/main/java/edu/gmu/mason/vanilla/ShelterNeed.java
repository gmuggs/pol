package edu.gmu.mason.vanilla;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.joda.time.Days;
import org.joda.time.LocalDateTime;

import sim.util.geo.MasonGeometry;
import edu.gmu.mason.vanilla.environment.Apartment;
import edu.gmu.mason.vanilla.environment.Building;
import edu.gmu.mason.vanilla.environment.BuildingType;
import edu.gmu.mason.vanilla.environment.BuildingUnit;
import edu.gmu.mason.vanilla.environment.Classroom;
import edu.gmu.mason.vanilla.environment.SpatialNetwork;
import edu.gmu.mason.vanilla.log.Skip;
import edu.gmu.mason.vanilla.log.State;
import edu.gmu.mason.vanilla.utils.CollectionUtil;
import edu.gmu.mason.vanilla.utils.GeoUtils;

/**
 * General description_________________________________________________________
 * A class to handle shelter need functions such as finding a place to live,
 * move to different place etc. Each agent has one {@code ShelterNeed} object.
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu)
 * 
 */
public class ShelterNeed implements Need, java.io.Serializable {
	private static final long serialVersionUID = -3291705599445052021L;
	@Skip
	private Person agent;
	
	@State
	private Apartment currentShelter;
	@Skip
	private LocalDateTime homelessSince;
	@Skip
	private Apartment ghostHouse;
	@Skip
	private boolean rentChanged;

	public ShelterNeed(Person agent) {
		this.agent = agent;
		this.homelessSince = agent.getSimulationTime();
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public void satisfy() {

		// do nothing if dead, already satisfied, has no employment, or
		// transporting to somewhere

		if (agent.getLifeStatus() != LifeStatus.Alive || isSatisfied() == true
				|| agent.getCurrentMode() == PersonMode.Transport) {
			return;
		}

		if (isSatisfied() == false) { // shelter need is not satisfied and agent
										// is not in transport

			if (agent.getFinancialSafetyNeed().isEmployed() == false) {
				// if agent couldn't find a place
				// let's put the agent in somewhere of the neighborhood.
				// homeless :(
				if (placedInGhostHouse() == false) {
					findGhostHouse();
				}

				checkHomelessness();

				return;
			}

			// try to find a shelter in the same neighborhood

			// there are three types of apartment searching strategies, based on
			// agents' characteristic values
			
			//// getMaximumAllowedRental method considers both GT_L#12 and GT_L#9
			double maxMoneyForRental = agent.getFinancialSafetyNeed().getMaximumAllowedRental();
			
			Apartment apt = searchApartmentBasedOnAgentCharacteristic(maxMoneyForRental);

			if (apt != null) { // if apartment found, move to the apartment
				move(apt);
			} else {

				// if agent couldn't find a place
				// let's put the agent in somewhere of the neighborhood.
				// homeless :(
				if (placedInGhostHouse() == false) {
					findGhostHouse();
				}
				
				checkHomelessness();
				
			}
		}

	}
	

	private Apartment searchApartmentBasedOnAgentCharacteristic(double maxMoneyForRental) {
		// 1. Sociolus: closest apartment to the workplace
		// 2. Balancus: cheapest empty apartment in the neighborhood of the
		// workplace; if there is no, cheapest apartment in the world,
		// 3. Croesus: cheapest shared apartment in the world
		Apartment apt = null;

		switch (agent.getCharacteristic()) {
		case Sociolus:

			apt = findApartment(ShelterSearchCriteria.Proximity, false, maxMoneyForRental);

			if (apt == null && agent.hasFamily() == false) {
				apt = findApartment(ShelterSearchCriteria.Proximity, true, maxMoneyForRental);
			}
			break;

		case Balancus:

			apt = findApartment(ShelterSearchCriteria.Cheapest, false, maxMoneyForRental, 
					agent.getFinancialSafetyNeed().getJob()
							.getNeighborhoodId());

			if (apt == null) {
				apt = findApartment(ShelterSearchCriteria.Cheapest, false, maxMoneyForRental);
			}

			if (apt == null && agent.hasFamily() == false) {
				apt = findApartment(ShelterSearchCriteria.Cheapest, true, maxMoneyForRental);
			}
			break;

		case Croesus:

			apt = findApartment(ShelterSearchCriteria.Cheapest,agent.hasFamily() == false, maxMoneyForRental);

			if (apt == null) {
				apt = findApartment(ShelterSearchCriteria.Cheapest, false, maxMoneyForRental);
			}
			break;
		}
		return apt;
	}

	private void checkHomelessness() {
		// logger.info("Agent is homeless");
		int daysHomeless = Days.daysBetween(homelessSince,
				agent.getSimulationTime()).getDays();
		
		if (daysHomeless > agent.getModel().params.maxDaysToBeHomeless) {
			agent.exitTheWorld(LifeStatus.Abandoned); // let the agent										// be free.
		}
	}

	private void findGhostHouse() {

		List<Building> residentialBuildings = agent.getModel()
				.getUsableBuildings(agent.getNeighborhoodId(),
						BuildingType.Residental);

		if (residentialBuildings == null || residentialBuildings.size() == 0) {
			// this means there is no usable residential building so we will
			// look at all
			residentialBuildings = agent.getModel().getUsableBuildings(
					BuildingType.Residental);
		}

		Building b1 = residentialBuildings.get(agent.getModel().random
				.nextInt(residentialBuildings.size()));
		Apartment ghostApt = new Apartment(0, b1);
		List<MasonGeometry> unitLocations = agent.getModel()
				.getSpatialNetwork().getBuildingUnitTable()
				.get((int) b1.getId());
		ghostApt.setLocation(unitLocations.get(0));
		setGhostHouse(ghostApt);
	}

	@Override
	public boolean isSatisfied() {
		return currentShelter != null;
	}

	/**
	 * Finds a school nearest to the agent and assigns the kid to there.
	 */
	public void assignSchool() {
		List<BuildingUnit> allClassrooms = agent.getModel()
				.getUsableClassrooms().stream().map(BuildingUnit.class::cast)
				.collect(Collectors.toList());
		Classroom classroom = (Classroom) GeoUtils.findNearestUnit(
				getCurrentShelter().getLocation(), allClassrooms);

		if (classroom != null) {
			agent.getFamily().setClassroom(classroom);
			double cost = classroom.getMonthlyCost();

			// multiply the cost per kid
			cost = cost * (agent.getFamily().getNumberOfPeople() - 2);
			agent.getFinancialSafetyNeed().withdrawMoney(cost, ExpenseType.Education);
		}
	}
	
	/**
	 * Routine for vacating a rental place
	 */
	public void vacate() {

		// get refund
		double amount = currentShelter.getAdjustedRentalCostPerPerson();
		agent.getRentalAdjustment(amount);

		currentShelter.getHousehold().removeMember(agent);
		currentShelter = null;
	}

	public void move(Apartment apt) {

		// check if this apt already has occupants
		List<Person> peopleInTheApt = apt.getHousehold().getMembers();
		double rentalAmount;

		if (peopleInTheApt.size() > 0) { // that means the agent will move as a
											// roommate

			// now, the agent found a roommate.
			// Let's add this agent to the household and share the cost for
			// rental.
			rentalAmount = apt.getRentalCostAdjusted()
					/ (peopleInTheApt.size() + 1);

		} else { // agent is moving as the first person/family in this apartment
			rentalAmount = apt.getRentalCostAdjusted();
		}

		this.agent.getFinancialSafetyNeed().withdrawMoney(rentalAmount, ExpenseType.Shelter);

		for (Person ppl : peopleInTheApt) { // this loop will execute for
											// roommates
			// the rental was already paid by current household members, so give
			// this money to them
			ppl.getRentalAdjustment(rentalAmount
					/ (double) peopleInTheApt.size());
			
			// let's add these roommates as a friendFamily network
			agent.getLoveNeed().strengthenTies(ppl.getAgentId());
			ppl.getLoveNeed().strengthenTies(agent.getAgentId());
		}

		apt.getHousehold().addMember(this.agent);
		setCurrentShelter(apt);
		agent.setNeighborhoodId(apt.getNeighborhoodId());
		// logging
		agent.writeInterventionLog("MoveIn", agent.currentHomeStatus());
	}

	public void emptyGhostHouse() {
		this.ghostHouse = null;
	}

	// APARTMENT FINDING METHODS
	
	/**
	 * Only call this method when the agent has an apartment
	 */
	public void forceToMoveToCheaperApartment() {
		
		double currentRent = getCurrentShelter().getRentalCostPerPerson();
		double delta = 0.1; // difference to be used to find cheaper than current rent
		
		Apartment apt = searchApartmentBasedOnAgentCharacteristic(currentRent-delta);
		
		if (apt!=null) {
			vacate();
			move(apt);
		}
	}

	
	private Apartment findApartment(ShelterSearchCriteria criteria, boolean enforceRoommate, double maxRental) {
	
		return findApartment(criteria, enforceRoommate, maxRental, -1);
	}

	private Apartment findApartment(ShelterSearchCriteria criteria, boolean enforceRoommate, 
			double maxMoneyForRental, int neighborhoodId) {

		List<Apartment> apartments = agent.getModel()
				.getUsableApartmentsWithAvailableCapacity();

		if (neighborhoodId >= 0) { // this means if a neighborhood is specified,
									// just keep apartments on that neighborhood
			apartments = apartments.stream()
					.filter(p -> p.getNeighborhoodId() == neighborhoodId)
					.collect(Collectors.toList());
		}

		if (agent.hasFamily() == true) {

			// the following statement ensures the house is empty, at least two
			// people can fit, and affordable
			apartments = apartments
					.stream()
					.filter(p -> p.getPersonCapacity() == p
							.getRemainingPersonCapacity()
							&& p.getPersonCapacity() >= 2
							&& p.getRentalCost() <= maxMoneyForRental)
					.collect(Collectors.toList());

			return chooseApartmentBasedOnCriteria(criteria, apartments.stream());
		}

		if (enforceRoommate == true) {
			// we make sure there is at least 1 person living in the apartment
			// and it's affordable
			apartments = apartments
					.stream()
					.filter(p -> p.getPersonCapacity() != p
							.getRemainingPersonCapacity()
							&& (p.getRentalCost() / (double) (p.getHousehold()
									.getMembers().size() + 1)) <= maxMoneyForRental)
					.collect(Collectors.toList());

		} else {
			// the following statement ensures the house is empty and affordable
			apartments = apartments
					.stream()
					.filter(p -> p.getPersonCapacity() == p
							.getRemainingPersonCapacity()
							&& p.getRentalCost() <= maxMoneyForRental)
					.collect(Collectors.toList());
		}

		return chooseApartmentBasedOnCriteria(criteria, apartments.stream());
	}

	private Apartment chooseApartmentBasedOnCriteria(
			ShelterSearchCriteria criteria, Stream<Apartment> apartmentStream) {
		List<Apartment> affordableApartments;
		switch (criteria) {
		case Cheapest:
			return apartmentStream.min(
					Comparator.comparing(Apartment::getRentalCost))
					.orElse(null);
		case Proximity:
			affordableApartments = apartmentStream.collect(Collectors.toList());
			SpatialNetwork spatialNetwork = agent.getModel()
					.getSpatialNetwork();
			double shortestDistance = 999999999;
			Apartment closestApartment = null;

			for (Apartment apt : affordableApartments) {
				double dist = spatialNetwork.getDistance(agent.getWorkplace()
						.getLocation(), apt.getLocation(), true);

				if (dist < shortestDistance) {
					shortestDistance = dist;
					closestApartment = apt;
				}
			}

			return closestApartment;
		case FirstAvailable:
			affordableApartments = apartmentStream.collect(Collectors.toList());

			return CollectionUtil.getRandomItem(affordableApartments, agent.getModel().random).get();
		}
		return null;
	}
	
	public void kill() {
		this.agent = null;
		this.currentShelter = null;
		this.ghostHouse = null;
		this.homelessSince = null;
	}

	private boolean placedInGhostHouse() {
		return this.ghostHouse != null;
	}

	private void setGhostHouse(Apartment ghostHouse) {
		this.ghostHouse = ghostHouse;
		this.homelessSince = agent.getSimulationTime();
	}

	/**
	 * Returns current shelter or ghost shelter
	 * 
	 * @return
	 */
	public Apartment getCurrentShelter() {
		return currentShelter == null ? ghostHouse : currentShelter;
	}

	public void setCurrentShelter(Apartment currentShelter) {
		this.currentShelter = currentShelter;
		homelessSince = null; // agent is no longer homeless
	}

	public boolean isRentChanged() {
		return rentChanged;
	}

	public void setRentChanged(boolean rentChanged) {
		this.rentChanged = rentChanged;
	}

	public enum ShelterSearchCriteria {
		FirstAvailable, Proximity, Cheapest
	}

}
