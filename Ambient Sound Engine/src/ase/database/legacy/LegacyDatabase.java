package ase.database.legacy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Vector;

import ase.database.DataType;
import ase.database.DatabaseException;
import ase.operations.Log;

import static ase.operations.OperationsManager.opsMgr;
import static ase.operations.Log.LogLevel.*;

/**
 * <p>Handles the majority of the statements to the SQLite database, any SQLite calls
 * outside of this file were added by David due to an attempt to expediate the
 * process.</p>
 * 
 * <h4>History</h4>
 * <ul>
 * 	<li>11/26/2015 - CKidwell - Updated to version 3, expanded the Sound table to 
 * 	add 3 new fields and changed the copyright field to be related to the index
 * 	of the {@link SoundObject#comboBoxOptions}.</li>
 * 	<li>07/09/2016 - CKidwell - Refactored some calls to try with resources to remove
 * 	potential memory leaks.</li>
 * 	<li>12/31/2017 - Kevin C Gall - Refactor Database class into LegacyDatabase. As of this date, only
 * 	migrating small handful of methods. Will migrate more as needed by the refactor of the ASE.</li>
 * </ul>
 * 
 * TODO: Replace sequential selects with join selects
 * TODO: Replace more stuff with prepared statements, close resource leaks
 * 
 * @author Lance
 * @author CKidwell
 * @author Kevin C Gall
 */
public class LegacyDatabase { // connects to sqlLite database
	//logger
	private static final Log logger = opsMgr.logger;
	
	private static final String DRIVER = "org.sqlite.JDBC";
	private static final String DATABASE = "jdbc:sqlite:./soundEngine.db";
	private Connection connection = null;
	private boolean connectionStatus;
	private static final int CURRENT_VERSION = 4;

	private PreparedStatement searchSoundKeywords;
	private PreparedStatement searchSoundsByKeyword;
	private PreparedStatement searchSoundscapeKeywords;
	private PreparedStatement searchSoundscapesByKeyword;
	private PreparedStatement loadSoundFileData;
	private PreparedStatement loadSoundscapeData;
	private PreparedStatement loadSoundscapeSoundsData;
	private PreparedStatement insertSoundscape;
	private PreparedStatement updateSoundscape;
	private PreparedStatement insertSoundscapeSounds;
	private PreparedStatement deleteSoundscapeSounds;
	private PreparedStatement insertKeyword;
	private PreparedStatement insertSoundscapeKeyword;
	private PreparedStatement getMostRecentSoundscapeAdded;
	private PreparedStatement getMostRecentSoundAdded;
	private PreparedStatement getKeywordId;
	private PreparedStatement getSettingCount;
	private PreparedStatement updateSetting;
	private PreparedStatement getSetting;

	//SETUP / TEARDOWN METHODS
	
	/** Creates the database object */
	public LegacyDatabase() {
		// really does nothing but set connectionStatus to false
		connectionStatus = false;
	}

	/**
	 * Attempts to connect to database, returns success / fail to caller
	 * Connects to database with preset username, password, database info
	 * 
	 * @return boolean, connection status after attempting to connect
	 */
	public boolean connect() throws SQLException{

		try {
			// load database driver class
			logger.log(DEBUG, "trying to connect");
			Class.forName(DRIVER);
			// connect to database
			connection = DriverManager.getConnection(DATABASE);
			logger.log(DEBUG, "connection ok!");
			connectionStatus = true; // connection OK!
		} catch (ClassNotFoundException e) {
			logger.log(DEV, e.toString());
			logger.log(DEBUG, e.getStackTrace());
			connectionStatus = false;
			// failed to connect - maybe missing the mySQL library jar in the
			// lib directory?
		} catch (SQLException e) {
			logger.log(DEV, e.toString());
			logger.log(DEBUG, e.getStackTrace());
			connectionStatus = false; // failed to connect
			// Database problem, might not be running, might not exist
		}
		
		if(connectionStatus) {
			int version = 0;
			try(Statement statement = connection.createStatement()){
				ResultSet rs = statement.executeQuery("SELECT * FROM version");
				version = 0;
				while(rs.next()){
					version = rs.getInt(1);
				}
				rs.close();
				
				if(version < CURRENT_VERSION){
					int retCode = this.updateDb(version);
					if(retCode > 0) {
						//Maybe some error handling code here
					}
				}
			} catch (SQLException e) {
				if(e.getMessage().contains("no such table")){
					logger.log(PROD, "Creating new db");
					//Assume brand new db
					this.updateDb(version);
				} else {
					logger.log(DEV, e.getMessage());
					logger.log(DEBUG, e.getStackTrace());
					connectionStatus = false;
				}
			}
		}
		
		//loading prepared statements here
		loadPreparedStatements();
		
		return connectionStatus;
	} // end of connect()
	
	public static void main(String[] args) {
		LegacyDatabase db = new LegacyDatabase();
		try {
			db.connect();
			
			ResultSet testResults = db.getSearchPrefixMatch("bowl", DataType.SOUND);
			
			while (testResults.next()) {
				logger.log(PROD, testResults.getString(1));
				logger.log(PROD, "" + testResults.getInt(2));
			}
			
		} catch (SQLException | DatabaseException ex) {
			logger.log(DEBUG, "You're basically fucked");
		}
		
		db.disconnect();
	}

	/**
	 * test db connection, return success / failure to caller
	 * 
	 * @return boolean, status of the connection.
	 */
	public boolean isConnected() {
		if (connection != null) {
			try {
				connectionStatus = !(connection.isClosed());
			} catch (SQLException e) {
				logger.log(DEV, e.getMessage());
				logger.log(DEBUG, e.getStackTrace());
			}
		} else
			connectionStatus = false;
		return connectionStatus;
	}

	/**
	 * attempts to disconnect from mySQL, checks if the connection is closed
	 * then returns the status.
	 * 
	 * @return boolean, status of the connection after disconnect attempt
	 */
	public boolean disconnect() {
		try {
			connection.close();
		} catch (SQLException e) {
			logger.log(DEV, e.getMessage());
			logger.log(DEBUG, e.getStackTrace());
		}
		try {
			connectionStatus = connection.isClosed();
		} catch (SQLException e) {
			logger.log(DEV, e.getMessage());
			logger.log(DEBUG, e.getStackTrace());
		}
		if (connectionStatus == true) {
			logger.log(PROD, "*** Database connection successfully closed ***");
		} else {
			logger.log(PROD, "*** ERROR CLOSING DATABASE CONNECTION! ***");
		}

		return connectionStatus;
	}
	
	/**
	 * Attempts to update all database structures to the latest version.
	 * When updating to a new version, update {@link #CURRENT_VERSION}
	 * @param version The version of the database you are updating from
	 * @return 0 if successful
	 */
	private int updateDb(int version) {
		try{
			if(version == 0) {
				Statement statement = connection.createStatement();
				
				statement.executeUpdate("drop table if exists keyword");
				statement.executeUpdate("CREATE TABLE keyword ( " +
						"keyword_id integer primary key, " +
						"keyword text NOT NULL UNIQUE)");
				
				statement.executeUpdate("drop table if exists sound_file");
				statement.executeUpdate("CREATE TABLE sound_file (" +
						" sound_file_id integer primary key," +
						" name text NOT NULL unique," +
						" description text NOT NULL default ''," +
						" data_file text NOT NULL," +
						" length text NOT NULL default ''," +
						" owner text NOT NULL default ''," +
						" copyright text NOT NULL default 'n'," +
						" file_path text default NULL," +
						" file_size integer)");
				
				statement.executeUpdate("DROP TABLE IF EXISTS sound_file_keyword");
				statement.executeUpdate("CREATE TABLE sound_file_keyword (" +
						" sound_file_keyword_id integer primary key," +
						" sound_file_id integer NOT NULL," +
						" keyword_id integer NOT NULL," +
						" CONSTRAINT UK01_sound_file_keyword UNIQUE (sound_file_id,keyword_id)," +
						" CONSTRAINT FK01_sound_file_keyword_sound_file FOREIGN KEY (sound_file_id) REFERENCES sound_file (sound_file_id)," +
						" CONSTRAINT FK02_sound_file_keyword_keyword FOREIGN KEY (keyword_id) REFERENCES keyword (keyword_id))");
				
				statement.executeUpdate("DROP TABLE IF EXISTS soundscape");
				statement.executeUpdate("CREATE TABLE soundscape ( " +
						" soundscape_id integer primary key," +
						" name text NOT NULL unique," +
						" description text NOT NULL default ''," +
						" fmod_volume integer default NULL)");
				
				statement.executeUpdate("DROP TABLE IF EXISTS soundscape_keyword");
				statement.executeUpdate("CREATE TABLE soundscape_keyword (" +
						" soundscape_keyword_id integer primary key," +
						" soundscape_id integer unsigned NOT NULL default '0'," +
						" keyword_id integer unsigned NOT NULL default '0'," +
						" CONSTRAINT UK01_soundscape_keyword UNIQUE (soundscape_id,keyword_id)," +
						" CONSTRAINT FK01_soundscape_keyword_soundscape FOREIGN KEY (soundscape_id) REFERENCES soundscape (soundscape_id)," +
						" CONSTRAINT FK02_soundscape_keyword_keyword FOREIGN KEY (keyword_id) REFERENCES keyword (keyword_id))");
				
				statement.executeUpdate("DROP TABLE IF EXISTS soundscape_sound");
				statement.executeUpdate("CREATE TABLE soundscape_sound (" +
						" soundscape_sound_id integer primary key," +
						" soundscape_id integer unsigned NOT NULL default '0'," +
						" sound_file_id integer unsigned NOT NULL default '0'," +
						" level integer unsigned NOT NULL default '0'," +
						" wipe text default NULL," +
						" fmod_volume integer default NULL," +
						" playback_mode integer default NULL," +
						" min_repeat smallinteger default '1'," +
						" max_repeat smallinteger default '1'," +
						" min_repeat_time smallinteger default '1'," +
						" max_repeat_time smallinteger default '1'," +
						" CONSTRAINT UK01_soundscape_sound UNIQUE (soundscape_id,sound_file_id)," +
						" CONSTRAINT FK01_soundscape_sound_soundscape FOREIGN KEY (soundscape_id) REFERENCES soundscape (soundscape_id)," +
						" CONSTRAINT FK02_soundscape_sound_sound_file FOREIGN KEY (sound_file_id) REFERENCES sound_file (sound_file_id))");
				
				statement.executeUpdate("DROP VIEW IF EXISTS sscape_keyword_search");
				statement.executeUpdate("CREATE VIEW if not exists sscape_keyword_search as " +
						  "select distinct ss.name AS name, s.soundscape_id AS soundscape_id, k.keyword_id AS keyword_id, k.keyword AS keyword " +
						  "FROM soundscape_sound s JOIN sound_file_keyword f JOIN keyword k JOIN soundscape ss " +
						  "ON s.sound_file_id =  f.sound_file_id AND f.keyword_id = k.keyword_id AND ss.soundscape_id = s.soundscape_id " +
						  "UNION " +
						  "SELECT ss.name AS name, s.soundscape_id AS soundscape_id, k.keyword_id AS keyword_id, k.keyword AS keyword " +
						  "FROM soundscape_keyword s JOIN keyword k JOIN soundscape ss " +
						  "ON s.keyword_id = k.keyword_id AND ss.soundscape_id = s.soundscape_id ");
				
				statement.executeUpdate("DROP VIEW IF EXISTS sound_file_keyword_index_view");
				statement.executeUpdate("CREATE VIEW sound_file_keyword_index_view AS " +
						"select keyword.keyword AS keyword, " +
						"sound_file.sound_file_id AS sound_file_id, " +
						"sound_file.name AS name " +
						"from ((sound_file_keyword join sound_file on((sound_file_keyword.sound_file_id = sound_file.sound_file_id))) " +
						"join keyword on((keyword.keyword_id = sound_file_keyword.keyword_id))) " +
						"order by keyword.keyword,sound_file.name");
				
				statement.executeUpdate("DROP VIEW IF EXISTS sound_file_keyword_view");
				statement.executeUpdate("CREATE VIEW sound_file_keyword_view AS " +
						" select sf.name AS Sound_File," +
						" k.keyword AS Keyword" +
						" from ((sound_file sf join sound_file_keyword sfk on((sf.sound_file_id = sfk.sound_file_id)))" +
						" join keyword k on((sfk.keyword_id = k.keyword_id)))" +
						" order by sf.name,k.keyword");
				
				statement.executeUpdate("DROP VIEW IF EXISTS soundscape_keyword_basic_view");
				statement.executeUpdate("CREATE VIEW soundscape_keyword_basic_view AS " +
						"select ss.name AS Soundscape, " +
						"k.keyword AS Keyword " +
						"from ((soundscape ss join soundscape_keyword sk on((ss.soundscape_id = sk.soundscape_id))) " +
						"join keyword k on((sk.keyword_id = k.keyword_id))) " +
						"order by ss.name,k.keyword");
				
				statement.executeUpdate("DROP VIEW IF EXISTS soundscape_keyword_index_view");
				statement.executeUpdate("CREATE VIEW soundscape_keyword_index_view AS " +
						"select keyword.keyword AS keyword, " +
						"soundscape.soundscape_id AS soundscape_id, " +
						"soundscape.name AS name " +
						"from ((soundscape_keyword join soundscape on((soundscape_keyword.soundscape_id = soundscape.soundscape_id))) " +
						"join keyword on((keyword.keyword_id = soundscape_keyword.keyword_id))) " +
						"order by keyword.keyword,soundscape.name");
				
				statement.executeUpdate("DROP VIEW IF EXISTS soundscape_keyword_view");
				statement.executeUpdate("CREATE VIEW soundscape_keyword_view AS " +
						"select ss.name AS Soundscape, " +
						"k.keyword AS Keyword " +
						"from ((((soundscape ss join soundscape_sound sss on((ss.soundscape_id = sss.soundscape_id))) " +
						"join sound_file sf on((sss.sound_file_id = sf.sound_file_id))) " +
						"join sound_file_keyword sfk on((sf.sound_file_id = sfk.sound_file_id))) " +
						"join keyword k on((sfk.keyword_id = k.keyword_id))) " +
						"union select ss.name AS Soundscape, " +
						"k.keyword AS Keyword " +
						"from ((soundscape ss join soundscape_keyword sk on((ss.soundscape_id = sk.soundscape_id))) " +
						"join keyword k on((sk.keyword_id = k.keyword_id))) " +
						"order by Soundscape, Keyword"); 
				
				statement.executeUpdate("DROP VIEW IF EXISTS soundscape_sound_view");
				statement.executeUpdate("CREATE VIEW soundscape_sound_view AS " +
						"select ss.name AS Soundscape, " +
						"sf.name AS Sound_File, " +
						"sss.level AS Level, " +
						"sss.wipe AS Wipe, " +
						"sf.copyright AS Copyright " +
						"from ((soundscape ss join soundscape_sound sss on((ss.soundscape_id = sss.soundscape_id))) " +
						"join sound_file sf on((sss.sound_file_id = sf.sound_file_id))) " +
						"order by ss.name,sf.name");
				
	
				statement.executeUpdate("drop table if exists version");
				statement.executeUpdate("create table version(version integer)");
				statement.executeUpdate("insert into version values(1)");
				
				version = 1;
			}
			if(version == 1) {
				Statement statement = connection.createStatement();
				
				statement.executeUpdate("DROP TABLE IF EXISTS SETTINGS");
				statement.executeUpdate("CREATE TABLE SETTINGS(" +
						" SETTING text not null unique," +
						" VALUE text)");
				
				statement.executeUpdate("update version set version = 2");
				version = 2;
			}
			if (version == 2) {
				Statement statement = connection.createStatement();
				
				statement.executeUpdate("ALTER TABLE sound_file "
						+ "add column "
						+ "IMPORTEDBY text ");
				
				statement.executeUpdate("ALTER TABLE sound_file "
						+ "add column "
						+ "source text ");
				
				statement.executeUpdate("ALTER TABLE sound_file "
						+ "add column "
						+ "EDITEDBY text");
				
				statement.executeUpdate("UPDATE sound_file "
						+ "set IMPORTEDBY = '', "
						+ "source = '', "
						+ "EditedBy = ''");
				
				statement.executeUpdate("UPDATE sound_file "
						+ "set copyright = '0' "
						+ "where copyright = 'y'");

				statement.executeUpdate("UPDATE sound_file "
						+ "set copyright = '1' "
						+ "where copyright = 'n'");
				
				statement.executeUpdate("update version set version = 3");
				
				version = 3;
			}
			if (version == 3) {
				Statement statement = connection.createStatement();
				
				//add a sound_file_keyword_search view mimicking the sscape_keyword_search view
				statement.executeUpdate("DROP VIEW IF EXISTS sound_keyword_search");
				statement.executeUpdate(
					"CREATE VIEW if not exists sound_keyword_search as " +
							" select sf.name AS Sound_File, sf.sound_file_id as sound_file_id, " +
							" k.keyword AS Keyword, k.keyword_id as keyword_id" +
							" from ((sound_file sf join sound_file_keyword sfk on((sf.sound_file_id = sfk.sound_file_id)))" +
							" join keyword k on((sfk.keyword_id = k.keyword_id)))" +
							" order by sf.name, k.keyword");
				
				statement.executeUpdate("update version set version = 4");
				
				version = 4;
			}
		} catch(SQLException e) {
		
			e.printStackTrace();
			return -1;
		}
		return 0;
	}
	
	/**
	 * loads prepared statements into the database so that they may be executed
	 * multiple times, efficiently, needs explanations on statements still.
	 */
	private void loadPreparedStatements() throws SQLException{
		if (isConnected()) {
			searchSoundKeywords = connection
					.prepareStatement("SELECT DISTINCT Keyword, keyword_id FROM sound_keyword_search WHERE Keyword like ?");
			searchSoundsByKeyword = connection
					.prepareStatement("SELECT DISTINCT Sound_File, sound_file_id FROM sound_keyword_search WHERE Keyword_id = ?");
			searchSoundscapeKeywords = connection
					.prepareStatement("SELECT DISTINCT keyword, keyword_id FROM sscape_keyword_search WHERE keyword like ?");
			searchSoundscapesByKeyword = connection
					.prepareStatement("SELECT DISTINCT name, soundscape_id FROM sscape_keyword_search WHERE keyword_id = ?");
			loadSoundFileData = connection
					.prepareStatement("SELECT name, data_file, file_path, file_size FROM sound_file WHERE sound_file_id = ?");
			loadSoundscapeData = connection
					.prepareStatement("SELECT name,description,fmod_volume FROM soundscape WHERE soundscape_id = ?");
			loadSoundscapeSoundsData = connection
					.prepareStatement("SELECT soundscape_sound.sound_file_id, sound_file.name," +
							  " sound_file.data_file, sound_file.file_path, soundscape_sound.fmod_volume," +
							  " soundscape_sound.playback_mode, sound_file.file_size, " +
							  " soundscape_sound.min_repeat, soundscape_sound.max_repeat, soundscape_sound.min_repeat_time, " +
							  " soundscape_sound.max_repeat_time" +
							  " FROM soundscape_sound " +
							  " LEFT JOIN sound_file ON (soundscape_sound.sound_file_id = sound_file.sound_file_id)" +
							  " WHERE soundscape_sound.soundscape_id =?");
			insertSoundscape = connection
					.prepareStatement("INSERT INTO soundscape (name, description, fmod_volume) VALUES (?,?,?)");
			updateSoundscape = connection
					.prepareStatement("UPDATE soundscape SET fmod_volume = ? WHERE soundscape_id = ?");
			insertSoundscapeSounds = connection
					.prepareStatement("INSERT INTO soundscape_sound"
							+ " (soundscape_id, sound_file_id,"
							+ " fmod_volume, playback_mode, min_repeat,"
							+ " max_repeat, min_repeat_time, max_repeat_time)"
							+ " VALUES (?,?,?,?,?,?,?,?)");
			deleteSoundscapeSounds = connection
					.prepareStatement("DELETE FROM soundscape_sound WHERE soundscape_id = ?");
			getMostRecentSoundscapeAdded = connection
					.prepareStatement("SELECT MAX(soundscape_id) FROM soundscape");
			insertKeyword = connection
					.prepareStatement("INSERT INTO keyword (keyword) VALUES (?)");
			insertSoundscapeKeyword = connection
					.prepareStatement("INSERT INTO soundscape_keyword (soundscape_id, keyword_id) VALUES (?,?)");
			getMostRecentSoundAdded = connection
					.prepareStatement("SELECT MAX(sound_file_id) FROM sound_file");
			getKeywordId = connection
					.prepareStatement("SELECT keyword_id FROM keyword WHERE keyword = ?");
			getSettingCount = connection
					.prepareStatement("SELECT COUNT(*) FROM settings WHERE setting = ?");
			updateSetting = connection
					.prepareStatement("UPDATE settings SET value = ? WHERE setting = ?");
			getSetting = connection
					.prepareStatement("SELECT value FROM settings WHERE setting = ?");
		}
	}
	
	//PUBLIC QUERY METHODS
	
	/**
	 * Searches for keywords narrowed by association either with sounds or soundscapes.
	 * @param prefix Search term
	 * @param type Must be either SOUND or SOUNDSCAPE.
	 * @return ResultSet from query
	 * @throws DatabaseException if type is not SOUND or SOUNDSCAPE
	 * @throws SQLException
	 */
	public ResultSet getSearchPrefixMatch(String prefix, DataType type) throws DatabaseException, SQLException {
		ResultSet results = null;
		
		String prefixWithWildcard = prefix + "%";
		
		switch(type) {
			case SOUND:
				searchSoundKeywords.setString(1, prefixWithWildcard);
				results = searchSoundKeywords.executeQuery();
				break;
			case SOUNDSCAPE:
				searchSoundscapeKeywords.setString(1, prefixWithWildcard);
				results = searchSoundscapeKeywords.executeQuery();
				break;
			default:
				throw new DatabaseException("Invalid data type for prefix matching");
		}
		
		return results;
	}
	
	/**
	 * Finds the sounds or soundscapes associated with a given keyword 
	 * @param keywordId
	 * @param type
	 * @return The result set including the sound or soundscape name and its integer id.
	 * @throws DatabaseException
	 * @throws SQLException
	 */
	public ResultSet getNarrowedSearchByKeyword(int keywordId, DataType type) throws DatabaseException, SQLException {
		ResultSet results = null;
		
		switch(type) {
			case SOUND:
				searchSoundsByKeyword.setInt(1, keywordId);
				results = searchSoundsByKeyword.executeQuery();
				break;
			case SOUNDSCAPE:
				searchSoundscapesByKeyword.setInt(1, keywordId);
				results = searchSoundscapesByKeyword.executeQuery();
				break;
			default:
				throw new DatabaseException("Invalid data type for prefix matching");
		}
		
		return results;
	}
	
	/**
	 * Retrieves data about a sound outside of the context of a Soundscape
	 * @param soundId
	 * @return
	 * @throws SQLException
	 */
	public ResultSet getSoundData(int soundId) throws SQLException {
		loadSoundFileData.setInt(1, soundId);
		
		return loadSoundFileData.executeQuery();
	}
	
	/**
	 * Retrieves soundscape data excluding soundscape sound data
	 * @param soundscapeId
	 * @return
	 * @throws SQLException
	 */
	public ResultSet getSoundscapeData(int soundscapeId) throws SQLException {
		loadSoundscapeData.setInt(1, soundscapeId);
		return loadSoundscapeData.executeQuery();
	}
	
	/**
	 * Retrievs soundscape sound data
	 * @param soundscapeId
	 * @return
	 * @throws SQLException
	 */
	public ResultSet getSoundscapeSoundData(int soundscapeId) throws SQLException {
		loadSoundscapeSoundsData.setInt(1, soundscapeId);
		return loadSoundscapeSoundsData.executeQuery();
	}
	
	public ResultSet getSetting(String setting) throws SQLException {
		getSetting.setString(1, setting);
		return getSetting.executeQuery();
	}
	
	//PUBLIC SAVE METHODS
	
	/**
	 * Saves a new soundscape and returns its ssId
	 * @param name
	 * @param volume Should be integer from 0 to 100
	 * @return
	 * @throws SQLException
	 * @throws DatabaseException If save was successful, but could not retrieve ssId
	 */
	public int saveNewSoundscape(String name, int volume) throws SQLException, DatabaseException {
		insertSoundscape.setString(1, name);
		insertSoundscape.setString(2,  "");
		insertSoundscape.setInt(3, volume);
		
		insertSoundscape.executeUpdate();
		
		ResultSet results = getMostRecentSoundscapeAdded.executeQuery();
		
		if (results.next()) {
			return results.getInt(1);
		}
		
		throw new DatabaseException("Could not retrieve new SSID");
	}
	
	/**
	 * Saves a sound to a specific soundscape with soundscape-specific settings
	 * @param ssid
	 * @param soundFileId
	 * @param volume Integer from 0 to 100
	 * @param playbackMode 0 for Loop, 1 for Single, 2 for Random
	 * @param minRepeats
	 * @param maxRepeats
	 * @param minDelay
	 * @param maxDelay
	 * @throws SQLException
	 */
	public void saveSoundscapeSound
		(int ssid, int soundFileId, int volume, int playbackMode, int minRepeats, int maxRepeats, int minDelay, int maxDelay)
		throws SQLException
	{
		insertSoundscapeSounds.setInt(1, ssid);
		insertSoundscapeSounds.setInt(2, soundFileId);
		insertSoundscapeSounds.setInt(3, volume);
		insertSoundscapeSounds.setInt(4, playbackMode);
		insertSoundscapeSounds.setInt(5, minRepeats);
		insertSoundscapeSounds.setInt(6, maxRepeats);
		insertSoundscapeSounds.setInt(7, minDelay);
		insertSoundscapeSounds.setInt(8, maxDelay);
		
		insertSoundscapeSounds.executeUpdate();
	}
	
	//PUBLIC UPDATE METHODS
	
	/**
	 * 
	 * @param ssid
	 * @param volume Integer from 0 to 100
	 * @throws SQLException
	 */
	public void updateSoundscape(int ssid, int volume) throws SQLException {
		updateSoundscape.setInt(1, volume);
		updateSoundscape.setInt(2, ssid);
		
		updateSoundscape.executeUpdate();
	}
	
	public void setSetting(String key, String value) throws SQLException {
		getSettingCount.setString(1, key);
		ResultSet rs = getSettingCount.executeQuery();
		
		int count = 0;
		if (rs.next()) {
			count = rs.getInt(1);
		}
		
		if (count != 1) {
			//Shouldn't be more than one, delete them and then just insert a new one
			PreparedStatement deletePs = connection.prepareStatement("DELETE FROM settings where setting = ?");
			deletePs.setString(1, key);
			deletePs.executeUpdate();
			deletePs.close();
			
			PreparedStatement insertPs = connection.prepareStatement("INSERT INTO settings (setting, value) values (?,?)");
			insertPs.setString(1, key);
			insertPs.setString(2, value);
			insertPs.executeUpdate();
			insertPs.close();
		} else {
			updateSetting.setString(1, value);
			updateSetting.setString(2, key);
			updateSetting.executeUpdate();
		}
	}
	
	//PUBLIC DELETE METHODS
	
	/**
	 * Deletes all sound associations with a soundscape
	 * @param ssid
	 * @throws SQLException
	 */
	public void deleteSoundscapeSounds(int ssid) throws SQLException {
		deleteSoundscapeSounds.setInt(1, ssid);
		
		deleteSoundscapeSounds.executeUpdate();
	}
	
	//PRIVATE DB METHODS
	
	public void addKeywordToSoundscape(int ssid, String keyword) throws SQLException, DatabaseException {
		getKeywordId.setString(1, keyword);
		
		ResultSet results = getKeywordId.executeQuery();
		
		int keywordId = -1;
		if (results.next()) {
			keywordId = results.getInt(1);
		}
		
		if (keywordId < 0) {
			insertKeyword.setString(1, keyword);
			
			insertKeyword.executeUpdate();
			results = getKeywordId.executeQuery();
			
			if (results.next()) {
				keywordId = results.getInt(1);
			} else {
				throw new DatabaseException("Unable to save new keyword");
			}
		}
		
		insertSoundscapeKeyword.setInt(1, ssid);
		insertSoundscapeKeyword.setInt(2, keywordId);
		
		insertSoundscapeKeyword.executeUpdate();
	}
}



















































