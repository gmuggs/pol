package edu.gmu.mason.vanilla.db;

import java.lang.reflect.Field;

/**
 * General description_________________________________________________________
 * This is a java class that has all structure of Common Data Format (CDF).
 * Structure of CDF
 * 
 * +-SimulationDefinition | +-VariableDefTable | +-RelationshipDefTable
 * +-Instances +-Instance1 | +-InstanceVariableTable | +-Runs | +-run-0 | |
 * +-RunDataTable | | +-SummaryStatisticsDataTable | | +-QualitativedataTable |
 * | +-RelationshipdataTable | +-run-1 | +-... +-Instance2 +-...
 * 
 * This could be JSON format that describes CDF. 
 * 
 * +-SimulationDefinition
 *  | +-VariableDefTable 
 *  | +-RelationshipDefTable
 *  +-Instances
 *    +-Instance1
 *    | +-InstanceVariableTable
 *    | +-Runs
 *    |   +-run-0
 *    |   | +-RunDataTable
 *    |   | +-SummaryStatisticsDataTable
 *    |   | +-QualitativedataTable
 *    |   | +-RelationshipdataTable
 *    |   +-run-1
 *    |     +-...
 *    +-Instance2
 *      +-...
 * 
 * This could be JSON format that describes CDF.
 * {
 *   "SimulationDefinition": {
 *     "VariableDefTable": {
 *       "Name": {},
 *       "LongName": {},
 *       "Values": {},
 *       "VarType": {},
 *       "DataType": {},
 *       "Notes": {}
 *     },
 *     "RelationshipDefTable": {
 *       "Name": {},
 *       "LongName": {},
 *       "RelType": {},
 *       "Notes": {}
 *     }
 *   },
 *   "Instances": [
 *     {
 *       "InstanceVariableTable": {},
 *       "Runs": [
 *         {
 *           "RunDataTable": {},
 *           "SummaryStatisticsDataTable": {},
 *           "QualitativeDataTable": {},
 *           "RelationshipDataTable": {}
 *         },
 *         {
 *           "RunDataTable": {},
 *           "SummaryStatisticsDataTable": {},
 *           "QualitativeDataTable": {},
 *           "RelationshipDataTable": {}
 *         }
 *       ]
 *     },
 *     {
 *       "InstanceVariableTable": {},
 *       "Runs": [
 *         {
 *           "RunDataTable": {},
 *           "SummaryStatisticsDataTable": {},
 *           "QualitativeDataTable": {},
 *           "RelationshipDataTable": {}
 *         },
 *         {
 *           "RunDataTable": {},
 *           "SummaryStatisticsDataTable": {},
 *           "QualitativeDataTable": {},
 *           "RelationshipDataTable": {}
 *         }
 *       ]
 *     }
 *   ]
 * }
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */

public class Cdf implements java.io.Serializable {
	private static final long serialVersionUID = -163164788300945435L;

	public SimulationDefinition SimulationDefinition = new SimulationDefinition();

	public class SimulationDefinition implements java.io.Serializable {
		public VariableDefTable VariableDefTable = new VariableDefTable();

		public class VariableDefTable extends Table {
			public Column Name = new Column();
			public Column LongName = new Column();
			public Column Values = new Column();
			public Column VarType = new Column();
			public Column DataType = new Column();
			public Column Notes = new Column();
		}

		public RelationshipDefTable RelationshipDefTable = new RelationshipDefTable();

		public class RelationshipDefTable extends Table {
			public Column Name = new Column();
			public Column LongName = new Column();
			public Column Values = new Column();
			public Column RelType = new Column();
			public Column DataType = new Column();
			public Column Notes = new Column();
		}
	}

	public Instance Instance = new Instance();

	public class Instance implements java.io.Serializable {
		public InstanceVariableTable InstanceVariableTable = new InstanceVariableTable();

		public class InstanceVariableTable extends Table {
			public Column Name = new Column();
			public Column Timestep = new Column();
			public Column Value = new Column();
		}

		public Run Run = new Run();

		public class Run implements java.io.Serializable {
			public RunDataTable RunDataTable = new RunDataTable();

			public class RunDataTable extends Table {
				public Column Timestep = new Column();
				public Column VariableName = new Column();
				public Column EntityIdx = new Column();
				public Column Value = new Column();
				public Column Notes = new Column();
			}

			public SummaryStatisticsDataTable SummaryStatisticsDataTable = new SummaryStatisticsDataTable();

			public class SummaryStatisticsDataTable extends Table {
				public Column Timestep = new Column();
				public Column VariableName = new Column();
				public Column EntityIdx = new Column();
				public Column Value = new Column();
				public Column Metadata = new Column();
			}

			public QualitativeDataTable QualitativeDataTable = new QualitativeDataTable();

			public class QualitativeDataTable extends Table {
				public Column Timestep = new Column();
				public Column EntityIdx = new Column();
				public Column QualData = new Column();
				public Column Metadata = new Column();
			}

			public RelationshipDataTable RelationshipDataTable = new RelationshipDataTable();

			public class RelationshipDataTable extends Table {
				public Column Timestep = new Column();
				public Column RelationshipType = new Column();
				public Column Directed = new Column();
				public Column FromEntityID = new Column();
				public Column ToEntityID = new Column();
				public Column Data = new Column();
				public Column Notes = new Column();
			}
		}
	}

	public Cdf() {
		try {
			recursiveInit(this);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private void recursiveInit(Object parent) throws IllegalArgumentException,
			IllegalAccessException {
		Field[] fields = parent.getClass().getFields();
		for (int i = 0; i < fields.length; i++) {
			Object child = fields[i].get(parent);
			if (child instanceof Column)
				((Column) child).init(parent);
			else if (child instanceof Table) {
				recursiveInit(child);
				((Table) child).init(parent);
			} else if (child != null && !(child instanceof String)
					&& !(child instanceof Integer))
				recursiveInit(child);
		}
	}
}
