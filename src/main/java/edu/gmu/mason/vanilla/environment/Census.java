package edu.gmu.mason.vanilla.environment;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.gmu.mason.vanilla.LifeStatus;
import edu.gmu.mason.vanilla.Person;
import edu.gmu.mason.vanilla.WorldModel;
import edu.gmu.mason.vanilla.log.Skip;

/**
 * General description_________________________________________________________
 * Census data structure class.
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class Census implements java.io.Serializable {
	private static final long serialVersionUID = -5431737859441714150L;

	private Map<Integer, CensusData> neighborhoods;

	@Skip
	private WorldModel model;

	public Census(WorldModel model) {
		this.model = model;
		reset();
	}

	public void reset() {
		neighborhoods = new HashMap<Integer, CensusData>();
	}

	public void collectData() {
		for (Person agent : model.getAgents()) {
			// we only consider alive agents
			if (agent.getLifeStatus() == LifeStatus.Alive) {
				addAgent(agent);
			}
		}

		for (Apartment apt : model.getAllApartments()) {
			addApartment(apt);
		}

		neighborhoods.values().forEach(b -> b.compute());
	}

	public Collection<CensusData> getNeighborhoodCensus() {
		return neighborhoods.values();
	}

	private void addAgent(Person agent) {
		Apartment apt = agent.getShelter();
		int neighborhoodId = apt.getNeighborhoodId();

		checkOrCreate(neighborhoodId);

		CensusData neighborhoodCensus = neighborhoods.get(neighborhoodId);

		neighborhoodCensus.population++;
		
		neighborhoodCensus.numOfHouseholds++;

		double income = agent.getFinancialSafetyNeed().projectedMonthlyIncome();

		neighborhoodCensus.familyIncomes.add(income);

		double travelTime = model.getSpatialNetwork().getEstimatedTravelTime(
				agent.getShelter().getLocation(),
				agent.getWorkplace().getLocation(), agent.getWalkingSpeed());

		neighborhoodCensus.travelTimes.add(travelTime);
	}

	private void addApartment(Apartment apt) {
		int neighborhoodId = apt.getNeighborhoodId();
		
		checkOrCreate(neighborhoodId);

		CensusData neighborhoodCensus = neighborhoods.get(neighborhoodId);

		Household household = apt.getHousehold();
		if (household.getMembers().isEmpty()) {
			neighborhoodCensus.vacantUnits++;
		} else {
			neighborhoodCensus.occpuiedUnits++;
		}

		neighborhoodCensus.totalHousingUnits++;
	}

	private void checkOrCreate(int neighborhoodId) {
		if (!neighborhoods.containsKey(neighborhoodId)) {
			CensusData neighborhoodCensus = new CensusData();
			neighborhoodCensus.censusType = CensusType.CENSUS_TRACT;
			neighborhoodCensus.id = neighborhoodId;
			neighborhoods.put(neighborhoodId, neighborhoodCensus);
		}
	}
}
