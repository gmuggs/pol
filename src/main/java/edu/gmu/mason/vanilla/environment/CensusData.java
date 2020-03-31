package edu.gmu.mason.vanilla.environment;

import java.util.ArrayList;
import java.util.List;

import edu.gmu.mason.vanilla.log.Skip;

/**
 * General description_________________________________________________________
 * Data structure for logging Census data
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class CensusData implements java.io.Serializable {
	private static final long serialVersionUID = 955586340922873332L;

	// for time saving, every field is public
	@Skip
	public CensusType censusType = CensusType.CENSUS_TRACT;
	public int id;
	public int population = 0;
	public int numOfHouseholds = 0;
	public double medianFamilyIncome = 0.0;
	public int totalHousingUnits = 0;
	public int occpuiedUnits = 0;
	public int vacantUnits = 0;
	public double averageTravelTime = 0.0;

	@Skip
	public List<Double> travelTimes = new ArrayList<Double>();
	@Skip
	public List<Double> familyIncomes = new ArrayList<Double>();

	public void compute() {
		if (!travelTimes.isEmpty()) {
			averageTravelTime = travelTimes.stream()
					.mapToDouble(Double::doubleValue).average()
					.orElse(Double.NaN);
		}

		if (!familyIncomes.isEmpty()) {
			familyIncomes.sort(null);
			medianFamilyIncome = familyIncomes.get(familyIncomes.size() / 2);
		}
	}
}
