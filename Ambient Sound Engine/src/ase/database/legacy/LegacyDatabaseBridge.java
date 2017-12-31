package ase.database.legacy;

import java.sql.SQLException;
import java.util.SortedMap;

import ase.database.Database;
import ase.database.DatabaseException;
import ase.database.IDatabase;
import ase.models.SoundModel;
import ase.models.SoundscapeModel;

public class LegacyDatabaseBridge extends Database {
	private final LegacyDatabase db;
	
	public LegacyDatabaseBridge () throws DatabaseException {
		super();
		db = new LegacyDatabase();
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
	public SortedMap<Integer, String> getKeyword(String identifier) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SortedMap<Integer, String> getSoundByKeyword(String identifier) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SortedMap<Integer, String> getSoundscapeByKeyword(String identifier) throws DatabaseException {
		// TODO Auto-generated method stub
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
