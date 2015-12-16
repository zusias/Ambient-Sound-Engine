package ase_source;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

/**
 * Handles the majority of the statements to the mySQL database, any mySQL calls
 * outside of this file were added by David due to an attempt to expediate the
 * process.
 * 
 * <br>
 * <br>
 * 11/26/2015 - CKidwell - Updated to version 3, expanded the Sound table to 
 * add 3 new fields and changed the copyright field to be related to the index
 * of the {@link SoundObject#comboBoxOptions.
 * 
 * @author Lance
 * @author CKidwell
 */
public class Database { // connects to mySQL database
	private static final String DRIVER = "org.sqlite.JDBC";
	private static final String DATABASE = "jdbc:sqlite:./soundEngine.db";
	private Connection connection = null;
	private boolean connectionStatus;
	private static final int CURRENT_VERSION = 3;

	static final int SOUND = 1;
	static final int SOUNDSCAPE = 2;

	private PreparedStatement loadKeywordFromName;
	private PreparedStatement loadSoundFileData;
	private PreparedStatement loadSoundscapeData;
	private PreparedStatement loadSoundscapeSoundsData;
	private PreparedStatement loadSoundscape;
	private PreparedStatement loadSoundFile;
	private PreparedStatement getMostRecentSoundscapeAdded;
	private PreparedStatement getMostRecentSoundAdded;
	private PreparedStatement deleteSoundscapeFromSystem;
	private PreparedStatement deleteKeywordFromSystem;
	private PreparedStatement deleteSoundFileFromSoundscape;
	private PreparedStatement deleteAllSoundFilesFromSoundscape;
	private PreparedStatement deleteSoundFileFromAllSoundscapes;
	private PreparedStatement deleteKeywordFromSoundscape;
	private PreparedStatement deleteKeywordFromAllSoundscapes;
	private PreparedStatement deleteAllKeywordsFromSoundscape;
	private PreparedStatement deleteKeywordFromSoundFile;
	private PreparedStatement deleteKeywordFromAllSoundFiles;
	private PreparedStatement deleteAllKeywordsFromSoundFile;

	/** Creates the database object */
	public Database() {
		// really does nothing but set connectionStatus to false
		connectionStatus = false;
	}

	/**
	 * Attempts to connect to database, returns success / fail to caller
	 * Connects to database with preset username, password, database info
	 * 
	 * @return boolean, connection status after attempting to connect
	 */
	public boolean connect() {

		try {
			// load database driver class
			EnvVariables.logMessage("trying to connect");
			Class.forName(DRIVER);
			// connect to database
			connection = DriverManager.getConnection(DATABASE);
			EnvVariables.logMessage("connection ok!");
			connectionStatus = true; // connection OK!
		} catch (ClassNotFoundException e) {
			EnvVariables.logMessage(e.toString());
			e.printStackTrace();
			connectionStatus = false;
			// failed to connect - maybe missing the mySQL library jar in the
			// lib directory?
		} catch (SQLException e) {
			EnvVariables.logMessage(e.toString());
			e.printStackTrace();
			connectionStatus = false; // failed to connect
			// Database problem, might not be running, might not exist
		}
		
		if(connectionStatus) {
			Statement statement;
			int version = 0;
			try{
				statement = connection.createStatement();
				ResultSet rs = statement.executeQuery("select * from version");
				version = 0;
				while(rs.next()){
					version = rs.getInt(1);
				}
				
				if(version < CURRENT_VERSION){
					int retCode = this.updateDb(version);
					if(retCode > 0) {
						//Maybe some error handling code here
					}
				}
			} catch (SQLException e) {
				if(e.getMessage().contains("no such table")){
					System.out.println("Creating new db");
					//Assume brand new db
					this.updateDb(version);
				} else {
					System.out.println("Unknown error");
					e.printStackTrace();
					connectionStatus = false;
				}
			}
		}
		return connectionStatus;
	} // end of connect()
	
	public static void main(String[] args) {
		Database db = new Database();
		db.connect();
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
				e.printStackTrace();
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
			e.printStackTrace();
		}
		try {
			connectionStatus = connection.isClosed();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (connectionStatus == true)
			System.out
					.println("*** Database connection successfully closed ***");
		else
			System.out.println("*** ERROR CLOSING DATABASE CONNECTION! ***");

		return connectionStatus;
	}
	
	public PreparedStatement getInsertSoundForSS(){
		PreparedStatement ps = null;
		try{
			ps = connection.prepareStatement("INSERT INTO soundscape_sound (soundscape_id, sound_file_id, level, "
						+ "wipe, fmod_volume, playback_mode, min_repeat, max_repeat, min_repeat_time, max_repeat_time) VALUES (?,?,?,?,?,?,?,?,?,?)");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ps;
	}

	/**
	 * loads prepared statements into the database so that they may be executed
	 * multiple times, efficiently, needs explanations on statements still.
	 */
	public void loadPreparedStatements() {
		if (isConnected()) {
			try {

				// Used by the search bar to find keywords that match to any
				// sounds or soundscapes
				// A search for "thin" will return the keyword "thing"
				
				// When a keyword is chosen, this finds the sounds or
				// soundscapes that it has been matched to

				// Loading data about keywords, sounds, or soundscapes
				loadKeywordFromName = connection
						.prepareStatement("SELECT keyword_id, keyword FROM keyword WHERE keyword = ?");
				loadSoundFileData = connection
						.prepareStatement("SELECT name, data_file, file_path, file_size FROM sound_file WHERE sound_file_id = ?");
				loadSoundscapeData = connection
						.prepareStatement("SELECT name,description,fmod_volume FROM soundscape WHERE soundscape_id =?");
				loadSoundscapeSoundsData = connection
						.prepareStatement("SELECT soundscape_sound.sound_file_id, sound_file.name," +
								  " sound_file.data_file, sound_file.file_path, soundscape_sound.fmod_volume," +
								  " soundscape_sound.playback_mode, sound_file.file_size, " +
								  " soundscape_sound.min_repeat, soundscape_sound.max_repeat, soundscape_sound.min_repeat_time, soundscape_sound.max_repeat_time" +
								  " FROM soundscape_sound " +
								  " LEFT JOIN sound_file ON (soundscape_sound.sound_file_id = sound_file.sound_file_id)" +
								  " WHERE soundscape_sound.soundscape_id =?");
				loadSoundscape = connection
						.prepareStatement("SELECT soundscape_id, name, description, fmod_volume FROM soundscape WHERE name = ?");
				loadSoundFile = connection
						.prepareStatement("SELECT sound_file_id, name, description, data_file, length, owner, copyright, file_path, file_size, IMPORTEDBY, source, EDITEDBY FROM sound_file WHERE name = ?");
				
				// Used as a convenience function to let the user quickly add
				// keywords to the most recent item added
				getMostRecentSoundscapeAdded = connection
						.prepareStatement("SELECT MAX(soundscape_id) FROM soundscape");
				getMostRecentSoundAdded = connection
						.prepareStatement("SELECT MAX(sound_file_id) FROM sound_file");

				// For removing data elements and their connections
				deleteSoundscapeFromSystem = connection
						.prepareStatement("DELETE FROM soundscape WHERE soundscape_id = ?");
				deleteKeywordFromSystem = connection
						.prepareStatement("DELETE FROM keyword WHERE keyword_id = ?");

				deleteSoundFileFromSoundscape = connection
						.prepareStatement("DELETE FROM soundscape_sound WHERE soundscape_id = ? AND sound_file_id = ?");
				deleteAllSoundFilesFromSoundscape = connection
						.prepareStatement("DELETE FROM soundscape_sound WHERE soundscape_id = ?");
				deleteSoundFileFromAllSoundscapes = connection
						.prepareStatement("DELETE FROM soundscape_sound WHERE sound_file_id = ?");

				deleteKeywordFromSoundscape = connection
						.prepareStatement("DELETE FROM soundscape_keyword WHERE soundscape_id =  ? AND keyword_id = ?");
				deleteKeywordFromAllSoundscapes = connection
						.prepareStatement("DELETE FROM soundscape_keyword WHERE keyword_id = ?");
				deleteAllKeywordsFromSoundscape = connection
						.prepareStatement("DELETE FROM soundscape_keyword WHERE soundscape_id = ?");

				deleteKeywordFromSoundFile = connection
						.prepareStatement("DELETE FROM sound_file_keyword WHERE sound_file_id = ? and keyword_id = ?");
				deleteKeywordFromAllSoundFiles = connection
						.prepareStatement("DELETE FROM sound_file_keyword WHERE keyword_id = ?");
				deleteAllKeywordsFromSoundFile = connection
						.prepareStatement("DELETE FROM sound_file_keyword WHERE sound_file_id = ?");

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Drops the temporary soundscape table from the database so it can be
	 * recreated without an issue
	 */
	public void dropTable() {
		Statement statement;
		try {
			statement = connection.createStatement();
			statement.executeUpdate("DROP view IF EXISTS sscape_keyword_search");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * First, attempts to unpair this keyword from any soundscapes and sound
	 * files it is associated with, then deletes the keyword passed in from the
	 * mySQL database.
	 * 
	 * @param keyword
	 *            A string of the keyword to be deleted from the mySQL database
	 */
	public void deleteKW(String keyword) {
		int kwID;
		kwID = keywordIdFromString(keyword);
		try {
			deleteKeywordFromAllSoundFiles.setInt(1, kwID);
			deleteKeywordFromAllSoundFiles.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			deleteKeywordFromAllSoundscapes.setInt(1, kwID);
			deleteKeywordFromAllSoundscapes.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			deleteKeywordFromSystem.setInt(1, kwID);
			deleteKeywordFromSystem.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Attempts to remove this sound from any pairings with keywords or
	 * soundscapes, then attempts to remove the sound from the database
	 * completely. Because only a String is passed in, any two sound files given
	 * the same name will cause a problem, there is no check for this currently.
	 * 
	 * @param sound
	 *            A string representation of the sound file to be removed from
	 *            the mySQL database
	 */
	public void deleteSound(String sound) {
		int sfxID;
		Statement statement;
		sfxID = soundFileIdFromString(sound);
		try {
			deleteAllKeywordsFromSoundFile.setInt(1, sfxID);
			deleteAllKeywordsFromSoundFile.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			deleteSoundFileFromAllSoundscapes.setInt(1, sfxID);
			deleteSoundFileFromAllSoundscapes.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			statement = connection.createStatement();
			statement.executeUpdate("DELETE FROM sound_file WHERE name = '"
					+ sound + "'");
			// deleteSoundFileFromSystem.setString(1,sound);
			// deleteSoundFileFromSystem.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Attempts to remove this soundscape from any pairings with keywords or
	 * sounds, then attempts to remove the soundscape from the database
	 * completely. Because only a String is passed in, any two sound files given
	 * the same name will cause a problem, there is no check for this currently.
	 * 
	 * @param scape
	 *            The String representation of the name of the soundscape to be
	 *            deleted
	 */
	public void deleteScape(String scape) {
		int ssID;
		ssID = soundscapeIdFromString(scape);
		try {
			deleteAllKeywordsFromSoundscape.setInt(1, ssID);
			deleteAllKeywordsFromSoundscape.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			deleteAllSoundFilesFromSoundscape.setInt(1, ssID);
			deleteAllSoundFilesFromSoundscape.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			deleteSoundscapeFromSystem.setInt(1, ssID);
			deleteSoundscapeFromSystem.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Attempts to remove the pairing of Keyword and Sound File from the mySQL
	 * database
	 * 
	 * @param soundFile
	 *            String representation of the sound file's name
	 * @param keyword
	 *            String representation of the keyword
	 */
	public void deleteKeywordSoundFile(String soundFile, String keyword) {
		int soundFileID = 0, kwID = 0;

		soundFileID = soundFileIdFromString(soundFile);
		kwID = keywordIdFromString(keyword);
		try {
			deleteKeywordFromSoundFile.setInt(1, soundFileID);
			deleteKeywordFromSoundFile.setInt(2, kwID);
			deleteKeywordFromSoundFile.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Attempts to remove the pairing of a Sound File and Soundscape from the
	 * mySQL database
	 * 
	 * @param soundscape
	 *            String representation of the soundscape's name
	 * @param soundFile
	 *            String representation of the Sound File's name
	 */
	public void deleteSoundFromSoundscape(String soundscape, String soundFile) {
		int soundscapeID = 0, soundFileID = 0;

		soundscapeID = soundscapeIdFromString(soundscape);
		soundFileID = soundFileIdFromString(soundFile);

		try {
			deleteSoundFileFromSoundscape.setInt(1, soundscapeID);
			deleteSoundFileFromSoundscape.setInt(2, soundFileID);
			deleteSoundFileFromSoundscape.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Attempts to remove the pairing of Keyword and Soundscape from the mySQL
	 * database
	 * 
	 * @param soundscape
	 *            String representation of the soundscape's name
	 * @param keyword
	 *            String representation of the keyword
	 */
	public void deleteKwFromSoundscape(String soundscape, String keyword) {
		int soundscapeID = 0, kwID = 0;

		soundscapeID = soundscapeIdFromString(soundscape);
		kwID = keywordIdFromString(keyword);

		try {
			deleteKeywordFromSoundscape.setInt(1, soundscapeID);
			deleteKeywordFromSoundscape.setInt(2, kwID);
			deleteKeywordFromSoundscape.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Updates the existing soundscape with the new sounds in the soundscape,
	 * does this by deleting all sounds currently associated in the DB then
	 * placing all current sounds into the database.
	 * 
	 * @param soundscape
	 *            An existing soundscape that will have it's sounds and volume
	 *            levels updated.
	 */
	public void saveExistingSoundscape(Soundscape soundscape) {
		int ssID = soundscape.getSoundscapeID();
		int sound_file_id, level;
		String wipe;
		int fmod_volume, playback_mode;
		int minRepeatTimes, maxRepeatTimes, minRepeatDelay, maxRepeatDelay;
		try {
			deleteAllSoundFilesFromSoundscape.setInt(1, ssID);
			deleteAllSoundFilesFromSoundscape.executeUpdate();
			Statement statement;
			statement = connection.createStatement();
			statement.executeUpdate("UPDATE soundscape SET fmod_volume="
					+ Integer.toString(soundscape.getMasterVolume())
					+ " WHERE soundscape_id=" + Integer.toString(ssID));
			for (int count = 0; count < soundscape.getSoundscapeSoundsCount(); count++) {
				sound_file_id = soundscape.getSound(count).getSoundID();
				level = 5;
				wipe = "y";
				fmod_volume = soundscape.getSound(count).getVolume();
				playback_mode = soundscape.getSound(count).getPlaybackType();
				minRepeatTimes = soundscape.getSound(count).getMinNumLoops();
				maxRepeatTimes = soundscape.getSound(count).getMaxNumLoops();
				minRepeatDelay = soundscape.getSound(count).getMinRepeatDelay();
				maxRepeatDelay = soundscape.getSound(count).getMaxRepeatDelay();
				
				PreparedStatement ps = getInsertSoundForSS();

				ps.setInt(1, ssID);
				ps.setInt(2,sound_file_id);
				ps.setInt(3, level);
				ps.setString(4, wipe);
				ps.setInt(5, fmod_volume);
				ps.setInt(6,playback_mode);
				ps.setInt(7,minRepeatTimes);
				ps.setInt(8,maxRepeatTimes);
				ps.setInt(9,minRepeatDelay);
				ps.setInt(10,maxRepeatDelay);

				ps.executeUpdate();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Inserts a new soundscape into the db, then adds in the sounds currently
	 * being used
	 * 
	 * @param soundscape
	 * @return The ID of the new soundscape
	 */
	public int saveNewSoundScape(Soundscape soundscape) {
		ResultSet resultSet;
		int newSSID = 0;
		String name, description;
		int fmod_volume;
		int minRepeatTimes, maxRepeatTimes, minRepeatDelay, maxRepeatDelay;

		name = soundscape.getSSName();
		description = "";
		fmod_volume = soundscape.getMasterVolume();

		try {
			PreparedStatement ps = connection
					.prepareStatement("INSERT INTO soundscape (name, description, fmod_volume) VALUES (?,?,?)");
			ps.setString(1, name);
			ps.setString(2, description);
			ps.setInt(3, fmod_volume);
			ps.executeUpdate();

			resultSet = getMostRecentSoundscapeAdded.executeQuery();
			while (resultSet.next()) {
				newSSID = resultSet.getInt(1);
			}
			int sound_file_id, level;
			String wipe;
			int playback_mode;

			for (int count = 0; count < soundscape.getSoundscapeSoundsCount(); count++) {
				sound_file_id = soundscape.getSound(count).getSoundID();
				level = 5;
				wipe = "y";
				fmod_volume = soundscape.getSound(count).getVolume();
				playback_mode = soundscape.getSound(count).getPlaybackType();
				minRepeatTimes = soundscape.getSound(count).getMinNumLoops();
				maxRepeatTimes = soundscape.getSound(count).getMaxNumLoops();
				minRepeatDelay = soundscape.getSound(count).getMinRepeatDelay();
				maxRepeatDelay = soundscape.getSound(count).getMaxRepeatDelay();

				PreparedStatement ps2 = getInsertSoundForSS();
				
				ps2.setInt(1, newSSID);
				ps2.setInt(2, sound_file_id);
				ps2.setInt(3, level);
				ps2.setString(4, wipe);
				ps2.setInt(5, fmod_volume);
				ps2.setInt(6, playback_mode);
				ps2.setInt(7, minRepeatTimes);
				ps2.setInt(8, maxRepeatTimes);
				ps2.setInt(9, minRepeatDelay);
				ps2.setInt(10, maxRepeatDelay);

				ps2.executeUpdate();
			}
			
			if(keywordIdFromString(name) == 0) {
				addKeywordIntoSystem(name);
			}
			addKeywordToSoundscape(name, name);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return newSSID;
	}
	
	/**
	 * Inserts this record into the database as a sound record. Does not copy the
	 * actual sound data into the database, and so the sound file must remain on
	 * the system at the same location.
	 * @param fileName The file name on the system including extension
	 * @param filePath The save path for imported sounds, not the source file path.
	 * @param soundName The sound that will show in the
	 * @param size file size, not sure why we care
	 * @param description AKA Notes field
	 * @param importedBy The user that imported the sound
	 * @param copyright Copyright status {@link SoundObject#comboBoxOptions}
	 * @param sourceDesc The source of the sound
	 * @param createdBy Who created the sound
	 * @param editedBy Who edited the sound
	 */
	public void addSoundFileIntoSystem(String fileName, String filePath,
			String soundName, long size, String description, String importedBy,
			String copyright, String sourceDesc, String createdBy,
			String editedBy) {
		StringBuffer pathBuffer = new StringBuffer(filePath);
		int length = pathBuffer.length();

		int i;
		for (i = 0; i < length; ++i) {
			if (pathBuffer.charAt(i) == '\\')
				pathBuffer.setCharAt(i, '/');
		} // for()

		filePath = pathBuffer.toString();
		try {
			PreparedStatement ps = connection.prepareStatement(
					"INSERT INTO sound_file (name, description, data_file, "
					+ "owner, copyright, file_path, file_size, "
					+ "importedBy, source, editedBy) "
					+ "VALUES (?,?,?,?,?,?,?,?,?,?)");
			ps.setString(1, soundName);//name
			ps.setString(2, description);//description
			ps.setString(3, fileName);//data_file
			ps.setString(4, createdBy);//owner
			ps.setString(5, copyright);//copyright
			ps.setString(6, filePath);//file_path
			ps.setLong(7, size);//file_size
			ps.setString(8, importedBy);
			ps.setString(9, sourceDesc);
			ps.setString(10, editedBy);
			
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds the specified keyword into the keyword table
	 * 
	 * @param keyword
	 */
	public void addKeywordIntoSystem(String keyword) {

		// TODO: Check to make sure the keyword isn't already in the db
		if ((keywordIdFromString(keyword) == 0)) {
			try {
				PreparedStatement ps = connection.prepareStatement("INSERT INTO keyword (keyword) VALUES (?)");
				ps.setString(1, keyword);
				ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {

		}
	}

	/**
	 * Gets the soundscape's ID from the database
	 * 
	 * @param searchString Soundscape name
	 * @return int, the id number
	 */
	public int soundscapeIdFromString(String searchString) {
		Statement statement;
		ResultSet resultSet;
		int id;
		try {
			statement = connection.createStatement();
			resultSet = statement
					.executeQuery("SELECT soundscape_id FROM soundscape WHERE name = '"
							+ searchString + "'");
			resultSet.next();
			id = resultSet.getInt(1);
			return id;
		} catch (SQLException sx) {
			System.out.println(sx);
		}
		return 0;
	}

	/**
	 * Takes in a string and performs a mySQL query that returns an integer, the
	 * ID of the sound file.
	 * 
	 * @param searchString
	 *            the String name of the sound file in question.
	 * @return int, the id number of the sound file in the mySQL database.
	 */
	public int soundFileIdFromString(String searchString) {
		Statement statement;
		ResultSet resultSet;
		int id;
		try {
			statement = connection.createStatement();
			resultSet = statement
					.executeQuery("SELECT sound_file_id FROM sound_file WHERE name = '"
							+ searchString + "'");
			resultSet.next();
			id = resultSet.getInt(1);
			return id;
		} catch (SQLException sx) {
			System.out.println(sx);
		}
		return 0;
	}

	/**
	 * takes in a String for the keyword's name and returns its ID number. If
	 * the keyword does not exist, it returns 0. This number is used in adding a
	 * keyword to a sound, and adding a keyword to a soundscape, it also needs
	 * to be used for error checking to see if a keyword being added to the
	 * database is already there. (Not implemented yet)
	 * 
	 * @param searchString
	 *            the String name of the keyword desired.
	 * @return int, the id number of the keyword in the mySQL database.
	 */
	public int keywordIdFromString(String searchString) {
		Statement statement;
		ResultSet resultSet;
		int id;
		try {
			statement = connection.createStatement();
			resultSet = statement
					.executeQuery("SELECT keyword_id FROM keyword WHERE keyword = '"
							+ searchString + "'");
			resultSet.next();
			id = resultSet.getInt(1);
			return id;
		} catch (SQLException sx) {
			System.out.println(sx);
			return 0;
		}
	}

	/**
	 * Takes in a String for the keyword, and then adds that keyword to the last
	 * sound file added to the database. The sound file does not necessarily
	 * need to be added in this session, you should be able to close the program
	 * completely and it should still properly find the "last" sound file.
	 * 
	 * @param keyword
	 *            The keyword to add to this sound file
	 */
	public void addKeywordToLastSoundfile(String keyword) {
		ResultSet resultSet;
		int newSoundID = 0, kwID = 0;

		try {
			resultSet = getMostRecentSoundAdded.executeQuery();
			while (resultSet.next()) {
				newSoundID = resultSet.getInt(1);
			}

			kwID = keywordIdFromString(keyword);

			PreparedStatement ps = connection
					.prepareStatement("INSERT INTO sound_file_keyword (sound_file_id, keyword_id) VALUES (?,?)");
			ps.setInt(1, newSoundID);
			ps.setInt(2, kwID);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds a keyword to the sound file with the specified name
	 * 
	 * @param soundFile
	 *            The name of the sound
	 * @param keyword
	 *            The keyword to add
	 */
	public void addKeywordToSoundFile(String soundFile, String keyword) {
		int soundFileID = 0, kwID = 0;

		soundFileID = soundFileIdFromString(soundFile);
		kwID = keywordIdFromString(keyword);

		try {
			PreparedStatement ps = connection.prepareStatement("INSERT INTO sound_file_keyword (sound_file_id, keyword_id) VALUES (?,?)");
			ps.setInt(1, soundFileID);
			ps.setInt(2, kwID);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds a keyword to a soundscape
	 * 
	 * @param soundscape The name of the soundscape, not the ID
	 * @param keyword The keyword to add, not the keyword's ID
	 */
	public void addKeywordToSoundscape(String soundscape, String keyword) {
		int soundscapeID = 0, kwID = 0;

		soundscapeID = soundscapeIdFromString(soundscape);
		kwID = keywordIdFromString(keyword);

		try {
			PreparedStatement ps = connection.prepareStatement("INSERT INTO soundscape_keyword (soundscape_id, keyword_id) VALUES(?,?)");
			ps.setInt(1, soundscapeID);
			ps.setInt(2, kwID);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds a sound to a soundscape
	 * 
	 * @param soundscape
	 * @param soundFile
	 */
	public void addSoundFileToSoundscape(String soundscape, String soundFile) {
		int soundscapeID = 0, soundFileID = 0;

		soundscapeID = soundscapeIdFromString(soundscape);
		soundFileID = soundFileIdFromString(soundFile);
		
		

		try {
			PreparedStatement ps = getInsertSoundForSS();
			
			ps.setInt(1, soundscapeID);
			ps.setInt(2, soundFileID);
			ps.setInt(3, 5);
			ps.setString(4, "y");
			ps.setInt(5, 50);
			ps.setInt(6, 0);
			ps.setInt(7, 1);
			ps.setInt(8, 1);
			ps.setInt(9, 1);
			ps.setInt(10, 1);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads a sound from the database
	 * 
	 * @param resultSet
	 *            Result set of the sound table, only the sound for the current
	 *            row will be returned
	 * @return A sound object for the current row of the result set
	 */
	public SoundObject getSoundObjectFromResultSet(ResultSet resultSet) {

		SoundObject newSound = null;

		try {
			int soundFileID = resultSet.getInt(1);
			String soundName = resultSet.getString(2);
			String path = resultSet.getString(4) + resultSet.getString(3);
			int soundLevel = resultSet.getInt(5);
			int playbackMode = resultSet.getInt(6);
			int fileSize = resultSet.getInt(7);
			int minRepeat = resultSet.getInt(8);
			int maxRepeat = resultSet.getInt(9);
			int minRepeatTime = resultSet.getInt(10);
			int maxRepeatTime = resultSet.getInt(11);

			newSound = new SoundObject(soundFileID, soundName, path,
					soundLevel, playbackMode, fileSize, minRepeat, maxRepeat,
					minRepeatTime, maxRepeatTime);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return newSound;
	}

	/**
	 * Load a soundscape into the main window
	 * 
	 * @param ssID
	 *            The chosen soundscape's ID
	 * @param consoleID
	 *            Indicates whether to load the soundscape into the top or
	 *            bottom console
	 * @return The soundscape loaded
	 */
	public Soundscape loadSoundscape(int ssID, int consoleID) {
		ResultSet resultSet = null;
		Soundscape soundscape = null;
		SoundObject newSound = null;

		String name = "";
		String desc = "";
		int fmodVolume = 0;

		try {
			loadSoundscapeData.setInt(1, ssID);
			resultSet = loadSoundscapeData.executeQuery();
			while (resultSet.next()) {
				name = resultSet.getString(1);
				desc = resultSet.getString(2);
				fmodVolume = resultSet.getInt(3);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		soundscape = new Soundscape(ssID, consoleID, name, desc, fmodVolume);

		try {
			loadSoundscapeSoundsData.setInt(1, ssID);
			resultSet = loadSoundscapeSoundsData.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			if (resultSet != null) {
				while (resultSet.next()) {
					newSound = getSoundObjectFromResultSet(resultSet);
					if (newSound == null)
						System.out.println("Trying to add null sound!");
					soundscape.addSound(newSound);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return soundscape;
	}

	public ResultSet getSoundData(String _name) {
		ResultSet resultSet = null;
		try {
			loadSoundFile.setString(1, _name);
			resultSet = loadSoundFile.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultSet;
	}

	public ResultSet getSoundscapeData(String _name) {
		ResultSet resultSet = null;
		try {
			loadSoundscape.setString(1, _name);
			resultSet = loadSoundscape.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return resultSet;
	}

	public ResultSet getKeywordData(String _name) {
		ResultSet resultSet = null;
		try {
			loadKeywordFromName.setString(1, _name);
			resultSet = loadKeywordFromName.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultSet;
	}

	/**
	 * Updates the given sound ID's data in the database to all given parameters.
	 * @param id The ID of the sound record to update
	 * @param soundName The new sound name
	 * @param notes The new description/notes for the sound
	 * @param dataFile File name for the sound
	 * @param length never really used properly, nor is it needed, but it's supposed 
	 * to be sound length (in milliseconds)
	 * @param owner The owner of the sound file
	 * @param copyrightIndex the index of the sound file's copyright status {@link SoundObject#comboBoxOptions}
	 * @param path The path to the parent directory. Can be relative to the execution
	 * environment, or absolute.
	 * @param fileSize File size in bytes. 
	 * @param importedBy The person that imported the sound into the program
	 * @param sourceDesc The source the sound file was obtained from
	 * @param editedBy The person that edited the sound
	 */
	public void setSoundData(int id, String soundName, String notes,
			String dataFile, String length, String owner, String copyrightIndex,
			String path, int fileSize, String importedBy, String sourceDesc,
			String editedBy) {
		PreparedStatement statement;
		try {
			statement = connection.prepareStatement("UPDATE sound_file "
					+ "SET name = ?, " //1
					+ "description = ?, " //2
					+ "data_file = ?, " //3
					+ "length = ?, " //4
					+ "owner = ?, " //5 
					+ "copyright = ?, " //6
					+ "file_path = ?, " //7
					+ "file_size = ?, " //8
					+ "importedBy = ?, " //9
					+ "source = ?, " //10
					+ "editedBy = ? " //11
					+ "WHERE sound_file_id = ?"); //12
			statement.setString(1, soundName);
			statement.setString(2, notes);
			statement.setString(3, dataFile);
			statement.setString(4, length);
			statement.setString(5, owner);
			statement.setString(6, copyrightIndex);
			statement.setString(7, path);
			statement.setInt(8, fileSize);
			statement.setString(9, importedBy);
			statement.setString(10, sourceDesc);
			statement.setString(11, editedBy);
			statement.setInt(12, id);
			
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Updates a soundscape's data in the database
	 * 
	 * @param _id
	 * @param _name
	 * @param _description
	 * @param _volume
	 */
	public void setSoundscapeData(int _id, String _name, String _description,
			int _volume) {
		Statement statement;
		try {
			statement = connection.createStatement();
			statement.executeUpdate("UPDATE soundscape SET name = '" + _name
					+ "', description = '" + _description + "', fmod_volume = "
					+ Integer.toString(_volume) + " WHERE soundscape_id = "
					+ Integer.toString(_id));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Lets you change the keyword, keeps all the old associations, 
	 * TODO: Check that the new keyword doesn't already exist
	 * 
	 * @param _id
	 * @param _kw
	 */
	public void setKeywordData(int _id, String _kw) {
		Statement statement;
		try {
			statement = connection.createStatement();
			statement.executeUpdate("UPDATE keyword SET keyword = '" + _kw
					+ "' WHERE keyword_id = " + Integer.toString(_id));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns a sound object for the given ID
	 * 
	 * @param soundID
	 * @return
	 */
	public SoundObject getSoundObject(int soundID) {
		ResultSet resultSet = null;
		SoundObject newSound = null;
		String name, path;
		int fileSize;

		try {
			loadSoundFileData.setInt(1, soundID);
			resultSet = loadSoundFileData.executeQuery();
			while (resultSet.next()) {
				name = resultSet.getString(1);
				path = resultSet.getString(3) + resultSet.getString(2);
				fileSize = resultSet.getInt(4);

				newSound = new SoundObject(soundID, name, path, fileSize);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return newSound;
	}

	/**
	 * Searches for keywords belonging to sounds or soundscapes that also begin
	 * with the prefix
	 * 
	 * @param dbResults
	 *            This vector will be overwritten with the results
	 * @param prefix
	 *            The keyword to search for, "burg" will return "burger" but not
	 *            "hamburger"
	 * @param mode
	 *            SOUND || SOUNDSCAPE
	 */
	public void getSearchPrefixMatch(Vector<ResultObject> dbResults,
			String prefix, int mode) {
		ResultSet resultSet = null;
		ResultObject newObject;
		// String query;

		prefix += "%";
		switch (mode) {
		case SOUND:
			System.out.println("**** Sound Mode *****");
			try {
				PreparedStatement ps = connection.prepareStatement("select distinct keyword from sound_file_keyword_index_view where keyword like ?");
				ps.setString(1, prefix);
				resultSet = ps.executeQuery();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			break;
		case SOUNDSCAPE:
			System.out.println("**** Soundscape Mode *****");
			try {
				PreparedStatement ps = connection.prepareStatement("select distinct keyword from sscape_keyword_search where keyword like ?");
				ps.setString(1, prefix);
				resultSet = ps.executeQuery();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			break;
		default:

			System.out.println("Error 193");
		}

		try {

			while (resultSet.next()) {
				newObject = new ResultObject(resultSet.getString(1));
				dbResults.add(newObject);

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the sounds or soundscapes associated with a chosen keyword
	 * 
	 * @param dbResults
	 *            This vector will be overwritten with the results
	 * @param keyword
	 *            The keyword to find results for
	 * @param mode
	 *            SOUND || SOUNDSCAPE
	 */
	public void getNarrowedSearchResults(Vector<ResultObject> dbResults,
			String keyword, int mode) {
		ResultSet resultSet = null;
		ResultObject newObject;

		switch (mode) {
		case SOUND:
			try {
				PreparedStatement ps = connection
						.prepareStatement("select distinct sound_file_id,name from sound_file_keyword_index_view where keyword =?");
				ps.setString(1, keyword);
				resultSet = ps.executeQuery();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			break;
		case SOUNDSCAPE:
			try {
				PreparedStatement ps = connection
						.prepareStatement("select distinct soundscape_id,name from sscape_keyword_search where keyword = ?");
				ps.setString(1, keyword);
				resultSet = ps.executeQuery();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			break;
		default:

			System.out.println("Error 163");
		}

		try {

			while (resultSet.next()) {

				newObject = new ResultObject(resultSet.getInt(1), resultSet
						.getString(2));
				dbResults.add(newObject);

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Selects all records for the named column from the given table and returns
	 * a Vector
	 * 
	 * @param tableName
	 *            Table to select from
	 * @param columnName
	 *            The column name to select
	 * @return
	 */
	public Vector<String> showTable(String tableName, String columnName) {
		Vector<String> tableList = new Vector<String>();
		Statement statement;
		ResultSet resultSet;

		try {
			statement = connection.createStatement();
			// Eww, sql injection, but it's not like this is a client facing web
			// application
			resultSet = statement.executeQuery("SELECT " + columnName
					+ " FROM " + tableName);
			while (resultSet.next()) {
				tableList.addElement(resultSet.getString(columnName));
			} // while()
		} catch (SQLException sx) {
		}

		return tableList;
	} // showTable()

	/**
	 * Returns keywords associated with the specified soundscape
	 * 
	 * @param searchString
	 * @return
	 */
	public Vector<String> getSoundscapeKeywords(String searchString) {
		Vector<String> tableList = new Vector<String>();
		Statement statement;
		ResultSet resultSet;

		int id;
		try {
			statement = connection.createStatement();
			resultSet = statement
					.executeQuery("SELECT soundscape_id FROM soundscape WHERE name = '"
							+ searchString + "'");
			resultSet.next();
			id = resultSet.getInt(1);

			String command = "SELECT keyword.keyword FROM (keyword, soundscape_keyword, soundscape) "
					+ "WHERE (keyword.keyword_id = soundscape_keyword.keyword_id) "
					+ "AND (soundscape_keyword.soundscape_id = soundscape.soundscape_id) "
					+ "AND soundscape.soundscape_id ="
					+ id
					+ " ORDER BY keyword.keyword";

			resultSet = statement.executeQuery(command);
			while (resultSet.next()) {
				tableList.addElement(resultSet.getString("keyword"));
			} // while()
		} catch (SQLException sx) {
			System.out.println(sx);
		}

		return tableList;
	} // getSoundScapeKeywords()

	/**
	 * Returns keywords for the sound file specified
	 * 
	 * @param searchString
	 *            The ASE Name for the sound file
	 * @return
	 */
	public Vector<String> getSoundKeywords(String searchString) {
		Vector<String> tableList = new Vector<String>();
		Statement statement;
		ResultSet resultSet;

		int id;
		try {
			statement = connection.createStatement();
			resultSet = statement
					.executeQuery("SELECT sound_file_id FROM sound_file WHERE name = '"
							+ searchString + "'");
			resultSet.next();
			id = resultSet.getInt(1);

			String command = "SELECT keyword.keyword FROM (keyword, sound_file_keyword, sound_file) "
					+ "WHERE (keyword.keyword_id = sound_file_keyword.keyword_id) "
					+ "AND (sound_file_keyword.sound_file_id = sound_file.sound_file_id) "
					+ "AND sound_file.sound_file_id = "
					+ id
					+ " ORDER BY keyword.keyword";

			resultSet = statement.executeQuery(command);
			while (resultSet.next()) {
				tableList.addElement(resultSet.getString("keyword"));
			} // while()
		} catch (SQLException sx) {
			System.out.println(sx);
		}

		return tableList;
	}

	/**
	 * Returns the sounds for a specified soundscape
	 * 
	 * @param searchString
	 * @return
	 */
	public Vector<String> getSoundscapeSounds(String searchString) {
		Vector<String> tableList = new Vector<String>();
		Statement statement;
		ResultSet resultSet;

		int id;
		try {
			statement = connection.createStatement();
			resultSet = statement
					.executeQuery("SELECT soundscape_id FROM soundscape WHERE name = '"
							+ searchString + "'");
			resultSet.next();
			id = resultSet.getInt(1);

			String command = "SELECT sound_file.name FROM (sound_file, soundscape_sound, soundscape) "
					+ "WHERE (sound_file.sound_file_id = soundscape_sound.sound_file_id) "
					+ "AND (soundscape_sound.soundscape_id = soundscape.soundscape_id) "
					+ "AND soundscape.soundscape_id = "
					+ id
					+ " ORDER BY sound_file.name";

			resultSet = statement.executeQuery(command);
			while (resultSet.next()) {
				tableList.addElement(resultSet.getString("name"));
			} // while()
		} catch (SQLException sx) {
			System.out.println(sx);
//			sx.printStackTrace();
		}

		return tableList;
	} // getSoundscapeSounds();

	/**
	 * Finds the soundscapes that a sound belongs to
	 * 
	 * @param searchString
	 * @return
	 */
	public Vector<String> getSoundSoundscapes(String searchString) {
		Vector<String> tableList = new Vector<String>();
		Statement statement;
		ResultSet resultSet;

		int id;
		try {
			statement = connection.createStatement();
			resultSet = statement
					.executeQuery("SELECT sound_file_id FROM sound_file WHERE name = '"
							+ searchString + "'");
			resultSet.next();
			id = resultSet.getInt(1);

			String command = "SELECT soundscape.name FROM (soundscape, soundscape_sound, sound_file) "
					+ "WHERE (soundscape.soundscape_id = soundscape_sound.soundscape_id) "
					+ "AND (soundscape_sound.sound_file_id = sound_file.sound_file_id) "
					+ "AND sound_file.sound_file_id = "
					+ id
					+ " ORDER BY soundscape.name";

			resultSet = statement.executeQuery(command);
			while (resultSet.next()) {
				tableList.addElement(resultSet.getString("name"));
			} // while()
		} catch (SQLException sx) {
			System.out.println(sx);
		}

		return tableList;
	} // getSoundSoundScapes()

	/**
	 * Finds the soundscapes for a given keyword
	 * 
	 * @param searchString
	 * @return
	 */
	public Vector<String> getKeywordSoundscapes(String searchString) {
		Vector<String> tableList = new Vector<String>();
		Statement statement;
		ResultSet resultSet;

		int id;
		try {
			statement = connection.createStatement();
			resultSet = statement
					.executeQuery("SELECT keyword_id FROM keyword WHERE keyword = '"
							+ searchString + "'");
			resultSet.next();
			id = resultSet.getInt(1);

			String command = "SELECT soundscape.name FROM (soundscape, soundscape_keyword, keyword) "
					+ "WHERE (soundscape.soundscape_id = soundscape_keyword.soundscape_id) "
					+ "AND (soundscape_keyword.keyword_id = keyword.keyword_id) "
					+ "AND keyword.keyword_id = "
					+ id
					+ " ORDER BY soundscape.name";

			resultSet = statement.executeQuery(command);
			while (resultSet.next()) {
				tableList.addElement(resultSet.getString("name"));
			} // while()
		} catch (SQLException sx) {
			System.out.println(sx);
		}

		return tableList;
	} // getKeywordSoundscapes()

	/**
	 * Finds the sounds associated with a given keyword
	 * 
	 * @param searchString
	 * @return
	 */
	public Vector<String> getKeywordSounds(String searchString) {
		Vector<String> tableList = new Vector<String>();
		Statement statement;
		ResultSet resultSet;

		int id;
		try {
			statement = connection.createStatement();
			resultSet = statement
					.executeQuery("SELECT keyword_id FROM keyword WHERE keyword = '"
							+ searchString + "'");
			resultSet.next();
			id = resultSet.getInt(1);

			String command = "SELECT sound_file.name FROM (sound_file, sound_file_keyword, keyword) "
					+ "WHERE (sound_file.sound_file_id = sound_file_keyword.sound_file_id) "
					+ "AND (sound_file_keyword.keyword_id = keyword.keyword_id) "
					+ "AND keyword.keyword_id = "
					+ id
					+ " ORDER BY sound_file.name";

			resultSet = statement.executeQuery(command);
			while (resultSet.next()) {
				tableList.addElement(resultSet.getString("name"));
			} // while()
		} catch (SQLException sx) {
			System.out.println(sx);
//			sx.printStackTrace();
		}

		return tableList;
	} // getKeywordSounds()
	
	public String getSetting(String setting) {
		String value = null;
		PreparedStatement ps;
		ResultSet rs;
		
		try{
			ps = connection.prepareStatement("SELECT value FROM SETTINGS where SETTING = ?");
			ps.setString(1, setting);
			rs = ps.executeQuery();
			
			if(rs.next()) {
				value = rs.getString(1);
			}
		} catch (SQLException sx) {
			System.out.println(sx);
		}
		
		return value;
	}
	
	public void setSetting(String key, String value) {
		PreparedStatement ps;
		ResultSet rs;
		int count=0;
		
		try{
			ps = connection.prepareStatement("SELECT count(*) from SETTINGS where setting = ?");
			ps.setString(1, key);
			rs = ps.executeQuery();
			if(rs.next()){
				count = rs.getInt(1);
			}
			ps.close();
			
			if(count == 0) {
				PreparedStatement ps2 = connection.prepareStatement("INSERT into SETTINGS (setting, value) values (?,?)");
				ps2.setString(1, key);
				ps2.setString(2, value);
				ps2.executeUpdate();
				ps2.close();
			} else if (count > 1){
				//Shouldn't be more than one, delete them and then just insert a new one;
				PreparedStatement ps2 = connection.prepareStatement("DELETE from SETTINGS where setting = ?");
				ps2.setString(1, key);
				ps2.executeUpdate();
				ps2.close();
				
				ps2 = connection.prepareStatement("INSERT into SETTINGS (setting, value) values (?,?)");
				ps2.setString(1, key);
				ps2.setString(2, value);
				ps2.executeUpdate();
				ps2.close();
			} else {
				ps = connection.prepareStatement("UPDATE SETTINGS set value = ? where setting = ?");
				ps.setString(1, value);
				ps.setString(2, key);
				ps.executeUpdate();
				ps.close();
			}
		} catch (SQLException sx) {
			EnvVariables.logMessage(sx.toString());
		}
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
		} catch(SQLException e) {
		
			e.printStackTrace();
			return -1;
		}
		return 0;
	}
}
