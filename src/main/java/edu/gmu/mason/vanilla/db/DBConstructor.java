package edu.gmu.mason.vanilla.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.io.Reader;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.MessageFormat;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import com.google.gson.GsonBuilder;
import com.vividsolutions.jts.geom.Geometry;

/**
 * General description_________________________________________________________
 * Database construction class.
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class DBConstructor implements AutoCloseable {
	private String[] archive_log_dir = {Constants.LOG_DIR};
	private PrintStream out;
	private int batchSize = 10000;
	private String delimiter = ";";
	private boolean fullLineDelimiter = false;
	private boolean stopOnError = true;
	private boolean autoCommit = false;
	private boolean silenceMode = true;
	private Connection connection = null;
	private List<String> buildingUnits = Arrays.asList(new String[] {
			"Apartment", "Classroom", "Pub", "Restaurant",
			"Workplace" });

	public DBConstructor(String logFilename) throws IOException {
		out = new PrintStream(logFilename);
	}

	public void connect(String host, int port, String database, String user,
			String password) throws SQLException {
		String connectionString = MessageFormat.format(Constants.BASE_URL,
				host, String.valueOf(port), database, user, password);
		connection = DriverManager.getConnection(connectionString);
		connection.setAutoCommit(autoCommit);
	}

	public void createSchema() throws FileNotFoundException, IOException,
			SQLException {
		File directory = new File(Constants.SCHEMA_DIR);
		if (directory.exists() && directory.isDirectory()) {
			FilenameFilter filter = new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					if (name.endsWith("Table.sql"))
						return true;
					return false;
				}
			};
			// find all SQL files
			File[] sqlFiles = directory.listFiles(filter);
			for (int i = 0; i < sqlFiles.length; i++) {
				// The order of files is important because they have dependency.
				runScript(connection, new FileReader(sqlFiles[i]));
			}
		}
	}
	
	public void createIndex() throws FileNotFoundException, IOException, SQLException {
		File directory = new File(Constants.SCHEMA_DIR);
		if (directory.exists() && directory.isDirectory()) {
			FilenameFilter filter = new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					if (name.endsWith("Index.sql"))
						return true;
					return false;
				}
			};
			// find all SQL files
			File[] sqlFiles = directory.listFiles(filter);
			for (int i = 0; i < sqlFiles.length; i++) {
				// The order of files is important because they have dependency.
				runScript(connection, new FileReader(sqlFiles[i]));
			}
		}
	}
	
	private void insertArchive() throws Exception {
		boolean inserted = true;
		for (int j = 0; j < archive_log_dir.length; j++) {
			// we can add multiple directories for archive files
			File archive = new File(archive_log_dir[j]);
			if (archive.exists() && archive.isDirectory()) {
				FilenameFilter filter = new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						if (name.endsWith("zip"))
							return true;
						return false;
					}
				};
				File[] dataFiles = archive.listFiles(filter);
				for (int i = 0; i < dataFiles.length; i++) {
					try (FileInputStream fis = new FileInputStream(dataFiles[i])) {
						out.println("Processing: " + dataFiles[i].getPath());
						ZipInputStream stream = new ZipInputStream(fis);

						ZipEntry entry;
						while ((entry = stream.getNextEntry()) != null) {
							String tableName = entry.getName();

							int tableStrIdx = tableName.indexOf("Table");
							// if the file name does not contain 'Table' such as group_world.log
							if (tableStrIdx == -1)
								continue;

							tableName = tableName.substring(0, tableStrIdx);

							long before = System.currentTimeMillis();
							out.println("File in " + dataFiles[i].getName() + " : " + entry.getName());
							inserted = true;
							// AgentState
							if (tableName.equalsIgnoreCase("AgentState")) {
								//insert(stream, tableName, true);
								insertAgentState(stream, tableName, true);
							}
							// Job
							else if (tableName.equalsIgnoreCase("Job")) {
								insert(stream, tableName, true);
							}
							else {
								inserted = false;
							}

							long after = System.currentTimeMillis();
							if(inserted)
								out.println(tableName + ": " + (after - before) + "ms");
							else 
								out.println("Skipped " + entry.getName());
						}
					}
				}
			}
		}
	}
	
	private void insertLog() throws Exception {
		File directory = new File(Constants.LOG_DIR);
		if (directory.exists() && directory.isDirectory()) {
			FilenameFilter filter = new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					if (name.endsWith("tsv") && name.contains("Table"))
						return true;
					return false;
				}
			};
			File[] dataFiles = directory.listFiles(filter);
			for (int i = 0; i < dataFiles.length; i++) {
				String tableName = dataFiles[i].getName();
				tableName = tableName.substring(0, tableName.indexOf("Table"));
				long before = System.currentTimeMillis();
				// 0. InstanceData
				// 1. Building Units
				// 2. Others
				out.println("Processing: " + dataFiles[i].getPath());
				if (tableName.equalsIgnoreCase("InstanceData")) {
					try (FileInputStream fis = new FileInputStream(dataFiles[i])) {
						insert(fis, tableName, true);
					}
				} else if (buildingUnits.contains(tableName)) {
					// Building Units need to be split columns into two tables
					// except Workplace.
					try (FileInputStream fis = new FileInputStream(dataFiles[i])) {
						insert(fis, "BuildingUnit", true);
					}
					if (!tableName.equalsIgnoreCase("Workplace")) {
						try (FileInputStream fis = new FileInputStream(
								dataFiles[i])) {
							insert(fis, tableName, true);
						}
					}
				} else {
					try (FileInputStream fis = new FileInputStream(dataFiles[i])) {
						insert(fis, tableName, true);
					}
				}

				long after = System.currentTimeMillis();
				
				out.println(tableName + ": " + (after - before) + "ms");

			}
		}
	}

	private void insertAllLog() throws Exception {
		// Two directories need to be considered.
		insertArchive();
		insertLog();
	}
	
	private void insertAgentState(InputStream inputStream, String tableName, boolean skip)
			throws Exception {
		LineNumberReader lineReader = new LineNumberReader(
				new InputStreamReader(inputStream));
		if (lineReader.markSupported()) {
			lineReader.mark(Constants.MAX_HEADER_SIZE);
		}
		
		SchemaMap typeTable = getSchema(tableName);
		if (typeTable == null)
			throw new Exception("No table exists: " + tableName);
		LocalDateTime month = null;
		do {
			month = insertAgentState(month, typeTable, lineReader, tableName, skip);
		} while (month != null);
		
	}
	
	private LocalDateTime insertAgentState(LocalDateTime month, SchemaMap typeTable, LineNumberReader lineReader, String tableName, boolean skip)
			throws Exception {
		boolean differentMonth = false;
		String sql = "INSERT INTO public.\"" + tableName + "\" ";
		if(month!=null) {
			// AgentState_y2018m04
			sql  ="INSERT INTO public.\"" + tableName + "_y" + month.getYear() + "m" + month.toString("MM") + "\" ";
		}
		StringBuilder fieldNameBuilder = new StringBuilder("(");
		StringBuilder valueStringBuilder = new StringBuilder(" VALUES (");

		String line = lineReader.readLine();
		String[] columns = line.split("\t");
		boolean hasHeader = false;
		boolean sameOrder = true;
		for (int j = 0; j < columns.length; j++) {
			Schema schema = typeTable.getSchemaByIndex(j);
			if (schema != null && !schema.name.equals(columns[j]))
				sameOrder = false;
			schema = typeTable.getSchemaByName(columns[j]);
			if (schema == null) {
				if (!skip)
					throw new Exception("cannot find " + columns[j]
							+ " field in " + tableName + " table.");
				continue;
			}
			hasHeader = true;

			fieldNameBuilder.append("\"").append(columns[j]).append("\",");
			if (schema.type.equals(Geometry.class))
				valueStringBuilder.append("ST_GeomFromText(?,4326),");
			else if (schema.type.equals(List.class))
				valueStringBuilder.append("array(?),");
			else
				valueStringBuilder.append("?,");

		}
		if (!hasHeader) {
			// if there is no header
			lineReader.reset();
			columns = new String[typeTable.size()];
			sameOrder = true;
			for (int j = 0; j < columns.length; j++) {
				Schema schema = typeTable.getSchemaByIndex(j);
				columns[j] = schema.name;
				if (schema.type.equals(Geometry.class))
					valueStringBuilder.append("ST_GeomFromText(?,4326),");
				else if (schema.type.equals(List.class))
					valueStringBuilder.append("array(?),");
				else
					valueStringBuilder.append("?,");
			}
		}
		fieldNameBuilder.deleteCharAt(fieldNameBuilder.length() - 1)
				.append(")");
		valueStringBuilder.deleteCharAt(valueStringBuilder.length() - 1)
				.append(")");

		if (!sameOrder)
			sql += fieldNameBuilder.toString();
		sql += valueStringBuilder.toString();
		PreparedStatement insertQuery = connection.prepareStatement(sql);
		String[] fields;
		int count = 0;
		while ((line = lineReader.readLine()) != null) {
			fields = line.split("\t");
			for (int j = 0, parameterIndex = 0; j < fields.length; j++) {
				if (columns.length - 1 < j)
					continue;
				Schema schema = typeTable.getSchemaByName(columns[j]);
				if (schema == null && skip)
					continue;
				parameterIndex++;
				boolean isNull = false;
				if (fields[j].equalsIgnoreCase("null")) {
					isNull = true;
				}
				if (schema.type.equals(String.class)
						|| schema.type.equals(Geometry.class)) {
					if (isNull)
						insertQuery.setNull(parameterIndex, Types.NCHAR);
					else
						insertQuery.setString(parameterIndex, fields[j]);
				} else if (schema.type.equals(Integer.class)) {
					if (isNull)
						insertQuery.setNull(parameterIndex, Types.INTEGER);
					else
						insertQuery.setInt(parameterIndex,
								Integer.valueOf(fields[j]));
				} else if (schema.type.equals(Boolean.class)) {
					if (isNull)
						insertQuery.setNull(parameterIndex, Types.BOOLEAN);
					else
						insertQuery.setBoolean(parameterIndex,
								Boolean.valueOf(fields[j]));
				} else if (schema.type.equals(Double.class)) {
					if (isNull)
						insertQuery.setNull(parameterIndex, Types.DOUBLE);
					else
						insertQuery.setDouble(parameterIndex,
								Double.valueOf(fields[j]));
				} else if (schema.type.equals(Array.class)) {
					if (isNull)
						insertQuery.setNull(parameterIndex, Types.ARRAY);
					else {
						// TODO: Need a more stable method. It only can handle
						// integer array.
						Integer[] arr = new GsonBuilder().create().fromJson(
								fields[j], Integer[].class);
						insertQuery.setArray(parameterIndex,
								connection.createArrayOf("integer", arr));
					}
				} else if (schema.type.equals(DateTime.class)) {
					if (isNull)
						insertQuery.setNull(parameterIndex,
								Types.TIMESTAMP_WITH_TIMEZONE);
					else
						insertQuery.setObject(parameterIndex, OffsetDateTime.parse(fields[j]));
				} else if (schema.type.equals(LocalDateTime.class)) {
					if (isNull)
						insertQuery.setNull(parameterIndex, Types.TIMESTAMP);
					else {
						insertQuery.setObject(parameterIndex, java.time.LocalDateTime.parse(fields[j]));
						// Optimization code
						if(columns[j].equals("simulationTime")) {
							// table
							LocalDateTime date = LocalDateTime.parse(fields[j]);
							if(month == null || date.getMonthOfYear() != month.getMonthOfYear()) {
								// cancel inserting query
								differentMonth = true;
								// reader recover
								lineReader.reset();
								// change month
								month = date;
								break;
							}
						}
					}
				} else if (schema.type.equals(LocalTime.class)) {
					if (isNull)
						insertQuery.setNull(parameterIndex, Types.TIME);
					else

						insertQuery.setObject(parameterIndex, java.time.LocalTime.parse(fields[j]));

				} else if (schema.type.equals(LocalDate.class)) {
					if (isNull)
						insertQuery.setNull(parameterIndex, Types.DATE);
					else
						insertQuery.setDate(parameterIndex,
								java.sql.Date.valueOf(fields[j]));
				} else if (!skip)
					throw new Exception("Cannot handle " + schema.type
							+ "type.");
			}
			if(differentMonth)
				break;

			insertQuery.addBatch();
			if (++count % batchSize == 0)
				insertQuery.executeBatch();
			// mark for recover
			lineReader.mark(Constants.MAX_HEADER_SIZE);
		}
		insertQuery.executeBatch();
		if (!connection.getAutoCommit())
			connection.commit();
		if(!differentMonth)
			return null;
		return month;
	}

	private void insert(InputStream inputStream, String tableName, boolean skip)
			throws Exception {
		SchemaMap typeTable = getSchema(tableName);
		if (typeTable == null)
			throw new Exception("No table exists: " + tableName);

		String sql = "INSERT INTO public.\"" + tableName + "\" ";
		StringBuilder fieldNameBuilder = new StringBuilder("(");
		StringBuilder valueStringBuilder = new StringBuilder(" VALUES (");
		PreparedStatement insertQuery = null;
		// we assume the first line will be schema

		// but if there is no header, we will detect it.
		LineNumberReader lineReader = new LineNumberReader(
				new InputStreamReader(inputStream));
		if (lineReader.markSupported()) {
			lineReader.mark(Constants.MAX_HEADER_SIZE);
		}
		String line = lineReader.readLine();
		// validate if the schema corresponds to DB schema.
		String[] columns = line.split("\t");
		boolean hasHeader = false;
		boolean sameOrder = true;
		for (int j = 0; j < columns.length; j++) {
			Schema schema = typeTable.getSchemaByIndex(j);
			if (schema != null && !schema.name.equals(columns[j]))
				sameOrder = false;
			schema = typeTable.getSchemaByName(columns[j]);
			if (schema == null) {
				if (!skip)
					throw new Exception("cannot find " + columns[j]
							+ " field in " + tableName + " table.");
				continue;
			}
			hasHeader = true;

			fieldNameBuilder.append("\"").append(columns[j]).append("\",");
			if (schema.type.equals(Geometry.class))
				valueStringBuilder.append("ST_GeomFromText(?,4326),");
			else if (schema.type.equals(List.class))
				valueStringBuilder.append("array(?),");
			else
				valueStringBuilder.append("?,");

		}
		if (!hasHeader) {
			// if there is no header
			lineReader.reset();
			columns = new String[typeTable.size()];
			sameOrder = true;
			for (int j = 0; j < columns.length; j++) {
				Schema schema = typeTable.getSchemaByIndex(j);
				columns[j] = schema.name;
				if (schema.type.equals(Geometry.class))
					valueStringBuilder.append("ST_GeomFromText(?,4326),");
				else if (schema.type.equals(List.class))
					valueStringBuilder.append("array(?),");
				else
					valueStringBuilder.append("?,");
			}
		}
		fieldNameBuilder.deleteCharAt(fieldNameBuilder.length() - 1)
				.append(")");
		valueStringBuilder.deleteCharAt(valueStringBuilder.length() - 1)
				.append(")");

		if (!sameOrder)
			sql += fieldNameBuilder.toString();
		sql += valueStringBuilder.toString();

		insertQuery = connection.prepareStatement(sql);

		String[] fields;
		int count = 0;
		while ((line = lineReader.readLine()) != null) {
			fields = line.split("\t");
			for (int j = 0, parameterIndex = 0; j < fields.length; j++) {
				if (columns.length - 1 < j)
					continue;
				Schema schema = typeTable.getSchemaByName(columns[j]);
				if (schema == null && skip)
					continue;
				parameterIndex++;
				boolean isNull = false;
				if (fields[j].equalsIgnoreCase("null")) {
					isNull = true;
				}
				if (schema.type.equals(String.class)
						|| schema.type.equals(Geometry.class)) {
					if (isNull)
						insertQuery.setNull(parameterIndex, Types.NCHAR);
					else
						insertQuery.setString(parameterIndex, fields[j]);
				} else if (schema.type.equals(Integer.class)) {
					if (isNull)
						insertQuery.setNull(parameterIndex, Types.INTEGER);
					else
						insertQuery.setInt(parameterIndex,
								Integer.valueOf(fields[j]));
				} else if (schema.type.equals(Boolean.class)) {
					if (isNull)
						insertQuery.setNull(parameterIndex, Types.BOOLEAN);
					else
						insertQuery.setBoolean(parameterIndex,
								Boolean.valueOf(fields[j]));
				} else if (schema.type.equals(Double.class)) {
					if (isNull)
						insertQuery.setNull(parameterIndex, Types.DOUBLE);
					else
						insertQuery.setDouble(parameterIndex,
								Double.valueOf(fields[j]));
				} else if (schema.type.equals(Array.class)) {
					if (isNull)
						insertQuery.setNull(parameterIndex, Types.ARRAY);
					else {
						// TODO: Need a more stable method. It only can handle
						// integer array.
						Integer[] arr = new GsonBuilder().create().fromJson(
								fields[j], Integer[].class);
						insertQuery.setArray(parameterIndex,
								connection.createArrayOf("integer", arr));
					}
				} else if (schema.type.equals(DateTime.class)) {
					if (isNull)
						insertQuery.setNull(parameterIndex,
								Types.TIMESTAMP_WITH_TIMEZONE);
					else
						insertQuery.setObject(parameterIndex, OffsetDateTime.parse(fields[j]));
				} else if (schema.type.equals(LocalDateTime.class)) {
					if (isNull)
						insertQuery.setNull(parameterIndex, Types.TIMESTAMP);
					else
						insertQuery.setObject(parameterIndex, java.time.LocalDateTime.parse(fields[j]));
				} else if (schema.type.equals(LocalTime.class)) {
					if (isNull)
						insertQuery.setNull(parameterIndex, Types.TIME);
					else

						insertQuery.setObject(parameterIndex, java.time.LocalTime.parse(fields[j]));

				} else if (schema.type.equals(LocalDate.class)) {
					if (isNull)
						insertQuery.setNull(parameterIndex, Types.DATE);
					else
						insertQuery.setDate(parameterIndex,
								java.sql.Date.valueOf(fields[j]));
				} else if (!skip)
					throw new Exception("Cannot handle " + schema.type
							+ "type.");
			}
			insertQuery.addBatch();
			if (++count % batchSize == 0)
				insertQuery.executeBatch();
		}
		insertQuery.executeBatch();

		if (!connection.getAutoCommit())
			connection.commit();

	}

	private SchemaMap getSchema(String tableName) throws Exception {
		SchemaMap mapping = null;
		Statement statement = connection.createStatement();
		boolean hasResults = statement
				.execute("select column_name, data_type, udt_name "
						+ "from INFORMATION_SCHEMA.COLUMNS where table_name = '"
						+ tableName + "'");
		ResultSet rs = statement.getResultSet();
		if (hasResults && rs != null) {
			int i = 0;
			mapping = new SchemaMap();
			while (rs.next()) {
				Schema schema = new Schema();
				schema.name = rs.getString(1);
				String udt_name = rs.getString(3);
				schema.setType(rs.getString(2), udt_name);
				schema.index = i++;
				mapping.add(schema);
			}
		}
		return mapping;
	}

	public void close() {
		if (connection != null) {
			try {
				if (!connection.isClosed())
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		out.close();
	}

	private void runScript(Connection conn, Reader reader) throws IOException,
			SQLException {
		StringBuffer command = null;
		try {
			LineNumberReader lineReader = new LineNumberReader(reader);
			String line = null;
			while ((line = lineReader.readLine()) != null) {
				if (command == null) {
					command = new StringBuffer();
				}
				String trimmedLine = line.trim();
				if (trimmedLine.startsWith("--")) {
					if (!silenceMode)
						out.println(trimmedLine);
				} else if (trimmedLine.length() < 1
						|| trimmedLine.startsWith("//")) {
					// Do nothing
				} else if (trimmedLine.length() < 1
						|| trimmedLine.startsWith("--")) {
					// Do nothing
				} else if (!fullLineDelimiter
						&& trimmedLine.endsWith(delimiter) || fullLineDelimiter
						&& trimmedLine.equals(delimiter)) {
					command.append(line.substring(0,
							line.lastIndexOf(delimiter)));
					command.append(" ");
					Statement statement = conn.createStatement();

					if (!silenceMode)
						out.println(command);

					boolean hasResults = false;
					if (stopOnError) {
						hasResults = statement.execute(command.toString());
					} else {
						try {
							statement.execute(command.toString());
						} catch (SQLException e) {
							e.fillInStackTrace();
							out.println("Error executing: " + command);
						}
					}

					if (autoCommit && !conn.getAutoCommit()) {
						conn.commit();
					}

					ResultSet rs = statement.getResultSet();
					if (hasResults && rs != null) {
						ResultSetMetaData md = rs.getMetaData();
						int cols = md.getColumnCount();
						for (int i = 1; i < cols; i++) {
							String name = md.getColumnLabel(i);
							if (!silenceMode)
								out.print(name + "\t");
						}
						if (!silenceMode)
							out.println();
						while (rs.next()) {
							for (int i = 1; i < cols; i++) {
								String value = rs.getString(i);
								if (!silenceMode)
									out.print(value + "\t");
							}
							if (!silenceMode)
								out.println("");
						}
					}

					command = null;
					try {
						statement.close();
					} catch (Exception e) {
					}
					Thread.yield();
				} else {
					command.append(line);
					command.append(" ");
				}
			}
			if (!autoCommit) {
				conn.commit();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			out.println("Error executing: " + command);
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			out.println("Error executing: " + command);
			throw e;
		} finally {
			// if (!conn.getAutoCommit())
			// conn.rollback();

		}
	}
	
	public static void printUsage() {
		System.out.println("Usage: use one of the following argements\n" +
				"\tcreate_schema\tCreate all tables. Note that this will remove all existing tables in the DB.\n" +
				"\tcreate_index\tCreate index.\n" +
				"\tinsert_all\tInsert all log (.tsv) files including archive files described in database.properties file.\n" +
				"\tinsert_tsv\tInsert tsv files only.\n" +
				"\tinsert_archives\tInsert archive files only.");
	}

	public static void main(String... args) {
		Configurations configurations = new Configurations();
		try {
			Configuration conf = configurations.properties(Constants.SCHEMA_DIR
					+ Constants.CONFIG);
			String output = conf.getString("db.log", Constants.DEFAULT_LOGGING);
			boolean isComplete = true;
			System.out.println("Insertion progress will be updated in " + output);
			DBConstructor db = new DBConstructor(output);
			db.connect(conf.getString("db.host", Constants.DEFAULT_HOST),
					conf.getInt("db.port", Constants.DEFAULT_PORT),
					conf.getString("db.database", Constants.DEFAULT_DB),
					conf.getString("db.user", Constants.DEFAULT_USER),
					conf.getString("db.password", Constants.DEFAULT_PASSWORD));

			db.archive_log_dir = conf.getStringArray("log.archive_dir");

			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("create_schema")) {
					db.createSchema();
				} else if (args[0].equalsIgnoreCase("create_index")) {
					db.createIndex();
				} else if (args[0].equalsIgnoreCase("insert_all")) {
					db.insertAllLog();
				} else if (args[0].equalsIgnoreCase("insert_tsv")) {
					db.insertLog();
				} else if (args[0].equalsIgnoreCase("insert_archives")) {
					db.insertArchive();
				} else {
					isComplete = false;
				}
			} else {
				isComplete = false;
			}
			

			db.close();
			if(isComplete)
				System.out.println("Complete!");
			else
				printUsage();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
