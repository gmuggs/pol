package edu.gmu.mason.vanilla.db;

/**
 * General description_________________________________________________________
 * An interface used for DB collection constants
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public interface Constants {
	public static final String BASE_URL = "jdbc:postgresql://{0}:{1}/{2}?user={3}&password={4}";
	public static final String DEFAULT_HOST = "localhost";
	public static final int DEFAULT_PORT = 5432;
	public static final String DEFAULT_DB = "challenge1";
	public static final String DEFAULT_USER = "postgres";
	public static final String DEFAULT_PASSWORD = "postgres";

	public static final String DEFAULT_SOCIAL_NETWORK = "target/FriendFamilyGraph.dgs";
	public static final String SCHEMA_DIR = "dbschema/";
	public static final String LOG_DIR = "logs/";
	public static final String CONFIG = "database.properties";
	public static final int MAX_HEADER_SIZE = 1024;
	public static final String DEFAULT_LOGGING = "DBConstructor.log";
}
