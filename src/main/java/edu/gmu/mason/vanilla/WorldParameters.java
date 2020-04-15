package edu.gmu.mason.vanilla;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.joda.time.LocalDateTime;

import edu.gmu.mason.vanilla.EditableProperty;
import edu.gmu.mason.vanilla.log.Skip;
import edu.gmu.mason.vanilla.utils.CustomConversionHandler;
import edu.gmu.mason.vanilla.utils.SimulationTimeStepSetting;

/**
 * General description_________________________________________________________
 * This is a data structure class to keep all model parameters. The values are
 * automatically loaded from a config file
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu), Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class WorldParameters extends AnnotatedPropertied {

	private static final long serialVersionUID = 3872954334021542511L;

	// CONSTANTS
	public static final String DEFAULT_PROPERTY_FILE_NAME = "parameters.properties";

	// SIMULATION
	public static final long SEED = 1;
	public static final int ONE_STEP_TIME = 5;
	public static final LocalDateTime INITIAL_SIMULATION_TIME = LocalDateTime.parse("2019-07-01");
	public static final LocalDateTime WARMUP_PERIOD_END_TIME = LocalDateTime.parse("2019-07-31");
	public static final long MAX_SIMULATIONSTEPS = 105120;

	// ENVIRONMENT
	public static final int NUM_APARTMENTS_PER_1000 = 1500;
	public static final int NUM_WORKPLACES_PER_1000 = 250;
	public static final int NUM_PUBS_PER_1000 = 10;
	public static final int NUM_RESTAURANTS_PER_1000 = 20;
	public static final int NUM_OF_AGENTS = 250;
	public static final int NUM_OF_AGENT_INTERESTS = 10;
	public static final double BASE_RENT_RATE = 500;
	public static final String DEFAULT_MAPS = "campus_data";

	// MODEL
	public static final int BASE_AGENT_AGE = 18;
	public static final int ADDITIONAL_AGENT_AGE_MIN = 0;
	public static final int ADDITIONAL_AGENT_AGE_MAX = 42;
	public static final double MAXIMUM_ALLOWED_RENTAL_SALARY_RATIO = 0.3333;
	public static final int MAX_DAYS_TO_BE_HOMELESS = 3;
	public static final int MAX_DAYS_TO_BE_STARVING = 3;
	public static final double MINUTE_SPENT_AT_RESTAURANT = 20.0;
	public static final int PREPARATION_TIME_IN_MINUTES = 10;
	public static final boolean SHOW_AGENT_INTEREST_COLOR = false;
	public static final boolean ENABLE_PRECOMPUTING_PATHS = false;
	public static final double AGENT_WALKING_SPEED = 1.4;
	public static final double RES_CHARGE_LOWER_BOUND = 4.0;
	public static final double RES_CHARGE_UPPER_BOUND = 6.0;
	public static final double APPETITE_LOWER_BOUND = 0.2;
	public static final double APPETITE_UPPER_BOUND = 0.8;
	public static final double DEFAULT_APPETITE_VALUE = 0.5;

	// FINANCE
	public static final double MEAL_COST_AT_HOME = 4.0;
	public static final int WORK_HOURS_PER_DAY = 8;
	public static final double BASE_INITIAL_BALANCE = 1500.0;
	public static final double TARGETED_SAVING_AMOUNT_WITH_RESPECT_TO_RENT = 0.0;
	public static final int MINIMUM_SITE_VISIT_LENGTH_IN_MINUTES = 20;
	public static final int MAXIMUM_SITE_VISIT_LENGTH_IN_MINUTES = 180;

	// NETWORK
	public static final double FOCAL_CLOSURE_PROBABILITY = 0.0025;
	public static final double CYCLIC_CLOSURE_PROBABILITY = 0.01;
	public static final double INITIAL_NETWORK_EDGE_WEIGHT = 0.01;
	public static final double NETWORK_EDGE_DECAY_FACTOR = 0.7;
	public static final double NETWORK_EDGE_WEIGHT_STRENGTHENING_RATE = 0.04;
	public static final double NETWORK_EDGE_DELETION_THRESHOLD = 0.001;
	public static final boolean USE_WORK_NETWORK = true;
	public static final double MAX_NUM_OF_FRIENDS = 40;
	public static final double MAX_LONELY_DAYS = 7;
	public static final double NETWORK_WEIGHT_UPPER_BOUND = 1.0;
	public static final int STABLE_RELATIONSHIP_PERIOD_IN_MIN = 60;


	// PUB
	public static final int NUMBER_OF_NEAREST_PUBS = 5;
	public static final int NUMBER_OF_INTERESTS_TO_CONSIDER = 3;
	public static final int MAX_GROUP_MEETING_SIZE = 20;
	public static final int MAX_GROUP_SIZE_TO_JOIN_FOR_ALONE_AGENTS = 2;
	public static final double PUB_CHOICE_EXPONENTIAL_DECAY_CONSTANT = 0.55;
	public static final double PUB_CHOICE_CLOSENESS_COEFFICIENT = 1.0;
	public static final double PUB_CHOICE_AGE_SIMILARITY_COEFFICIENT = 1.0;
	public static final double PUB_CHOICE_INCOME_SIMILARITY_COEFFICIENT = 1.0;
	public static final double PUB_CHOICE_INTEREST_SIMILARITY_COEFFICIENT = 1.2;
	public static final int MAX_NUMBER_OF_VISITOR_LOGS = 1000;
	public static final int MIN_NUMBER_OF_VISITOR_LOGS_REQUIRED_FOR_PLACE_PROFILE = 100;
	public static final double PUB_CHARGE_LOWER_BOUND = 5.0;
	public static final double PUB_CHARGE_UPPER_BOUND = 15.0;
	public static final int MAX_DAYS_TO_EXPIRE_FOR_PUB_PROFILE = 30;

	// SOCIALITY
	public static final int DEFAULT_TRAVEL_TIME_IN_MINUTES = 30;
	public static final int NUMBER_OF_DAYS_TO_CONSIDER_FOR_MEASURING_SOCIAL_STATUS = 5;
	public static final double INITIAL_SOCIAL_STATUS_WEIGHT = 0.5;
	public static final double SOCIAL_STATUS_DECAY_FACTOR = 0.8;
	public static final double MAX_SOCIAL_STATUS_VALUE = 1.0;
	public static final double MIN_SOCIAL_STATUS_VALUE = 0.01;
	public static final double SOCIAL_STATUS_INCREASE_VALUE = 0.07;
	public static final double SOCIAL_STATUS_DECREASE_VALUE = 0.03;

	// NEIGHBORHOOD
	public static final double ATTRACTIVENESS_LOWER_BOUND = 0.0;
	public static final double ATTRACTIVENESS_UPPER_BOUND = 1.0;
	public static final int APARTMENT_ROOM_NUMBER_LOWER_BOUND = 1;
	public static final int APARTMENT_ROOM_NUMBER_UPPER_BOUND = 4;
	public static final int NUMBER_OF_JOBS_AT_A_WORKPLACE_LOWER_BOUND = 2;
	public static final int NUMBER_OF_JOBS_AT_A_WORKPLACE_UPPER_BOUND = 9;
	public static final double SCHOOL_CAPACITY_MEAN = 420.0;
	public static final double SCHOOL_CAPACITY_SD = 120.0;
	public static final double SITE_CAPACITY_MEAN = 80.0;
	public static final double SITE_CAPACITY_SD = 20.0;
	public static final double MONTHLY_SCHOOL_COST_RATE = 100.0;
	public static final double MINIMUM_HOURLY_RATE = 10.0;
	public static final double MAXIMUM_HOURLY_RATE = 100.0;
	public static final double INITIAL_ADDITIONAL_BALANCE_LOWER_BOUND = 500.0;
	public static final double INITIAL_ADDITIONAL_BALANCE_UPPER_BOUND = 1000.0;

	public static final int NUM_OF_SINGLE_AGENTS_PER_1000 = 335;
	public static final int NUM_OF_FAMILY_AGENTS_WITH_KIDS_PER_1000 = 298;

	public double EDUCATION_REQ_LOW = 10.0;
	public double EDUCATION_REQ_HS_COLLEGE = EDUCATION_REQ_LOW + 54.0;
	public double EDUCATION_REQ_BACHELORS = EDUCATION_REQ_HS_COLLEGE + 23.0;
	public double EDUCATION_REQ_GRADUATE = EDUCATION_REQ_BACHELORS + 13.0;

	// variables
	public long seed;

	// simulation time parameters
	public int oneStepTime;
	public LocalDateTime initialSimulationTime;
	public LocalDateTime warmupPeriodEndTime;
	public SimulationTimeStepSetting timeStepUnit;
	public long maxSimulationSteps;

	// general model parameters
	@EditableProperty(group = "Init", description = "Number of agents", lower = "100", upper = "10000", readOnly = false)
	public int numOfAgents;
	@EditableProperty(group = "Init", description = "Number of agent interests", lower = "5", upper = "10", readOnly = false)
	public int numOfAgentInterests;
	public int baseAgentAge;
	public int additionalAgentAgeMin;
	public int additionalAgentAgeMax;
	@EditableProperty(group = "Init", description = "Maximum permitted rental/salary ratio", lower = "0.0", upper = "1.0", readOnly = false)
	public double maximumAllowedRentalSalaryRatio;
	@EditableProperty(group = "Init", description = "Number days allowed to be homeless", lower = "3", upper = "7", readOnly = false)
	public int maxDaysToBeHomeless;
	@EditableProperty(group = "Init", description = "Number days allowed to be starving", lower = "3", upper = "10", readOnly = false)
	public int maxDaysToBeStarving;
	@EditableProperty(group = "Init", description = "Eating time (minute) at restaurant", lower = "10", upper = "30", readOnly = false)
	public double minuteSpentAtRestaurant;
	public int preparationTimeInMinutes;
	@EditableProperty(group = "Init", description = "Show agent interest color", lower = "true", upper = "false", readOnly = false)
	public boolean showAgentInterestColor;
	public boolean enablePrecomputingPaths;
	@EditableProperty(group = "Init", description = "Agent walking speed", lower = "1.0", upper = "3.0", readOnly = false)
	public double agentWalkingSpeed;

	// economics/money parameters
	// GT: node "Meal Cost at Home"; 1 of 1; next 2 lines
	@EditableProperty(group = "Init", description = "Meal cost at home", lower = "4.0", upper = "10.0", readOnly = false)
	public double mealCostAtHome;
	@EditableProperty(group = "Init", description = "Work hours / day", lower = "6", upper = "10", readOnly = false)
	public int workHoursPerDay;
	public double baseInitialBalance;
	public double targetedSavingAmountWithRespectToRent;
	public int minimumSiteVisitLengthInMinutes;
	public int maximumSiteVisitLengthInMinutes;
	public double baseRentRate;
	public String maps;

	// Network parameters
	@EditableProperty(group = "Behavior", description = "Focal closure probability", lower = "0.0", upper = "1.0", readOnly = false)
	public double focalClosureProbability;
	@EditableProperty(group = "Behavior", description = "Cyclic closure probability", lower = "0.0", upper = "1.0", readOnly = false)
	public double cyclicClosureProbability;
	@EditableProperty(group = "Group Network", description = "Initial network edge weight", lower = "0.001", upper = "1.0", readOnly = false)
	public double initialNetworkEdgeWeight;
	@EditableProperty(group = "Group Network", description = "Network edge decay factor", lower = "0.0", upper = "1.0", readOnly = false)
	public double networkEdgeDecayFactor;
	@EditableProperty(group = "Group Network", description = "Network edge increase factor", lower = "1.0", upper = "2.0", readOnly = false)
	public double networkEdgeWeightStrengtheningRate;
	// GT: node "Link Break-up Threshold"; 1 of 1; next 2 lines
	@EditableProperty(group = "Group Network", description = "Network edge deletion threshold", lower = "0.0", upper = "1.0", readOnly = false)
	public double networkEdgeDeletionThreshold;
	@EditableProperty(group = "Group Network", description = "Use work network for meetings in addition to friend network", lower = "false", upper = "true", readOnly = false)
	public boolean useWorkNetwork;
	@EditableProperty(group = "Behavior", description = "Max target number of friends of social Person", lower = "20", upper = "50", readOnly = false)
	public double maxNumOfFriends;
	@EditableProperty(group = "Behavior", description = "Max number of days to start feeling lonely (unhappy) ", lower = "5", upper = "10", readOnly = false)
	public double maxLonelyDays;
	@EditableProperty(group = "Behavior", description = "Stable", lower = "60", upper = "180", readOnly = false)
	public int stableRelationshipPeriodInMin;

	public int numApartmentsPer1000;
	public int numWorkplacesPer1000;
	public int numPubsPer1000;
	public int numRestaurantsPer1000;

	public double appetiteLowerBound;
	public double appetiteUpperBound;

	public double resChargeLowerBound;
	public double resChargeUpperBound;

	public double pubChargeLowerBound;
	public double pubChargeUpperBound;

	// sociality parameters
	@EditableProperty(group = "Sociality", description = "Number of days to consider for measuring social status", lower = "5", upper = "7", readOnly = false)
	public int numberOfDaysToConsiderForMeasuringSocialStatus;
	@EditableProperty(group = "Sociality", description = "Initial social status weight", lower = "0.1", upper = "1.0", readOnly = false)
	public double initialSocialStatusWeight;
	@EditableProperty(group = "Sociality", description = "Social status decay factor", lower = "0.5", upper = "1.0", readOnly = false)
	public double socialStatusDecayFactor;
	@EditableProperty(group = "Sociality", description = "Max social status value", lower = "0.9", upper = "1.0", readOnly = false)
	public double maxSocialStatusValue;
	@EditableProperty(group = "Sociality", description = "Min social status value", lower = "0.01", upper = "0.1", readOnly = false)
	public double minSocialStatusValue;
	@EditableProperty(group = "Sociality", description = "Social status increase value", lower = "0.05", upper = "0.1", readOnly = false)
	public double socialStatusIncreaseValue;
	@EditableProperty(group = "Sociality", description = "Social status decrease value", lower = "0.01", upper = "0.05", readOnly = false)
	public double socialStatusDecreaseValue;

	// Environment
	public double attractivenessLowerBound;
	public double attractivenessUpperBound;
	public int apartmentRoomNumberLowerBound;
	public int apartmentRoomNumberUpperBound;
	public int numOfJobsAtWorkplaceLowerBound;
	public int numOfJobsAtWorkplaceUpperBound;
	public double schoolCapacityMean;
	public double schoolCapacitySD;
	public double siteCapcityMeam;
	public double siteCapacitySD;
	public double monthlySchoolCostRate;
	public double minimumHourlyRate;
	public double maximumHourlyRate;
	public double initialAdditionalBalanceLowerBound;
	public double initialAdditionalBalanceUpperBound;
	public int numOfSingleAgentsPer1000;
	public int numOfFamilyAgentsWithKidsPer1000;

	@Skip
	@EditableProperty(group = "Intervention", description = "Initial intervention input file path", lower = "", upper = "", readOnly = true)
	public String initialManipulationFilePath = "manipulations.json";
	// when simulation wake up at a checkpoint, this path will be used to add
	// manipulation.
	@Skip
	@EditableProperty(group = "Intervention", description = "Additional intervention input file path", lower = "", upper = "", readOnly = true)
	public String additionalManipulationFilePath = "additionalManipulations.json";

	// pub preference parameters
	@EditableProperty(group = "Behavior", description = "Number of nearest pubs", lower = "1", upper = "10", readOnly = false)
	public int numberOfNearestPubs;
	@EditableProperty(group = "Behavior", description = "Number of interests to consider", lower = "3", upper = "3", readOnly = true)
	public int numberOfInterestsToConsider;
	public int maxGroupMeetingSize;
	@EditableProperty(group = "Behavior", description = "Maximum group size to join for conversation", lower = "1", upper = "10", readOnly = false)
	public int maxGroupSizeToJoinForAloneAgents;
	@EditableProperty(group = "Behavior", description = "Pub choice exponential decay constant", lower = "0.0", upper = "1.0", readOnly = false)
	public double pubChoiceExponentialDecayConstant;
	@EditableProperty(group = "Behavior", description = "The effect of spatial proximity on pub choice", lower = "0.001", upper = "5.0", readOnly = false)
	public double pubChoiceClosenessCoefficient;
	@EditableProperty(group = "Behavior", description = "The effect of age similarity on pub choice", lower = "0.001", upper = "5.0", readOnly = false)
	public double pubChoiceAgeSimilarityCoefficient;
	@EditableProperty(group = "Behavior", description = "The effect of income similarity on pub choice", lower = "0.001", upper = "5.0", readOnly = false)
	public double pubChoiceIncomeSimilarityCoefficient;
	@EditableProperty(group = "Behavior", description = "The effect of interest similarity on pub choice", lower = "0.001", upper = "5.0", readOnly = false)
	public double pubChoiceInterestSimilarityCoefficient;
	@EditableProperty(group = "Init", description = "Maximum number of visitor logs for place profile", lower = "200", upper = "5000", readOnly = false)
	public int maxNumberOfVisitorLogs;
	@EditableProperty(group = "Init", description = "Minimum number of visitor logs required to create a place profile", lower = "10", upper = "200", readOnly = false)
	public int minNumberOfVisitorLogsRequiredForPlaceProfile;


	// social network visualization
	@Skip
	public boolean isFriendFamilyGraphVisible;
	@Skip
	public boolean isWorkGraphVisible;


	public WorldParameters() {
	}

	public WorldParameters(String fileName) throws IllegalArgumentException, IllegalAccessException, ConfigurationException {
		this();
		Parameters params = new Parameters();
		File propertiesFile = new File(fileName);

		CustomConversionHandler handler = new CustomConversionHandler();
		FileBasedConfigurationBuilder<FileBasedConfiguration> builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(
				PropertiesConfiguration.class)
						.configure(params.fileBased().setFile(propertiesFile).setConversionHandler(handler)
								.setListDelimiterHandler(new DefaultListDelimiterHandler(',')));
		Configuration conf = builder.getConfiguration();

		Field[] fields = WorldParameters.class.getDeclaredFields();
		int mod;
		int skipMod = Modifier.STATIC | Modifier.VOLATILE | Modifier.TRANSIENT | Modifier.FINAL;
		for (int i = 0; i < fields.length; i++) {
			mod = fields[i].getModifiers();
			if ((mod & skipMod) == 0 || fields[i].getName().equals("a")) {
				String key = fields[i].getName();
				if(!conf.containsKey(key))
					continue;
				Object value = conf.get((Class<?>) fields[i].getType(), key);

				fields[i].setAccessible(true);
				fields[i].set(this, value);
			}
		}
	}

	public void store(String fileName)
			throws IllegalArgumentException, IllegalAccessException, ConfigurationException, IOException {
		Parameters params = new Parameters();
		File propertiesFile = new File(fileName);
		if (!propertiesFile.exists())
			propertiesFile.createNewFile();

		FileBasedConfigurationBuilder<FileBasedConfiguration> builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(
				PropertiesConfiguration.class).configure(params.fileBased().setFile(propertiesFile));
		Configuration conf = builder.getConfiguration();

		Field[] fields = WorldParameters.class.getDeclaredFields();
		int mod;
		int skipMod = Modifier.STATIC | Modifier.VOLATILE | Modifier.TRANSIENT |  Modifier.FINAL;
		for (int i = 0; i < fields.length; i++) {
			mod = fields[i].getModifiers();
			if ((mod & skipMod) == 0) {
				String key = fields[i].getName();
				Object defaultValue = fields[i].get(this);
				if (defaultValue instanceof LocalDateTime) {
					defaultValue = defaultValue.toString();
				}

				conf.setProperty(key, defaultValue);
			}
		}
		builder.save();
	}

	protected void initializationWithDefaultValues() {
		seed = SEED;

		// simulation time parameters
		oneStepTime = ONE_STEP_TIME;
		initialSimulationTime = INITIAL_SIMULATION_TIME;
		warmupPeriodEndTime = WARMUP_PERIOD_END_TIME;
		timeStepUnit = SimulationTimeStepSetting.MinutePerStep;
		maxSimulationSteps = MAX_SIMULATIONSTEPS;

		// social network visualization
		isFriendFamilyGraphVisible = false;
		isWorkGraphVisible = false;

		// general model parameters
		numOfAgents = NUM_OF_AGENTS;
		numOfAgentInterests = NUM_OF_AGENT_INTERESTS;
		baseAgentAge = BASE_AGENT_AGE;
		additionalAgentAgeMin = ADDITIONAL_AGENT_AGE_MIN;
		additionalAgentAgeMax = ADDITIONAL_AGENT_AGE_MAX;
		baseRentRate = BASE_RENT_RATE;
		maps = DEFAULT_MAPS;

		maximumAllowedRentalSalaryRatio = MAXIMUM_ALLOWED_RENTAL_SALARY_RATIO;
		maxDaysToBeHomeless = MAX_DAYS_TO_BE_HOMELESS;
		maxDaysToBeStarving = MAX_DAYS_TO_BE_STARVING;
		minuteSpentAtRestaurant = MINUTE_SPENT_AT_RESTAURANT;
		preparationTimeInMinutes = PREPARATION_TIME_IN_MINUTES;
		showAgentInterestColor = SHOW_AGENT_INTEREST_COLOR;
		enablePrecomputingPaths = ENABLE_PRECOMPUTING_PATHS;
		agentWalkingSpeed = AGENT_WALKING_SPEED;

		// economics/money parameters
		mealCostAtHome = MEAL_COST_AT_HOME;
		workHoursPerDay = WORK_HOURS_PER_DAY;
		baseInitialBalance = BASE_INITIAL_BALANCE;
		targetedSavingAmountWithRespectToRent = TARGETED_SAVING_AMOUNT_WITH_RESPECT_TO_RENT;
		minimumSiteVisitLengthInMinutes = MINIMUM_SITE_VISIT_LENGTH_IN_MINUTES;
		maximumSiteVisitLengthInMinutes = MAXIMUM_SITE_VISIT_LENGTH_IN_MINUTES;

		// Network parameters
		focalClosureProbability = FOCAL_CLOSURE_PROBABILITY;
		cyclicClosureProbability = CYCLIC_CLOSURE_PROBABILITY;
		initialNetworkEdgeWeight = INITIAL_NETWORK_EDGE_WEIGHT;
		networkEdgeDecayFactor = NETWORK_EDGE_DECAY_FACTOR;
		networkEdgeWeightStrengtheningRate = NETWORK_EDGE_WEIGHT_STRENGTHENING_RATE;
		networkEdgeDeletionThreshold = NETWORK_EDGE_DELETION_THRESHOLD;
		useWorkNetwork = USE_WORK_NETWORK;
		maxNumOfFriends = MAX_NUM_OF_FRIENDS;
		maxLonelyDays = MAX_LONELY_DAYS;
		stableRelationshipPeriodInMin = STABLE_RELATIONSHIP_PERIOD_IN_MIN;

		numApartmentsPer1000 = NUM_APARTMENTS_PER_1000;
		numWorkplacesPer1000 = NUM_WORKPLACES_PER_1000;
		numPubsPer1000 = NUM_PUBS_PER_1000;
		numRestaurantsPer1000 = NUM_RESTAURANTS_PER_1000;
		resChargeLowerBound = RES_CHARGE_LOWER_BOUND;
		resChargeUpperBound = RES_CHARGE_UPPER_BOUND;

		pubChargeLowerBound = PUB_CHARGE_LOWER_BOUND;
		pubChargeUpperBound = PUB_CHARGE_UPPER_BOUND;

		// Environment
		appetiteLowerBound = APPETITE_LOWER_BOUND;
		appetiteUpperBound = APPETITE_UPPER_BOUND;
		attractivenessLowerBound = ATTRACTIVENESS_LOWER_BOUND;
		attractivenessUpperBound = ATTRACTIVENESS_UPPER_BOUND;
		apartmentRoomNumberLowerBound = APARTMENT_ROOM_NUMBER_LOWER_BOUND;
		apartmentRoomNumberUpperBound = APARTMENT_ROOM_NUMBER_UPPER_BOUND;
		numOfJobsAtWorkplaceLowerBound = NUMBER_OF_JOBS_AT_A_WORKPLACE_LOWER_BOUND;
		numOfJobsAtWorkplaceUpperBound = NUMBER_OF_JOBS_AT_A_WORKPLACE_UPPER_BOUND;
		schoolCapacityMean = SCHOOL_CAPACITY_MEAN;
		schoolCapacitySD = SCHOOL_CAPACITY_SD;
		siteCapcityMeam = SITE_CAPACITY_MEAN;
		siteCapacitySD = SITE_CAPACITY_SD;
		monthlySchoolCostRate = MONTHLY_SCHOOL_COST_RATE;
		minimumHourlyRate = MINIMUM_HOURLY_RATE;
		maximumHourlyRate = MAXIMUM_HOURLY_RATE;
		initialAdditionalBalanceLowerBound = INITIAL_ADDITIONAL_BALANCE_LOWER_BOUND;
		initialAdditionalBalanceUpperBound = INITIAL_ADDITIONAL_BALANCE_UPPER_BOUND;
		numOfSingleAgentsPer1000 = NUM_OF_SINGLE_AGENTS_PER_1000;
		numOfFamilyAgentsWithKidsPer1000 = NUM_OF_FAMILY_AGENTS_WITH_KIDS_PER_1000;

		// pub preference parameters
		numberOfNearestPubs = NUMBER_OF_NEAREST_PUBS;
		numberOfInterestsToConsider = NUMBER_OF_INTERESTS_TO_CONSIDER;
		maxGroupMeetingSize = MAX_GROUP_MEETING_SIZE;
		maxGroupSizeToJoinForAloneAgents = MAX_GROUP_SIZE_TO_JOIN_FOR_ALONE_AGENTS;
		pubChoiceExponentialDecayConstant = PUB_CHOICE_EXPONENTIAL_DECAY_CONSTANT;
		pubChoiceClosenessCoefficient = PUB_CHOICE_CLOSENESS_COEFFICIENT;
		pubChoiceAgeSimilarityCoefficient = PUB_CHOICE_AGE_SIMILARITY_COEFFICIENT;
		pubChoiceIncomeSimilarityCoefficient = PUB_CHOICE_INCOME_SIMILARITY_COEFFICIENT;
		pubChoiceInterestSimilarityCoefficient = PUB_CHOICE_INTEREST_SIMILARITY_COEFFICIENT;
		maxNumberOfVisitorLogs = MAX_NUMBER_OF_VISITOR_LOGS;
		minNumberOfVisitorLogsRequiredForPlaceProfile = MIN_NUMBER_OF_VISITOR_LOGS_REQUIRED_FOR_PLACE_PROFILE;

		// sociality
		numberOfDaysToConsiderForMeasuringSocialStatus = NUMBER_OF_DAYS_TO_CONSIDER_FOR_MEASURING_SOCIAL_STATUS;
		initialSocialStatusWeight = INITIAL_SOCIAL_STATUS_WEIGHT;
		socialStatusDecayFactor = SOCIAL_STATUS_DECAY_FACTOR;
		maxSocialStatusValue = MAX_SOCIAL_STATUS_VALUE;
		minSocialStatusValue = MIN_SOCIAL_STATUS_VALUE;
		socialStatusIncreaseValue = SOCIAL_STATUS_INCREASE_VALUE;
		socialStatusDecreaseValue = SOCIAL_STATUS_DECREASE_VALUE;
	}
}
