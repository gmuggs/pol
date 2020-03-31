package edu.gmu.mason.vanilla.environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.distribution.LogNormalDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;

import ec.util.MersenneTwisterFast;
import edu.gmu.mason.vanilla.EducationLevel;
import edu.gmu.mason.vanilla.WorldParameters;
import edu.gmu.mason.vanilla.utils.CollectionUtil;
import edu.gmu.mason.vanilla.utils.MersenneTwisterWrapper;

/**
 * General description_________________________________________________________
 * A class used to initialize the environment object quantitiy and
 * characteristics.
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu)
 * 
 */
@SuppressWarnings("serial")
public class NeighborhoodComposition {

	private WorldParameters params;

	private static final double PAY_SCALE_LOW = 0.8;
	private static final double PAY_SCALE_HS_COLLEGE = 1.0;
	private static final double PAY_SCALE_BACHELORS = 1.7;
	private static final double PAY_SCALE_GRADUATE = 2.6;

	private static final double DIFFERENT_WORKDAY_PERC_LOW = 0.5;
	private static final double DIFFERENT_WORKDAY_PERC_HS_COLLEGE = 0.20;
	private static final double DIFFERENT_WORKDAY_PERC_BACHELORS = 0.1;
	private static final double DIFFERENT_WORKDAY_PERC_GRADUATE = 0.05;

	private static Map<EducationLevel, Double> mapPayScale = new HashMap<EducationLevel, Double>() {
		{
			put(EducationLevel.Low, PAY_SCALE_LOW);
			put(EducationLevel.HighSchoolOrCollege, PAY_SCALE_HS_COLLEGE);
			put(EducationLevel.Bachelors, PAY_SCALE_BACHELORS);
			put(EducationLevel.Graduate, PAY_SCALE_GRADUATE);
		}
	};

	private static Map<EducationLevel, Double> mapChancesToHaveDifferentWorkdays = new HashMap<EducationLevel, Double>() {
		{
			put(EducationLevel.Low, DIFFERENT_WORKDAY_PERC_LOW);
			put(EducationLevel.HighSchoolOrCollege,
					DIFFERENT_WORKDAY_PERC_HS_COLLEGE);
			put(EducationLevel.Bachelors, DIFFERENT_WORKDAY_PERC_BACHELORS);
			put(EducationLevel.Graduate, DIFFERENT_WORKDAY_PERC_GRADUATE);
		}
	};

	private int numberOfApartments;
	private int numberOfSchools;
	private int numberOfWorkplaces;
	private int numberOfPubs;
	private int numberOfRestaurants;
	private int numberOfSingleAgents;
	private int numberOfFamilyAgentsWKids;
	private int numberOfFamilyAgentsWOKids;
	private MersenneTwisterWrapper rng;

	public void calculate(int numOfAgents) {
		// based on 1000 agents

		double ratio = (double) numOfAgents / 1000;

		double apartmentNum = params.numApartmentsPer1000 * ratio;
		double workplaceNum = params.numWorkplacesPer1000 * ratio;
		double pubNum = params.numPubsPer1000 * ratio;
		double restaurantNum = params.numRestaurantsPer1000 * ratio;

		// calculate number of units
		numberOfApartments = Math.toIntExact(Math.round(apartmentNum));

		double schoolLowerBound = ratio * 0.2;
		double schoolUpperBound = ratio * 1.0;
		UniformRealDistribution uRNG = new UniformRealDistribution(rng,
				schoolLowerBound, schoolUpperBound);

		numberOfSchools = Math.toIntExact(Math.round(uRNG.sample()));
		numberOfWorkplaces = Math.toIntExact(Math.round(workplaceNum));
		numberOfPubs = Math.toIntExact(Math.round(pubNum));
		numberOfRestaurants = Math.toIntExact(Math.round(restaurantNum));

		// make sure we have at least 1 school, 1 workplace, 1 pub, and 1 restaurant
		numberOfSchools = Math.max(numberOfSchools, 1);
		numberOfWorkplaces = Math.max(numberOfWorkplaces, 1);
		numberOfPubs = Math.max(numberOfPubs, 1);
		numberOfRestaurants = Math.max(numberOfRestaurants, 1);

		double singleNum = params.numOfSingleAgentsPer1000 * ratio;
		double familyWKidsNum = params.numOfFamilyAgentsWithKidsPer1000
				* ratio;

		numberOfSingleAgents = Math.toIntExact(Math.round(singleNum));
		numberOfFamilyAgentsWKids = Math.toIntExact(Math.round(familyWKidsNum));
		numberOfFamilyAgentsWOKids = numOfAgents - numberOfSingleAgents
				- numberOfFamilyAgentsWKids;
	}

	public long getRandomBuildingId(List<Building> buildings, BuildingType type) {
		List<Long> ids = new ArrayList<Long>();

		for (Building b : buildings) {
			if (b.getBuildingType().equals(type)) {
				ids.add(b.getId());
			}
		}

		UniformIntegerDistribution uRNG = new UniformIntegerDistribution(rng,
				0, ids.size() - 1);

		return ids.get(uRNG.sample());
	}

	public double generateAttractivenessNumber() {
		UniformRealDistribution uRNG = new UniformRealDistribution(rng,
				params.attractivenessLowerBound,
				params.attractivenessUpperBound);

		return uRNG.sample();
	}

	public int getSiteCapacity() {
		NormalDistribution nRNG = new NormalDistribution(rng,
				params.siteCapcityMeam, params.siteCapacitySD);
		return (int) nRNG.sample();
	}

	public EducationLevel generateEducationLevelRequirementForJobs() {
		UniformRealDistribution uRNG = new UniformRealDistribution(rng, 0.0,
				params.EDUCATION_REQ_GRADUATE);
		double percentile = uRNG.sample();

		if (percentile <= params.EDUCATION_REQ_LOW) {
			return EducationLevel.Low;
		} else if (percentile <= params.EDUCATION_REQ_HS_COLLEGE) {
			return EducationLevel.HighSchoolOrCollege;
		} else if (percentile <= params.EDUCATION_REQ_BACHELORS) {
			return EducationLevel.Bachelors;
		} else if (percentile <= params.EDUCATION_REQ_GRADUATE) {
			return EducationLevel.Graduate;
		}
		return EducationLevel.Unknown;
	}

	public int generateNumberOfRoomsForApartments() {
		UniformIntegerDistribution uRNG = new UniformIntegerDistribution(rng,
				params.apartmentRoomNumberLowerBound,
				params.apartmentRoomNumberUpperBound);

		return uRNG.sample();
	}

	public int generateNumberOfJobsAtAWorkplaceUnit() {
		UniformIntegerDistribution uRNG = new UniformIntegerDistribution(rng,
				params.numOfJobsAtWorkplaceLowerBound,
				params.numOfJobsAtWorkplaceUpperBound);

		return uRNG.sample();
	}

	public double generateHourlyRate(EducationLevel level) {
		LogNormalDistribution lnRNG = new LogNormalDistribution(rng, 2.5, 0.5);
		double lnAmount = lnRNG.sample();

		// let's not cross the minimum or maximum hourly rates
		return Math.min(
				Math.max(params.minimumHourlyRate, lnAmount
						* getPayScale(level)), params.maximumHourlyRate);
	}

	public List<DayOfWeek> generateWorkDays(EducationLevel level) {
		UniformRealDistribution uRNG = new UniformRealDistribution(rng, 0, 1);
		double num = uRNG.sample();

		List<DayOfWeek> days = new ArrayList<>();
		days.add(DayOfWeek.Monday);
		days.add(DayOfWeek.Tuesday);
		days.add(DayOfWeek.Wednesday);
		days.add(DayOfWeek.Thursday);
		days.add(DayOfWeek.Friday);

		if (num <= mapChancesToHaveDifferentWorkdays.get(level)) {
			days.add(DayOfWeek.Saturday);
			days.add(DayOfWeek.Sunday);
			CollectionUtil.shuffle(days, rng.getRandom());

			days.remove(6);
			days.remove(5);
		}

		return days;
	}

	public double generatePubHourlyCharge() {
		UniformRealDistribution uRNG = new UniformRealDistribution(rng,
				params.pubChargeLowerBound, params.pubChargeUpperBound);

		return uRNG.sample();
	}

	public double generateRestaurantCostCharge() {
		UniformRealDistribution uRNG = new UniformRealDistribution(rng,
				params.resChargeLowerBound, params.resChargeUpperBound);

		return uRNG.sample();
	}

	public double generateSchoolCapacity() {

		NormalDistribution nRNG = new NormalDistribution(rng,
				params.schoolCapacityMean, params.schoolCapacitySD);
		return nRNG.sample();
	}

	public double generateSchoolCost(double attractiveness) {

		return params.monthlySchoolCostRate * attractiveness;
	}

	public double generateApartmentRentalPrice(int numberOfBedrooms) {
		return params.baseRentRate * Math.log(numberOfBedrooms + 1);
	}

	public static double getPayScale(EducationLevel level) {
		return mapPayScale.get(level);
	}

	public NeighborhoodComposition(MersenneTwisterFast random, WorldParameters params) {
		rng = new MersenneTwisterWrapper(random);
		this.params = params;
	}

	public int getNumberOfApartments() {
		return numberOfApartments;
	}

	public int getNumberOfSchools() {
		return numberOfSchools;
	}

	public int getNumberOfWorkplaces() {
		return numberOfWorkplaces;
	}

	public int getNumberOfPubs() {
		return numberOfPubs;
	}

	public int getNumberOfRestaurants() {
		return numberOfRestaurants;
	}

	public int getNumberOfSingleAgents() {
		return numberOfSingleAgents;
	}

	public int getNumberOfFamilyAgentsWKids() {
		return numberOfFamilyAgentsWKids;
	}

	public int getNumberOfFamilyAgentsWOKids() {
		return numberOfFamilyAgentsWOKids;
	}

}
