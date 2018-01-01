package ase.database.legacy;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.SortedMap;
import java.util.TreeMap;

import ase.database.DatabaseException;
import ase.database.IDatabase;
import ase.models.SoundModel;
import ase.models.SoundscapeModel;

import static ase.database.DataType.*;

public class LegacyDatabaseBridge implements IDatabase {
	private final LegacyDatabase db;
	
	public LegacyDatabaseBridge() throws DatabaseException {
		db = new LegacyDatabase();
		this.connect();
	}
	
	@Override
	/**
	 * Calls Legacy DB's disconnect method to close the db connection
	 */
	public void close() throws Exception {
		db.disconnect();
	}

	@Override
	/**
	 * @inheritDoc
	 */
	public void connect() throws DatabaseException {
		
		try {
			if (!db.connect()) {
				throw new DatabaseException("Connection not made to database!");
			}
		} catch (SQLException sqlEx) {
			throw new DatabaseException(sqlEx);
		}
	}

	@Override
	/**
	 * @inheritDoc
	 */
	public SortedMap<Integer, String> getKeywords(String identifier) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	/**
	 * @inheritDoc
	 */
	public SortedMap<String, String> getSettings(String identifier) throws DatabaseException {
		//TODO implement
		return null;
	}

	@Override
	/**
	 * @inheritDoc
	 */
	public SortedMap<Integer, String> getSoundKeywords(String identifier) throws DatabaseException {
		ResultSet results;
		TreeMap<Integer, String> keywordById = new TreeMap<>();
		
		try {
			results = db.getSearchPrefixMatch(identifier, SOUND);
			
			while (results.next()) {
				keywordById.put(results.getInt(2), results.getString(1));
			}
		} catch (SQLException sqlEx) {
			throw new DatabaseException(sqlEx);
		}
		
		return keywordById;
	}
	
	@Override
	/**
	 * @inheritDoc
	 */
	public SortedMap<Integer, String> getSoundsByKeyword(int keywordId) throws DatabaseException {
		ResultSet results;
		TreeMap<Integer, String> soundNameById = new TreeMap<>();
		
		
		try {
			results = db.getKeywordSounds(keywordId);
			
			while (results.next()) {
				soundNameById.put(results.getInt(2), results.getString(1));
			}
		} catch (SQLException sqlEx) {
			throw new DatabaseException(sqlEx);
		}
		
		return soundNameById;
	}

	@Override
	/**
	 * @inheritDoc
	 */
	public SortedMap<Integer, String> getSoundscapeKeywords(String identifier) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	/**
	 * @inheritDoc
	 */
	public SortedMap<Integer, String> getSoundscapesByKeyword(int keywordId) throws DatabaseException {
		//TODO implement
		return null;
	}

	@Override
	public SoundModel getSoundById(int soundId) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SoundscapeModel getSoundscapeById(int ssId) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

}
