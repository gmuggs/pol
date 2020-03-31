package edu.gmu.mason.vanilla.db;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.gmu.mason.vanilla.db.Cdf.Instance.Run;

/**
 * This is class used to check whether structure of data generated in CDF is
 * correct.
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 *
 */
public class CdfValidator {
	private static final int RESET_INSTANCE_COUNT = 0;
	private static final int RESET_RUN_COUNT = -1;
	private static final String FILE_EXTENSION = ".tsv";
	private static final String DIRECTORY_NAME_SimulationDefinition = "SimulationDefinition";
	private static final String DIRECTORY_NAME_Instances = "Instances";
	private static final String DIRECTORY_NAME_Runs = "Runs";
	
	private Map<String, String> variableDefitions;
	private Map<String, String> relationshipDefinitions;
	
	private Map<String, Set<String>> missingVariable;
	private Map<String, Set<String>> missingRelationship;
	
	private Map<String, Boolean> checkVariable;
	private Map<String, Boolean> checkRelationship;

	private String rootDirectory;
	private String nextPath;
	private List<String> mustHaveTables;
	private int instanceCount;
	private int runCount;
	public Cdf cdf;

	public CdfValidator(String rootDirectory) {
		this.rootDirectory = rootDirectory;
		cdf = new Cdf();
		mustHaveTables = new ArrayList<String>();
		variableDefitions = new HashMap<String, String>();
		relationshipDefinitions = new HashMap<String, String>();
		missingVariable = new HashMap<String, Set<String>>();
		missingRelationship = new HashMap<String, Set<String>>();
		checkVariable = new HashMap<String, Boolean>();
		checkRelationship = new HashMap<String, Boolean>();
	}

	public void addTableName(String tableName) {
		mustHaveTables.add(tableName);
	}

	public void addTableNameList(List<String> tableNames) {
		mustHaveTables.addAll(tableNames);
	}

	private String getRootDirectory() {
		return rootDirectory;
	}

	private String getCurrentRunPath() {
		return getCurrentInstancePath() + "Runs/run-" + runCount + "/";
	}

	private String getCurrentInstancePath() {
		return getInstancesPath() + "Instance" + instanceCount + "/";
	}

	private String getInstancesPath() {
		return getRootDirectory() + DIRECTORY_NAME_Instances + "/";
	}

	private void initializePathForTraverse() {
		instanceCount = RESET_INSTANCE_COUNT;
		runCount = RESET_RUN_COUNT;
	}

	private String nextRun() {
		// Instances -> Instance1 -> Runs -> run-0 ...
		// Try increasing runCount
		nextPath = getCurrentInstancePath() + "Runs/run-" + (runCount + 1) + "/";
		if (directoryExists(nextPath)) {
			runCount++;
			return nextPath;
		}

		throw new IndexOutOfBoundsException();
	}

	private String nextInstance() {
		// Try increasing instanceCount and reset runCount
		nextPath = getInstancesPath() + "Instance" + (instanceCount + 1) + "/";
		if (directoryExists(nextPath)) {
			instanceCount++;
			runCount = RESET_RUN_COUNT;
			return nextPath;
		}
		throw new IndexOutOfBoundsException();
	}

	private boolean hasNextRun() {
		// Instances -> Instance1 ...
		// Try increasing runCount
		nextPath = getCurrentInstancePath() + "Runs/run-" + (runCount + 1) + "/";
		if (directoryExists(nextPath)) {
			return true;
		}
		return false;
	}

	private boolean hasNextInstance() {
		// Try increasing instanceCount and reset runCount
		nextPath = getInstancesPath() + "Instance" + (instanceCount + 1) + "/";
		if (directoryExists(nextPath)) {
			return true;
		}
		return false;
	}

	boolean directoryExists(String path) {
		File file = new File(path);
		if (file.isDirectory() && file.exists()) {
			return true;
		}
		return false;
	}

	boolean fileExists(String path) {
		File file = new File(path);
		if (!file.isDirectory() && file.exists()) {
			return true;
		}
		return false;
	}

	String getWholePath(String path) {
		return getCurrentRunPath() + path;
	}

	boolean isFileExisit(String fileName) {
		File file = new File(fileName);
		if (file.exists()) {
			return true;
		}
		return false;
	}

	boolean checkFilesInRuns() {
		// check if there exist unnecessary files in the directory.
		boolean isValid = true;
		Run run = cdf.Instance.Run;
		String path = getCurrentRunPath() + run.RunDataTable.name + FILE_EXTENSION;
		if (mustHaveTables.contains(run.RunDataTable.name)) {
			if (!fileExists(path)) {
				isValid = false;
				System.err.println("File does not exist: " + path);
			} else {
				try (LineNumberReader reader = new LineNumberReader(new FileReader(path))) {
					// header
					String line = reader.readLine();
					String[] tokens;
					int nameIdx;
					while ((line = reader.readLine()) != null) {
						if(line.equals(""))
							continue;
						tokens = line.split("\t");
						nameIdx = 1;
//						if (line.startsWith("\t"))
//							nameIdx = 0;
						if (!variableDefitions.containsKey(tokens[nameIdx])) {
							isValid = false;
							if (!missingVariable.containsKey(tokens[nameIdx]))
								missingVariable.put(tokens[nameIdx], new HashSet<String>());
							missingVariable.get(tokens[nameIdx]).add(path);
						}
						else {
							checkVariable.put(tokens[nameIdx], true);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					isValid = false;
				}
			}
		}
		

		path = getCurrentRunPath() + run.SummaryStatisticsDataTable.name + FILE_EXTENSION;
		if (mustHaveTables.contains(run.SummaryStatisticsDataTable.name)) {
			if (!fileExists(path)) {
				isValid = false;
				System.err.println("File does not exist: " + path);
			} else {
				try (LineNumberReader reader = new LineNumberReader(new FileReader(path))) {
					// header
					String line = reader.readLine();
					String[] tokens;
					int nameIdx;
					while ((line = reader.readLine()) != null) {
						if(line.equals(""))
							continue;
						tokens = line.split("\t");
						nameIdx = 1;
//						if (line.startsWith("\t"))
//							nameIdx = 0;
						if (!variableDefitions.containsKey(tokens[nameIdx])) {
							isValid = false;
							if (!missingVariable.containsKey(tokens[nameIdx]))
								missingVariable.put(tokens[nameIdx], new HashSet<String>());
							missingVariable.get(tokens[nameIdx]).add(path);
						}
						else {
							checkVariable.put(tokens[nameIdx], true);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					isValid = false;
				}
			}
		}

		path = getCurrentRunPath() + run.QualitativeDataTable.name + FILE_EXTENSION;
		if (mustHaveTables.contains(run.QualitativeDataTable.name) && !fileExists(path)) {
			isValid = false;
			System.err.println("File does not exist: " + path);
		}

		path = getCurrentRunPath() + run.RelationshipDataTable.name + FILE_EXTENSION;
		if (mustHaveTables.contains(run.RelationshipDataTable.name)) {
			if (!fileExists(path)) {
				isValid = false;
				System.err.println("File does not exist: " + path);
			} else {
				try (LineNumberReader reader = new LineNumberReader(new FileReader(path))) {
					// header
					String line = reader.readLine();
					String[] tokens;
					int nameIdx;
					while ((line = reader.readLine()) != null) {
						if(line.equals(""))
							continue;
						tokens = line.split("\t");
						nameIdx = 1;
//						if (line.startsWith("\t"))
//							nameIdx = 0;
						if (!relationshipDefinitions.containsKey(tokens[nameIdx])) {
							isValid = false;
							if (!missingRelationship.containsKey(tokens[nameIdx]))
								missingRelationship.put(tokens[nameIdx], new HashSet<String>());
							missingRelationship.get(tokens[nameIdx]).add(path);
						}
						else {
							checkRelationship.put(tokens[nameIdx], true);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					isValid = false;
				}
			}
		}
		
		
		return isValid;
	}

	boolean isInstanceVariableTableCorrect() {
		// check if there exist unnecessary files in the directory.
		boolean isValid = true;
		String path = getCurrentInstancePath() + cdf.Instance.InstanceVariableTable.name + FILE_EXTENSION;
		if (mustHaveTables.contains(cdf.Instance.InstanceVariableTable.name) && !fileExists(path)) {
			isValid = false;
			System.err.println("File does not exist: " + path);
		}

		// TODO: checking data in the table

		return isValid;
	}

	public boolean isCdfStructureValid() {
		String path = null;
		String runPath = null;
		String instancePath = null;
		boolean isValid = true;

		// check SimulationDefintion
		String directory = getRootDirectory() + DIRECTORY_NAME_SimulationDefinition;
		if (!directoryExists(directory)) {
			isValid = false;
			System.err.println("Directory does not exist: " + directory);
		} else {
			//
			path = getRootDirectory() + DIRECTORY_NAME_SimulationDefinition + "/" + cdf.SimulationDefinition.VariableDefTable.name + FILE_EXTENSION;
			if (mustHaveTables.contains(cdf.SimulationDefinition.VariableDefTable.name)) {
				if (!fileExists(path)) {
					isValid = false;
					System.err.println("File does not exist: " + path);
				} else {
					try (LineNumberReader reader = new LineNumberReader(new FileReader(path))) {
						// header
						String line = reader.readLine();
						String[] tokens;
						while ((line = reader.readLine()) != null) {
							if (line.equals(""))
								continue;
							tokens = line.split("\t");
							// NAME, Data Type
							checkVariable.put(tokens[0], false);
							variableDefitions.put(tokens[0], tokens[4]);
						}
					} catch (Exception e) {
						e.printStackTrace();
						isValid = false;
					}
				}
			}

			path = getRootDirectory() + DIRECTORY_NAME_SimulationDefinition + "/"
					+ cdf.SimulationDefinition.RelationshipDefTable.name + FILE_EXTENSION;
			if (mustHaveTables.contains(cdf.SimulationDefinition.RelationshipDefTable.name)) {
				if (!fileExists(path)) {
					isValid = false;
					System.err.println("File does not exist: " + path);
				} else {
					try (LineNumberReader reader = new LineNumberReader(new FileReader(path))) {
						// header
						String line = reader.readLine();
						String[] tokens;
						while ((line = reader.readLine()) != null) {
							if (line.equals(""))
								continue;
							tokens = line.split("\t");
							// NAME, Data Type
							checkRelationship.put(tokens[0], false);
							relationshipDefinitions.put(tokens[0], tokens[4]);
						}
					} catch (Exception e) {
						e.printStackTrace();
						isValid = false;
					}
				}
			}
		}

		// check Instances
		directory = getRootDirectory() + DIRECTORY_NAME_Instances;
		if (!directoryExists(directory)) {
			isValid = false;
			System.err.println("Directory does not exist: " + directory);
		}

		// check Runs

		initializePathForTraverse();
		while (hasNextInstance()) {
			instancePath = nextInstance();
			if (!isInstanceVariableTableCorrect()) {
				isValid = false;
				System.err.println("Check path: " + instancePath);
			}

			directory = instancePath + DIRECTORY_NAME_Runs;
			if (!directoryExists(directory)) {
				isValid = false;
				System.err.println("Directory does not exist: " + directory);
			}

			while (hasNextRun()) {
				runPath = nextRun();
				if (!checkFilesInRuns()) {
					isValid = false;
					System.err.println("Check path: " + runPath);
				}
			}
		}
		
		// print all missing variables
		missingVariable
				.forEach((k, v) -> v.forEach(p -> System.err.println(k + " is not defined in VariableDefTable:" + p)));
		missingRelationship.forEach(
				(k, v) -> v.forEach(p -> System.err.println(k + " is not defined in RelationshipDefTable:" + p)));
		checkVariable.forEach((k, v) -> {
			if (!v)
				System.err.println(k + " is defined VariableDefTable. But, it is never used.");
		});
		checkRelationship.forEach((k, v) -> {
			if (!v)
				System.err.println(k + " is defined RelationshipDefTable. But, it is never used.");
		});

		return isValid;
	}

	public static void main(String... args) {
		CdfValidator validator = new CdfValidator("logs/TA2B-TA1A-22PsychometricsOn100-RR/TA2B-TA1A-22PsychometricsOn100-RR/");

		// add names in a static way.
		validator.addTableName(validator.cdf.SimulationDefinition.VariableDefTable.name);
//		validator.addTableName(validator.cdf.SimulationDefinition.RelationshipDefTable.name);
//		validator.addTableName(validator.cdf.Instance.InstanceVariableTable.name);
		validator.addTableName(validator.cdf.Instance.Run.RunDataTable.name);
//		validator.addTableName(validator.cdf.Instance.Run.SummaryStatisticsDataTable.name);
//		validator.addTableName(validator.cdf.Instance.Run.QualitativeDataTable.name);
//		validator.addTableName(validator.cdf.Instance.Run.RelationshipDataTable.name);

		if (validator.isCdfStructureValid()) {
			System.out.println("CDF file structure is valid.");
		} else {
			System.err.println("CDF file structure is not valid.");
		}
	}
}
