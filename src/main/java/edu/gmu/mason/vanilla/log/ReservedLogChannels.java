package edu.gmu.mason.vanilla.log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

import org.apache.logging.log4j.Level;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.ISODateTimeFormat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.gmu.mason.vanilla.AgentGeometry;
import edu.gmu.mason.vanilla.Person;
import edu.gmu.mason.vanilla.QuantitiesOfInterest;
import edu.gmu.mason.vanilla.WorldModel;
import edu.gmu.mason.vanilla.db.Cdf;
import edu.gmu.mason.vanilla.db.Column;
import edu.gmu.mason.vanilla.db.PredefinedTables;
import edu.gmu.mason.vanilla.environment.Apartment;
import edu.gmu.mason.vanilla.environment.Building;
import edu.gmu.mason.vanilla.environment.BuildingUnit;
import edu.gmu.mason.vanilla.environment.Census;
import edu.gmu.mason.vanilla.environment.CensusData;
import edu.gmu.mason.vanilla.environment.Classroom;
import edu.gmu.mason.vanilla.environment.Job;
import edu.gmu.mason.vanilla.environment.Pub;
import edu.gmu.mason.vanilla.environment.Restaurant;
import edu.gmu.mason.vanilla.environment.SpatialNetwork;
import edu.gmu.mason.vanilla.environment.Workplace;
import edu.gmu.mason.vanilla.log.EventList.Item;
import edu.gmu.mason.vanilla.utils.Exclusion;
import sim.util.geo.MasonGeometry;

/**
 * Configuration for reserved logs
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 *
 */
public class ReservedLogChannels implements Serializable {
	public static final String LOG_ROOT_DIRECTORY_PROPERTY_NAME = "log.rootDirectory";
	public static final String TEST_PROPERTY_NAME = "simulation.test";
	private static final long serialVersionUID = 6956761501760484884L;
	protected static final String DEFAULT_OUTPUT_TYPE = "File";
	protected static final String DEFAULT_SUFFIX_TYPE = ".tsv";
	protected static final String DEFAULT_ROOT_DIRECTORY = "./";
	public static final String DEFAULT_DIRECTORY = "logs/";
	
	protected static final ConcurrentMap<Level, Setting> RESERVED_LEVELS = init();
	
	protected static final double LOGGING_PERIOD = 1.0;
	protected static final int STEPS_PER_MONTH = 8640;
	protected static final int STEPS_PER_DAY = 288;
	
	// census data
	protected Census census;

	protected transient Gson characteristicsGson;
	protected transient Gson stateGson;
	protected transient Gson buildingGson;
	protected transient Gson buildingStateGson;
	protected transient Gson buildingUnitGson;
	protected transient Gson buildingUnitStateGson;
	protected transient Gson eventGson;
	// variables for log
	protected GsonFormatter characteristicsSchema;
	protected GsonFormatter characteristicsData;
	protected GsonFormatter stateSchema;
	protected GsonFormatter stateValue;
	protected GsonFormatter buildingUnitSchema;
	protected GsonFormatter buildingUnitData;
	protected GsonFormatter buildingUnitStateSchema;
	protected GsonFormatter buildingUnitStateData;
	protected GsonFormatter eventFormatter;
	protected PlainTextFormatter textFormatter;
	
	protected Collection<EventList> eventMovingHome;
	protected Collection<EventList> eventChangingJob;
	
	protected WorldModel model;
	
	private static ConcurrentMap<Level, Setting> init()
	{
		ConcurrentMap<Level, Setting> instance = new ConcurrentHashMap<>();
		// ADD ALL
		// system level logging
		instance.putIfAbsent(Level.getLevel("INFO"), new Setting("pattenrs_of_life","info level logging", "RollingFile", DEFAULT_DIRECTORY, ".log"));
		// If you want flexibility, add -Dsimulation.test=flexibility
		String whatTest = System.getProperty(TEST_PROPERTY_NAME);
		if (whatTest != null && whatTest.equals("flexibility")) {
			instance.putIfAbsent(Level.getLevel("MODEL1"), new Setting("InstanceVariableTable", "InstanceVariableTable", DEFAULT_OUTPUT_TYPE, "../../"));
			instance.putIfAbsent(Level.getLevel("STAT7"), new Setting("SummaryStatisticsDataTable", "SummaryStatisticsDataTable", DEFAULT_OUTPUT_TYPE, ""));

			return instance;
		}
		if (whatTest != null && whatTest.equals("qoi")) {
			instance.putIfAbsent(Level.getLevel("STAT1"), new Setting("QOI1Table","QOI1", DEFAULT_OUTPUT_TYPE, "qois/"));
			instance.putIfAbsent(Level.getLevel("STAT2"), new Setting("QOI2Table","QOI2", DEFAULT_OUTPUT_TYPE, "qois/"));
			instance.putIfAbsent(Level.getLevel("STAT3"), new Setting("QOI3Table","QOI3", DEFAULT_OUTPUT_TYPE, "qois/"));
			instance.putIfAbsent(Level.getLevel("STAT4"), new Setting("QOI4Table","QOI4", DEFAULT_OUTPUT_TYPE, "qois/"));
			instance.putIfAbsent(Level.getLevel("STAT5"), new Setting("QOI5Table","QOI5", DEFAULT_OUTPUT_TYPE, "qois/"));
			instance.putIfAbsent(Level.getLevel("STAT6"), new Setting("QOI6Table","QOI6", DEFAULT_OUTPUT_TYPE, "qois/"));
			return instance;
		}
		// If you want to generate all data, add -Dsimulation.test=all
		if (whatTest != null && whatTest.equals("all")) {
			instance.putIfAbsent(Level.getLevel("ENV1"), new Setting("BuildingTable","Buildings"));
			instance.putIfAbsent(Level.getLevel("ENV2"), new Setting("ApartmentTable","Apartments"));
			instance.putIfAbsent(Level.getLevel("ENV3"), new Setting("WorkplaceTable","Workplaces"));
			instance.putIfAbsent(Level.getLevel("ENV4"), new Setting("RestaurantTable","Restaurants"));
			instance.putIfAbsent(Level.getLevel("ENV5"), new Setting("PubTable","Pubs"));
			instance.putIfAbsent(Level.getLevel("ENV6"), new Setting("ClassroomTable","Classrooms"));
			instance.putIfAbsent(Level.getLevel("ENV7"), new Setting("OpenPubState","OpenPubState","RollingFile"));
			instance.putIfAbsent(Level.getLevel("ENV8"), new Setting("OpenRestaurantState","OpenRestaurantState","RollingFile"));
			
			instance.putIfAbsent(Level.getLevel("MODEL"), new Setting("InstanceDataTable","InstanceData"));
			
			instance.putIfAbsent(Level.getLevel("AGENT"), new Setting("AgentStateTable","AgentStates","RollingFile"));
			instance.putIfAbsent(Level.getLevel("AGENT1"), new Setting("AgentCharacteristicsTable","AgentCharacteristics"));
			instance.putIfAbsent(Level.getLevel("AGENT3"), new Setting("JobTable","Jobs"));
			instance.putIfAbsent(Level.getLevel("AGENT4"), new Setting("RelationshipTable","FriendRelationship",DEFAULT_OUTPUT_TYPE, "qois/"));
			
			instance.putIfAbsent(Level.getLevel("STAT"), new Setting("CensusTable","CensusData"));
			
			// Append all event journals that do not follow general logging mechanism.
			// You can use from EVT1 to EVNT49
			instance.putIfAbsent(Level.getLevel("EVT1"), new Setting("TravelJournal","TravelingJournal","RollingFile",DEFAULT_DIRECTORY,".csv"));
			instance.putIfAbsent(Level.getLevel("EVT2"), new Setting("FinancialJournal","FinancialJournal","RollingFile",DEFAULT_DIRECTORY,".csv"));
			instance.putIfAbsent(Level.getLevel("EVT3"), new Setting("FinancialAttributesJournal","FinancialAttributesJournal","RollingFile",DEFAULT_DIRECTORY,".csv"));
			instance.putIfAbsent(Level.getLevel("EVT4"), new Setting("InterventionJournal","InterventionJournal","RollingFile",DEFAULT_DIRECTORY,".csv"));
			instance.putIfAbsent(Level.getLevel("EVT5"), new Setting("VistorProfile","VistorProfile","RollingFile"));
			instance.putIfAbsent(Level.getLevel("EVT6"), new Setting("Trajectory","Trajectory","RollingFile"));
			
			instance.putIfAbsent(Level.getLevel("EVT10"), new Setting("MovingJournal","MovingJournal"));
			instance.putIfAbsent(Level.getLevel("EVT11"), new Setting("JobChangeJournal","JobChangeJournal"));
			
			// return instance;
			
			
		}
		instance.putIfAbsent(Level.getLevel("STAT1"), new Setting("QOI1Table","QOI1", DEFAULT_OUTPUT_TYPE, "qois/"));
		instance.putIfAbsent(Level.getLevel("STAT2"), new Setting("QOI2Table","QOI2", DEFAULT_OUTPUT_TYPE, "qois/"));
		instance.putIfAbsent(Level.getLevel("STAT3"), new Setting("QOI3Table","QOI3", DEFAULT_OUTPUT_TYPE, "qois/"));
		instance.putIfAbsent(Level.getLevel("STAT4"), new Setting("QOI4Table","QOI4", DEFAULT_OUTPUT_TYPE, "qois/"));
		instance.putIfAbsent(Level.getLevel("STAT5"), new Setting("QOI5Table","QOI5", DEFAULT_OUTPUT_TYPE, "qois/"));
		instance.putIfAbsent(Level.getLevel("STAT6"), new Setting("QOI6Table","QOI6", DEFAULT_OUTPUT_TYPE, "qois/"));
		instance.putIfAbsent(Level.getLevel("AGENT5"), new Setting("Checkin", "Checkin", "RollingFile"));
		instance.putIfAbsent(Level.getLevel("AGENT6"), new Setting("SocialNetwork", "SocialNetwork", "RollingFile"));
		return instance;
	}
	
	protected ReservedLogChannels(ReservedLogChannels upgrade) {
		census = upgrade.census;

		characteristicsGson = upgrade.characteristicsGson;
		stateGson = upgrade.stateGson;
		buildingGson = upgrade.buildingGson;
		buildingStateGson = upgrade.buildingStateGson;
		buildingUnitGson = upgrade.buildingUnitGson;
		buildingUnitStateGson = upgrade.buildingUnitStateGson;
		
		characteristicsSchema = upgrade.characteristicsSchema;
		characteristicsData = upgrade.characteristicsData;
		stateSchema = upgrade.stateSchema;
		stateValue = upgrade.stateValue;
		buildingUnitSchema = upgrade.buildingUnitSchema;
		buildingUnitData = upgrade.buildingUnitData;
		buildingUnitStateSchema = upgrade.buildingUnitStateSchema;
		buildingUnitStateData = upgrade.buildingUnitStateData;
		
		model = upgrade.model;
	}
	
	public ReservedLogChannels(WorldModel model) {
		this.model = model;
		census = new Census(model);
	}
	
	protected long getSteps() {
		return model.schedule.getSteps();
	}
	

	public void loggingSchedule() {
		Object[][] scheduleCandidates = getCandidates();
		for (int i = 0; i < scheduleCandidates.length; i++) {
			Level lv = Level.getLevel((String)scheduleCandidates[i][0]);
			if(RESERVED_LEVELS.containsKey(lv)) {
				model.addLogSchedule((LogSchedule)scheduleCandidates[i][1]);
			}
		}
	}
	
	protected Object[][] getCandidates() {
		CensusData censusData = new CensusData();
		
		Cdf cdf = new Cdf();
		
		OutputFormatter instanceVariableSchema = new CdfSchemaFormatter();
		CdfMapper mapper = new CdfMapper(cdf.Instance.InstanceVariableTable);

		Column col = new Column();
		col.index = 0;
		col.name = "step";
		mapper.addMap(cdf.Instance.InstanceVariableTable.Timestep.name, col,
				new SupplierExtractor((Supplier & Serializable)() -> getSteps()));
		try {
			int i = 0;
			Map<String, ReflectionValueExtractor> functions = CdfMapperBuilder.functionBuilder(characteristicsGson,
					model.params);
			for (Entry<String, ReflectionValueExtractor> entry : functions.entrySet()) {
				if(entry.getKey().equals("a"))
					continue;
				col = new Column();
				col.index = i++;
				col.name = "Name";
				mapper.addMap(cdf.Instance.InstanceVariableTable.Name.name, col,
						new SupplierExtractor((Supplier & Serializable)() -> entry.getKey()));
				col = new Column();
				col.index = i++;
				col.name = entry.getKey();
				mapper.addMap(cdf.Instance.InstanceVariableTable.Value.name, col, entry.getValue());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		OutputFormatter instanceVariableData = new CdfValueFormatter(mapper);
		
		
		OutputFormatter runDataSchema = new CdfSchemaFormatter();
		// AVERAGE_SOCIAL_NETWORK_DEGREE
		mapper = new CdfMapper(cdf.Instance.Run.RunDataTable);
		col = new Column();
		col.index = 0;
		col.name = "step";
		mapper.addMap(cdf.Instance.Run.RunDataTable.Timestep.name, col,
				new SupplierExtractor((Supplier & Serializable)() -> getSteps()));
		
		col = new Column();
		col.index = 1;
		col.name = "VariableName";
		mapper.addMap(cdf.Instance.Run.RunDataTable.VariableName.name, col,
				new SupplierExtractor((Supplier & Serializable)() -> QuantitiesOfInterest.AVERAGE_SOCIAL_NETWORK_DEGREE));
		
		col = new Column();
		col.index = 2;
		col.name = "Value";
		mapper.addMap(cdf.Instance.Run.RunDataTable.Value.name, col,
				new SupplierExtractor((Supplier & Serializable)() -> model.getQuantitiesOfInterest().getCollectedValues(QuantitiesOfInterest.AVERAGE_SOCIAL_NETWORK_DEGREE).get(getSteps())));
		OutputFormatter runData1 = new CdfValueFormatter(mapper);

		// AVERAGE_BALANCE
		mapper = new CdfMapper(cdf.Instance.Run.RunDataTable);
		col = new Column();
		col.index = 0;
		col.name = "step";
		mapper.addMap(cdf.Instance.Run.RunDataTable.Timestep.name, col,
				new SupplierExtractor((Supplier & Serializable)() -> getSteps()));
		
		col = new Column();
		col.index = 1;
		col.name = "VariableName";
		mapper.addMap(cdf.Instance.Run.RunDataTable.VariableName.name, col,
				new SupplierExtractor((Supplier & Serializable)() -> QuantitiesOfInterest.AVERAGE_BALANCE));
		
		col = new Column();
		col.index = 2;
		col.name = "Value";
		mapper.addMap(cdf.Instance.Run.RunDataTable.Value.name, col,
				new SupplierExtractor((Supplier & Serializable)() -> model.getQuantitiesOfInterest().getCollectedValues(QuantitiesOfInterest.AVERAGE_BALANCE).get(getSteps())));
		OutputFormatter runData2 = new CdfValueFormatter(mapper);
		
		// PERCENTAGE_OF_UNHAPPY_AGENTS
		mapper = new CdfMapper(cdf.Instance.Run.RunDataTable);
		col = new Column();
		col.index = 0;
		col.name = "step";
		mapper.addMap(cdf.Instance.Run.RunDataTable.Timestep.name, col,
				new SupplierExtractor((Supplier & Serializable)() -> getSteps()));
		
		col = new Column();
		col.index = 1;
		col.name = "VariableName";
		mapper.addMap(cdf.Instance.Run.RunDataTable.VariableName.name, col,
				new SupplierExtractor((Supplier & Serializable)() -> QuantitiesOfInterest.PERCENTAGE_OF_UNHAPPY_AGENTS));
		
		col = new Column();
		col.index = 2;
		col.name = "Value";
		mapper.addMap(cdf.Instance.Run.RunDataTable.Value.name, col,
				new SupplierExtractor((Supplier & Serializable)() -> model.getQuantitiesOfInterest().getCollectedValues(QuantitiesOfInterest.PERCENTAGE_OF_UNHAPPY_AGENTS).get(getSteps())));
		OutputFormatter runData3 = new CdfValueFormatter(mapper);
		
		// PUB_VISITS_PER_AGENT
		mapper = new CdfMapper(cdf.Instance.Run.RunDataTable);
		col = new Column();
		col.index = 0;
		col.name = "step";
		mapper.addMap(cdf.Instance.Run.RunDataTable.Timestep.name, col,
				new SupplierExtractor((Supplier & Serializable)() -> getSteps()));
		
		col = new Column();
		col.index = 1;
		col.name = "VariableName";
		mapper.addMap(cdf.Instance.Run.RunDataTable.VariableName.name, col,
				new SupplierExtractor((Supplier & Serializable)() -> QuantitiesOfInterest.PUB_VISITS_PER_AGENT));
		
		col = new Column();
		col.index = 2;
		col.name = "Value";
		mapper.addMap(cdf.Instance.Run.RunDataTable.Value.name, col,
				new SupplierExtractor((Supplier & Serializable)() -> model.getQuantitiesOfInterest().getCollectedValues(QuantitiesOfInterest.PUB_VISITS_PER_AGENT).get(getSteps())));
		OutputFormatter runData4 = new CdfValueFormatter(mapper);
		
		// NUM_OF_SOCIAL_INTERACTIONS
		mapper = new CdfMapper(cdf.Instance.Run.RunDataTable);
		col = new Column();
		col.index = 0;
		col.name = "step";
		mapper.addMap(cdf.Instance.Run.RunDataTable.Timestep.name, col,
				new SupplierExtractor((Supplier & Serializable)() -> getSteps()));
		
		col = new Column();
		col.index = 1;
		col.name = "VariableName";
		mapper.addMap(cdf.Instance.Run.RunDataTable.VariableName.name, col,
				new SupplierExtractor((Supplier & Serializable)() -> QuantitiesOfInterest.NUM_OF_SOCIAL_INTERACTIONS));
		
		col = new Column();
		col.index = 2;
		col.name = "Value";
		mapper.addMap(cdf.Instance.Run.RunDataTable.Value.name, col,
				new SupplierExtractor((Supplier & Serializable)() -> model.getQuantitiesOfInterest().getCollectedValues(QuantitiesOfInterest.NUM_OF_SOCIAL_INTERACTIONS).get(getSteps())));
		OutputFormatter runData5 = new CdfValueFormatter(mapper);

		// Social network
		OutputFormatter runData6 = new CdfValueFormatter(mapper);
		
		PredefinedTables schema = new PredefinedTables();
		OutputFormatter checkinTableSchema = new TableSchemaFormatter();
		TableMapper tableMapper = new TableMapper(schema.CheckinDataset.CheckinTable);

		col = new Column();
		col.index = 0;
		col.name = "UserId";
		tableMapper.addMap(schema.CheckinDataset.CheckinTable.UserId.name, col, (o)->((Person)o).getAgentId());

		col = new Column();
		col.index = 1;
		col.name = "CheckinTime";
		tableMapper.addMap(schema.CheckinDataset.CheckinTable.CheckinTime.name, col,
				new SupplierExtractor((Supplier & Serializable)() -> model.getSimulationTime().toString(ISODateTimeFormat.dateTimeNoMillis())));

		col = new Column();
		col.index = 2;
		col.name = "VenueId";
		tableMapper.addMap(schema.CheckinDataset.CheckinTable.VenueId.name, col, (o) -> {
			if (((Person) o).getCurrentUnit() != null)
				return ((Person) o).getCurrentUnit().getId();
			return null;
		});

		col = new Column();
		col.index = 3;
		col.name = "VenueType";
		tableMapper.addMap(schema.CheckinDataset.CheckinTable.VenueType.name, col, (o) -> {
			if (((Person) o).getCurrentUnit() != null)
				return ((Person) o).getCurrentUnit().getClass().getSimpleName();
			return null;
		});

		col = new Column();
		col.index = 4;
		col.name = "X";
		tableMapper.addMap(schema.CheckinDataset.CheckinTable.X.name, col, (o)->((Person)o).getLocation().getGeometry().getCoordinate().x);

		col = new Column();
		col.index = 5;
		col.name = "Y";
		tableMapper.addMap(schema.CheckinDataset.CheckinTable.Y.name, col, (o)->((Person)o).getLocation().getGeometry().getCoordinate().y);

		OutputFormatter checkinData = new TableValueFormatter(tableMapper);
		
		OutputFormatter socialNetworkData = new TableFlatFormatterForRelation((Supplier<LocalDateTime> & Serializable)() -> model.getSimulationTime());
		
				
		// Keep lambda function safe from serialization
		Object[][] scheduleCandidates = {
				{"ENV1", new LogSchedule(0, "ENV1", (Supplier & Serializable) () -> model.getAllBuildings().get(0), new GsonCsvSchemaFormatter(buildingGson), 0)},
				{"ENV1", new IterativeLogSchedule(0, "ENV1", (Supplier<Collection> & Serializable) () -> model.getAllBuildings(), new GsonCsvValueFormatter(buildingGson), 1)},
				{"ENV2", new LogSchedule(0, "ENV2", (Supplier & Serializable) () -> model.getAllApartments().get(0), buildingUnitSchema, 0)},
				{"ENV2", new IterativeLogSchedule(0, "ENV2", (Supplier<Collection> & Serializable) () -> model.getAllApartments(), buildingUnitData, 1)},
				{"ENV3", new LogSchedule(0, "ENV3", (Supplier & Serializable) () -> model.getAllWorkplaces().get(0), buildingUnitSchema, 0)},
				{"ENV3", new IterativeLogSchedule(0, "ENV3", (Supplier<Collection> & Serializable) () -> model.getAllWorkplaces(), buildingUnitData, 1)},
				{"ENV4", new LogSchedule(0, "ENV4", (Supplier & Serializable) () -> model.getAllRestaurants().get(0), buildingUnitSchema, 0)},
				{"ENV4", new IterativeLogSchedule(0, "ENV4", (Supplier<Collection> & Serializable) () -> model.getAllRestaurants(), buildingUnitData, 1)},
				{"ENV5", new LogSchedule(0, "ENV5", (Supplier & Serializable) () -> model.getAllPubs().get(0), buildingUnitSchema, 0)},
				{"ENV5", new IterativeLogSchedule(0, "ENV5", (Supplier<Collection> & Serializable) () -> model.getAllPubs(), buildingUnitData, 1)},
				{"ENV6", new LogSchedule(0, "ENV6", (Supplier & Serializable) () -> model.getAllClassrooms().get(0), buildingUnitSchema, 0)},
				{"ENV6", new IterativeLogSchedule(0, "ENV6", (Supplier<Collection> & Serializable) () -> model.getAllClassrooms(), buildingUnitData, 1)},
				{"ENV7", new LogSchedule(0, "ENV7", (Supplier & Serializable) () -> model.getUsablePubs().get(0), buildingUnitStateSchema, 0)},
				{"ENV7", new IterativeLogSchedule(0, 1, "ENV7", (Supplier<Collection> & Serializable) () -> model.getUsablePubs(), buildingUnitStateData, 1)},
				{"ENV8", new LogSchedule(0, "ENV8", (Supplier & Serializable) () -> model.getUsableRestaurants().get(0), buildingUnitStateSchema, 0)},
				{"ENV8", new IterativeLogSchedule(0, 1, "ENV8", (Supplier<Collection> & Serializable) () -> model.getUsableRestaurants(), buildingUnitStateData, 1)},
				{"MODEL", new LogSchedule(0, "MODEL", (Supplier & Serializable) () -> model.params, characteristicsSchema, 0)},
				{"MODEL", new LogSchedule(0, "MODEL", (Supplier & Serializable) () -> model.params, characteristicsData, 1)},
				{"MODEL1", new LogSchedule(0, "MODEL1", (Supplier & Serializable) () -> cdf.Instance.InstanceVariableTable, instanceVariableSchema, 0)},
				{"MODEL1", new LogSchedule(0, "MODEL1", (Supplier & Serializable) () -> model.params, instanceVariableData, 1)},
				{"AGENT1", new LogSchedule(0, "AGENT1", (Supplier & Serializable) () -> model.getAgents().get(0), characteristicsSchema, 0)},
				{"AGENT1", new IterativeLogSchedule(0, "AGENT1", (Supplier<Collection> & Serializable) () -> model.getAgents(), characteristicsData, 1)},
				{"AGENT", new LogSchedule(0, "AGENT", (Supplier & Serializable) () -> model.getAgents().get(0), stateSchema, 0)},
				{"AGENT", new IterativeLogSchedule(0, 1, "AGENT", (Supplier<Collection> & Serializable) () -> model.getAgents(), stateValue, 1)},
				{"AGENT3", new LogSchedule(0, "AGENT3", (Supplier & Serializable) () -> model.getAllJobs().get(0), characteristicsSchema, 0)},
				{"AGENT3", new IterativeLogSchedule(0, "AGENT3", (Supplier<Collection> & Serializable) () -> model.getAllJobs(), characteristicsData, 1)},
				// RelationshipDataTable // every one month
				{"AGENT4", new LogSchedule(0, "AGENT4", (Supplier & Serializable) () -> cdf.Instance.Run.RelationshipDataTable, runDataSchema, 0)},
				{"AGENT4", new LogSchedule(0, STEPS_PER_MONTH, "AGENT4", (Supplier & Serializable) () -> model.getVisualFriendFamilyGraph(), runData6, 1)},
				{"STAT", new LogSchedule(0, "STAT", (Supplier & Serializable) () -> censusData, stateSchema, 0)},
				{"STAT", new LogSchedule(0, STEPS_PER_MONTH, "STAT", (Supplier & Serializable) () -> "", new CensusDataCollector(), 1)},
				{"STAT", new IterativeLogSchedule(0, STEPS_PER_MONTH, "STAT", (Supplier<Collection> & Serializable) () -> census.getNeighborhoodCensus(), stateValue, 2)},
				{"STAT1", new LogSchedule(0, "STAT1", (Supplier & Serializable) () -> cdf.Instance.Run.RunDataTable, runDataSchema, 0)},
				{"STAT1", new LogSchedule(0, STEPS_PER_DAY, "STAT1", (Supplier & Serializable) () -> model.getQuantitiesOfInterest(), runData1, 1)},
				{"STAT2", new LogSchedule(0, "STAT2", (Supplier & Serializable) () -> cdf.Instance.Run.RunDataTable, runDataSchema, 0)},
				{"STAT2", new LogSchedule(0, STEPS_PER_DAY, "STAT2", (Supplier & Serializable) () -> model.getQuantitiesOfInterest(), runData2, 1)},
				{"STAT3", new LogSchedule(0, "STAT3", (Supplier & Serializable) () -> cdf.Instance.Run.RunDataTable, runDataSchema, 0)},
				{"STAT3", new LogSchedule(0, STEPS_PER_DAY, "STAT3", (Supplier & Serializable) () -> model.getQuantitiesOfInterest(), runData3, 1)},
				{"STAT4", new LogSchedule(0, "STAT4", (Supplier & Serializable) () -> cdf.Instance.Run.RunDataTable, runDataSchema, 0)},
				{"STAT4", new LogSchedule(0, STEPS_PER_DAY, "STAT4", (Supplier & Serializable) () -> model.getQuantitiesOfInterest(), runData4, 1)},
				{"STAT5", new LogSchedule(0, "STAT5", (Supplier & Serializable) () -> cdf.Instance.Run.RunDataTable, runDataSchema, 0)},
				{"STAT5", new LogSchedule(0, STEPS_PER_DAY, "STAT5", (Supplier & Serializable) () -> model.getQuantitiesOfInterest(), runData5, 1)},
				// for test
				{"STAT6", new LogSchedule(0, "STAT6", (Supplier & Serializable) () -> cdf.Instance.Run.RunDataTable, runDataSchema, 0)},
				{"STAT6", new LogSchedule(0, STEPS_PER_DAY, "STAT6", (Supplier & Serializable) () -> model.getQuantitiesOfInterest(), runData1, 1)},
				{"STAT6", new LogSchedule(0, STEPS_PER_DAY, "STAT6", (Supplier & Serializable) () -> model.getQuantitiesOfInterest(), runData2, 1)},
				{"STAT6", new LogSchedule(0, STEPS_PER_DAY, "STAT6", (Supplier & Serializable) () -> model.getQuantitiesOfInterest(), runData3, 1)},
				{"STAT6", new LogSchedule(0, STEPS_PER_DAY, "STAT6", (Supplier & Serializable) () -> model.getQuantitiesOfInterest(), runData4, 1)},
				{"STAT6", new LogSchedule(0, STEPS_PER_DAY, "STAT6", (Supplier & Serializable) () -> model.getQuantitiesOfInterest(), runData5, 1)},
				// EVENT Journal
				{"EVT10", new LogSchedule(0, "EVT10", (Supplier & Serializable) () -> "step\tagentId\t[currentShelter,neighborhood,classroom]", textFormatter, 0)},
				{"EVT10", new IterativeEventLogSchedule(0, 1, "EVT10", (Supplier<Collection<EventList>> & Serializable) () -> eventMovingHome, eventFormatter, 1)},
				{"EVT11", new LogSchedule(0, "EVT11", (Supplier & Serializable) () -> "step\tagentId\t[job]", textFormatter, 0)},
				{"EVT11", new IterativeEventLogSchedule(0, 1, "EVT11", (Supplier<Collection<EventList>> & Serializable) () -> eventChangingJob, eventFormatter, 1)},
				// For checkin
				{"AGENT5", new LogSchedule(0, "AGENT5", (Supplier & Serializable) () -> schema.CheckinDataset.CheckinTable, checkinTableSchema, 0)},
				{"AGENT5", new IterativeLogSchedule(1, 1, "AGENT5", (Supplier & Serializable) () -> model.getAgentsCheckin(), checkinData, 1)},
				{"AGENT6", new LogSchedule(0, "AGENT6", (Supplier & Serializable) () -> "time\tfrom\tto", textFormatter, 0)},
				{"AGENT6", new LogSchedule(0, STEPS_PER_DAY, "AGENT6", (Supplier & Serializable) () -> model.getVisualFriendFamilyGraph(), socialNetworkData, 1)},
		};
		
		return scheduleCandidates;
	}
	
	private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
		in.defaultReadObject();
		loggingSetup();
	}
	
	public void loggingSetup() {
		Exclusion exclusion = new Exclusion(Building.class, WorldModel.class);
		exclusion.addSkipField(Skip.class);
		exclusion.addSkipField(Characteristics.class);
		stateGson = new GsonBuilder()
				.setExclusionStrategies(exclusion)
				.serializeSpecialFloatingPointValues()
				.serializeNulls()
				.excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT, Modifier.VOLATILE)
				.registerTypeAdapter(AgentGeometry.class, new MasonGeometryTypeAdapter())
				.registerTypeAdapter(MasonGeometry.class, new MasonGeometryTypeAdapter())
				.registerTypeAdapter(Apartment.class, new ReferenceTypeAdapter())
				.registerTypeAdapter(Classroom.class, new ReferenceTypeAdapter())
				.registerTypeAdapter(Pub.class, new ReferenceTypeAdapter())
				.registerTypeAdapter(Restaurant.class, new ReferenceTypeAdapter())
				.registerTypeAdapter(Workplace.class, new ReferenceTypeAdapter())
				.registerTypeAdapter(DateTime.class, new DateTimeTypeAdapter())
				.registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter())
				.registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
				.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
				.create();
		
		exclusion = new Exclusion(Building.class, WorldModel.class);
		exclusion.addSkipField(Skip.class);
		exclusion.addSkipField(State.class);
		characteristicsGson = new GsonBuilder()
				.setExclusionStrategies(exclusion)
				.serializeSpecialFloatingPointValues()
				.serializeNulls()
				.excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT, Modifier.VOLATILE)
				.registerTypeAdapter(AgentGeometry.class, new MasonGeometryTypeAdapter())
				.registerTypeAdapter(MasonGeometry.class, new MasonGeometryTypeAdapter())
				.registerTypeAdapter(Apartment.class, new ReferenceTypeAdapter())
				.registerTypeAdapter(Classroom.class, new ReferenceTypeAdapter())
				.registerTypeAdapter(Pub.class, new ReferenceTypeAdapter())
				.registerTypeAdapter(Restaurant.class, new ReferenceTypeAdapter())
				.registerTypeAdapter(Workplace.class, new ReferenceTypeAdapter())
				.registerTypeAdapter(DateTime.class, new DateTimeTypeAdapter())
				.registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter())
				.registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
				.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
				.create();
		
		exclusion = new Exclusion(SpatialNetwork.class, WorldModel.class);
		exclusion.addSkipField(Skip.class);
		exclusion.addSkipField(State.class);
		buildingGson = new GsonBuilder()
				.setExclusionStrategies(exclusion)
				.serializeSpecialFloatingPointValues()
				.excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT, Modifier.VOLATILE)
				.registerTypeAdapter(AgentGeometry.class, new MasonGeometryTypeAdapter())
				.registerTypeAdapter(MasonGeometry.class, new MasonGeometryTypeAdapter())
				.registerTypeAdapter(BuildingUnit.class, new ReferenceTypeAdapter())
				.registerTypeAdapter(Apartment.class, new ReferenceTypeAdapter())
				.registerTypeAdapter(Classroom.class, new ReferenceTypeAdapter())
				.registerTypeAdapter(Pub.class, new ReferenceTypeAdapter())
				.registerTypeAdapter(Restaurant.class, new ReferenceTypeAdapter())
				.registerTypeAdapter(Workplace.class, new ReferenceTypeAdapter())
				.registerTypeAdapter(DateTime.class, new DateTimeTypeAdapter())
				.registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter())
				.registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
				.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
				.create();
		
		exclusion = new Exclusion(SpatialNetwork.class, WorldModel.class);
		exclusion.addSkipField(Skip.class);
		exclusion.addSkipField(Characteristics.class);
		buildingStateGson = new GsonBuilder()
				.setExclusionStrategies(exclusion)
				.serializeSpecialFloatingPointValues()
				.excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT, Modifier.VOLATILE)
				.registerTypeAdapter(AgentGeometry.class, new MasonGeometryTypeAdapter())
				.registerTypeAdapter(MasonGeometry.class, new MasonGeometryTypeAdapter())
				.registerTypeAdapter(BuildingUnit.class, new ReferenceTypeAdapter())
				.registerTypeAdapter(Apartment.class, new ReferenceTypeAdapter())
				.registerTypeAdapter(Classroom.class, new ReferenceTypeAdapter())
				.registerTypeAdapter(Pub.class, new ReferenceTypeAdapter())
				.registerTypeAdapter(Restaurant.class, new ReferenceTypeAdapter())
				.registerTypeAdapter(Workplace.class, new ReferenceTypeAdapter())
				.registerTypeAdapter(DateTime.class, new DateTimeTypeAdapter())
				.registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter())
				.registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
				.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
				.create();
		
		exclusion = new Exclusion(SpatialNetwork.class, WorldModel.class);
		exclusion.addSkipField(Skip.class);
		exclusion.addSkipField(State.class);
		buildingUnitGson = new GsonBuilder()
				.setExclusionStrategies(exclusion)
				.serializeSpecialFloatingPointValues()
				.excludeFieldsWithModifiers(Modifier.STATIC, Modifier.VOLATILE)
				.registerTypeAdapter(AgentGeometry.class, new MasonGeometryTypeAdapter())
				.registerTypeAdapter(MasonGeometry.class, new MasonGeometryTypeAdapter())
				.registerTypeAdapter(Building.class, new ReferenceTypeAdapter())
				.registerTypeAdapter(DateTime.class, new DateTimeTypeAdapter())
				.registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter())
				.registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
				.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
				.create();
		
		exclusion = new Exclusion(SpatialNetwork.class, WorldModel.class);
		exclusion.addSkipField(Skip.class);
		exclusion.addSkipField(Characteristics.class);
		buildingUnitStateGson = new GsonBuilder()
				.setExclusionStrategies(exclusion)
				.serializeSpecialFloatingPointValues()
				.excludeFieldsWithModifiers(Modifier.STATIC, Modifier.VOLATILE)
				.registerTypeAdapter(AgentGeometry.class, new MasonGeometryTypeAdapter())
				.registerTypeAdapter(MasonGeometry.class, new MasonGeometryTypeAdapter())
				.registerTypeAdapter(Building.class, new ReferenceTypeAdapter())
				.registerTypeAdapter(DateTime.class, new DateTimeTypeAdapter())
				.registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter())
				.registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
				.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
				.create();
		
		if (eventMovingHome == null) {
			eventMovingHome = new ArrayList<>();
			// step agentId [currentShelter,neighborhoodId,classroomId]
			for (Person p : model.getAgents()) {
				EventList eventList = new EventList(p.getAgentId());
				eventList.enableIndividualUpdateTime(false);
				eventList.add((Supplier & Serializable) () -> {
					BuildingUnit unit = model.getAgent(p.getAgentId()).getShelter();
					if (unit != null)
						return unit.getId();
					return null;
				});
				eventList.add((Supplier & Serializable) () -> model.getAgent(p.getAgentId()).getNeighborhoodId());
				eventList.add((Supplier & Serializable) () -> {
					BuildingUnit unit = model.getAgent(p.getAgentId()).getFamily().getClassroom();
					if (unit != null)
						return unit.getId();
					return null;
				});
				eventMovingHome.add(eventList);
			}
		}

		if (eventChangingJob == null) {
			eventChangingJob = new ArrayList<>();
			// step agentId [jobId]
			for (Person p : model.getAgents()) {
				EventList eventList = new EventList(p.getAgentId());
				eventList.enableIndividualUpdateTime(false);
				eventList.add((Supplier & Serializable) () -> {
					Job job = model.getAgent(p.getAgentId()).getJob();
					if (job != null)
						return job.getId();
					return null;
				});
				eventChangingJob.add(eventList);
			}
		}
		
		exclusion = new Exclusion(Building.class, WorldModel.class);
		exclusion.addSkipField(Skip.class);
		exclusion.addSkipField(Characteristics.class);
		eventGson = new GsonBuilder()
				.setExclusionStrategies(exclusion)
				.serializeSpecialFloatingPointValues()
				.serializeNulls()
				.excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT, Modifier.VOLATILE)
				.registerTypeAdapter(AgentGeometry.class, new MasonGeometryTypeAdapter())
				.registerTypeAdapter(MasonGeometry.class, new MasonGeometryTypeAdapter())
				.registerTypeAdapter(Apartment.class, new ReferenceTypeAdapter())
				.registerTypeAdapter(Classroom.class, new ReferenceTypeAdapter())
				.registerTypeAdapter(Pub.class, new ReferenceTypeAdapter())
				.registerTypeAdapter(Restaurant.class, new ReferenceTypeAdapter())
				.registerTypeAdapter(Workplace.class, new ReferenceTypeAdapter())
				.registerTypeAdapter(DateTime.class, new DateTimeTypeAdapter())
				.registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter())
				.registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
				.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
				.registerTypeAdapter(Item.class, new EventList.ItemTypeAdapter())
				.create();
		
		
		if(characteristicsSchema == null)
			characteristicsSchema = new GsonCsvSchemaFormatter(characteristicsGson);
		else 
			characteristicsSchema.setGson(characteristicsGson);
		
		if(characteristicsData == null)
			characteristicsData = new GsonCsvValueFormatter(characteristicsGson);
		else 
			characteristicsData.setGson(characteristicsGson);
		
		if(buildingUnitSchema == null)
			buildingUnitSchema = new GsonCsvSchemaFormatter(buildingUnitGson);
		else
			buildingUnitSchema.setGson(buildingUnitGson);
		
		if(buildingUnitData == null)
			buildingUnitData = new GsonCsvValueFormatter(buildingUnitGson);
		else 
			buildingUnitData.setGson(buildingUnitGson);
		
		if(buildingUnitStateSchema == null) {
			buildingUnitStateSchema = new GsonCsvSchemaFormatter(buildingUnitStateGson);
			buildingUnitStateSchema.setPrefix((Supplier<String> & Serializable) () -> "step\tsimulationTime\t");
		}
		else
			buildingUnitStateSchema.setGson(buildingUnitStateGson);
		
		if(buildingUnitStateData == null) {
			buildingUnitStateData = new GsonCsvValueFormatter(buildingUnitStateGson);
			buildingUnitStateData.setPrefix((Supplier<String> & Serializable) () -> getStepsAndTimeToString());
		}
		else 
			buildingUnitStateData.setGson(buildingUnitStateGson);
		
		if(stateSchema == null) {
			stateSchema = new GsonCsvSchemaFormatter(stateGson);
			stateSchema.setPrefix((Supplier<String> & Serializable) () -> "step\tsimulationTime\t");
		}
		else 
			stateSchema.setGson(stateGson);
		
		if(stateValue == null) {
			stateValue = new GsonCsvValueFormatter(stateGson);
			stateValue.setPrefix((Supplier<String> & Serializable) () -> getStepsAndTimeToString());
		}
		else
			stateValue.setGson(stateGson);
		
		if(eventFormatter == null)
			eventFormatter = new GsonCsvValueFormatter(eventGson);
		else
			eventFormatter.setGson(eventGson);

		if(textFormatter == null)
			textFormatter = new PlainTextFormatter();
	}
	
	private String getStepsAndTimeToString() {
		return getSteps() + "\t" + model.getSimulationTime() + "\t";
	}

	public class CensusDataCollector implements OutputFormatter, java.io.Serializable {
		private static final long serialVersionUID = -3281211399311529403L;

		@Override
		public String print(Object value) {
			census.reset();
			census.collectData();
			return null;
		}
	}
	
	public static Setting get(Level level) {
		return RESERVED_LEVELS.get(level);
	}
	
	public static String fullDirectory(String subDirectory) {
		// load from system property to decide root directory for logging
		String root = System.getProperty(LOG_ROOT_DIRECTORY_PROPERTY_NAME);
		if (root == null)
			root = DEFAULT_ROOT_DIRECTORY;

		if (subDirectory.startsWith("/") && root.endsWith("/")) {
			return root.substring(root.lastIndexOf("/")) + subDirectory;
		}
		if ((subDirectory.startsWith("/") && !root.endsWith("/"))
				|| (!subDirectory.startsWith("/") && root.endsWith("/"))) {
			return root + subDirectory;
		}

		return root + "/" + subDirectory;
	}
	
	public static class Setting {
		String name;
		String description;
		String outputType;
		String directory;
		String suffix;
		int size = 500;

		Setting(String name) {
			this(name, null);
		}
		
		Setting(String name, String description) {
			this(name, description, DEFAULT_OUTPUT_TYPE);
		}
		
		Setting(String name, String description, String outputType) {
			this(name, description, outputType, DEFAULT_DIRECTORY);
		}
		
		Setting(String name, String description, String outputType, String directory) {
			this(name, description, outputType, directory, DEFAULT_SUFFIX_TYPE);
		}
		
		Setting(String name, String description, String outputType, String directory, String suffix) {
			this.name = name;
			this.description = description;
			this.outputType = outputType;
			this.directory = fullDirectory(directory);
			this.suffix = suffix;
		}
		
		public String fileName() {
			return directory + name + suffix;
		}
		
		public String filePattern() {
			return directory + "$${date:yyyy-MM-dd}/" + name + "-%i" + suffix + ".zip"; 
		}
	}
	
}
