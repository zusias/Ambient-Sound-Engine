package ase.database.legacy;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import ase.database.DataType;
import ase.database.DatabaseException;
import ase.database.IDatabase;
import ase.models.RandomPlaySettings;
import ase.models.SoundModel;
import ase.models.SoundModel.PlayType;
import ase.models.SoundscapeModel;

import static ase.database.DataType.*;
import static ase.operations.OperationsManager.opsMgr;
import static ase.operations.Log.LogLevel.*;

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
		return getDataKeywords(identifier, SOUND);
	}

	@Override
	/**
	 * @inheritDoc
	 */
	public SortedMap<Integer, String> getSoundscapeKeywords(String identifier) throws DatabaseException {
		return getDataKeywords(identifier, SOUNDSCAPE);
	}
	
	private SortedMap<Integer, String> getDataKeywords(String identifier, DataType type) throws DatabaseException {
		ResultSet results;
		TreeMap<Integer, String> keywordById = new TreeMap<>();
		
		try {
			results = db.getSearchPrefixMatch(identifier, type);
			
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
		return getDataByKeyword(keywordId, SOUND);
	}
	
	@Override
	/**
	 * @inheritDoc
	 */
	public SortedMap<Integer, String> getSoundscapesByKeyword(int keywordId) throws DatabaseException {
		return getDataByKeyword(keywordId, SOUNDSCAPE);
	}
	
	private SortedMap<Integer, String> getDataByKeyword(int keywordId, DataType type) throws DatabaseException {
		ResultSet results;
		TreeMap<Integer, String> dataNameById = new TreeMap<>();
		
		
		try {
			results = db.getNarrowedSearchByKeyword(keywordId, type);
			
			while (results.next()) {
				dataNameById.put(results.getInt(2), results.getString(1));
			}
		} catch (SQLException sqlEx) {
			throw new DatabaseException(sqlEx);
		}
		
		return dataNameById;
	}

	@Override
	/**
	 * @inheritDoc
	 */
	public SoundModel getSoundById(int soundId) throws DatabaseException {
		try {
			ResultSet results = db.getSoundData(soundId);
			SoundModel sound = null;
			
			if (results.next()) {
				String name = results.getString(1);
				String path = results.getString(3) + results.getString(2);
				long fileSize = results.getLong(4);
				
				sound = opsMgr.modelFactory.getSoundWithDefaults(soundId, path, name, fileSize);
			}
			
			return sound;
		} catch(SQLException sqlEx) {
			throw new DatabaseException(sqlEx);
		}
	}

	@Override
	/**
	 * @inheritDoc
	 */
	public SoundscapeModel getSoundscapeById(int ssId) throws DatabaseException {
		try {
			ResultSet soundResults = db.getSoundscapeSoundData(ssId);
			
			Vector<SoundModel> sounds = new Vector<>(); //I don't know how many rows, so use Vector so it can grow
			while (soundResults.next()) {
				RandomPlaySettings rps = new RandomPlaySettings(
						soundResults.getInt(10),
						soundResults.getInt(11),
						soundResults.getInt(8),
						soundResults.getInt(9));
				
				//Construct path from file_path and data_file fields
				Path path = Paths.get(soundResults.getString(4) + soundResults.getString(3));

				//Convert integer representation of volume to double
				double volume = (double) soundResults.getDouble(5) / 100.0;
				
				sounds.add(
					new SoundModel(
						soundResults.getInt(1),
						path,
						soundResults.getString(2),
						PlayType.fromInt(soundResults.getInt(6)),
						true,
						volume,
						soundResults.getLong(7),
						rps));
			}
			
			ResultSet ssResult = db.getSoundscapeData(ssId);
			
			if (ssResult.next()) {
				//Convert integer rep of volume to double
				double volume = (double) ssResult.getInt(3) / 100.0;
				
				//create necessary SoundModel array
				SoundModel[] soundArray = new SoundModel[sounds.size()];
				for (int i = 0; i < sounds.size(); i++) {
					soundArray[i] = sounds.get(i);
				}
				
				return opsMgr.modelFactory.getSoundscape(
					ssId, volume, soundArray, ssResult.getString(1));
			}
			
			return null;
		} catch (SQLException sqlEx) {
			throw new DatabaseException(sqlEx);
		}
	}
	
	@Override
	/**
	 * @inheritDoc
	 */
	public SoundscapeModel saveSoundscape(SoundscapeModel soundscape) throws DatabaseException {
		opsMgr.logger.log(DEV, "Saving soundscape: Name - " + soundscape.name + "; ssid - " + soundscape.ssid);
		
		SoundscapeModel returnSoundscape = soundscape;
		try {
			//update / insert the soundscape
			if (soundscape.ssid == -1) {
				int newSsid = db.saveNewSoundscape(soundscape.name, (int) (100 * soundscape.masterVolume));
				
				returnSoundscape = soundscape.setSsid(newSsid);
				
				opsMgr.logger.log(DEV, "Adding keyword " + soundscape.name + " to soundscape keywords");
				db.addKeywordToSoundscape(returnSoundscape.ssid, returnSoundscape.name);
			} else {
				db.updateSoundscape(soundscape.ssid, (int) (100 * soundscape.masterVolume));
			}
			
			//now update / insert the sounds
			db.deleteSoundscapeSounds(returnSoundscape.ssid);
			
			for (SoundModel sound : returnSoundscape) {
				db.saveSoundscapeSound(
					returnSoundscape.ssid,
					sound.soundId,
					(int) (100 * sound.volume),
					sound.currentPlayType.ordinal(),
					sound.randomSettings.minRepeats,
					sound.randomSettings.maxRepeats,
					sound.randomSettings.minDelay,
					sound.randomSettings.maxDelay);
			}
		} catch (SQLException sqlEx) {
			throw new DatabaseException(sqlEx);
		}
		
		return returnSoundscape;
	}
	
	@Override
	/**
	 * @inheritDoc
	 */
	public void setSetting(String key, String value) throws DatabaseException {
		opsMgr.logger.log(DEV, "Setting setting " + key + " to value " + value);
		
		try {
			db.setSetting(key, value);
		} catch (SQLException sqlEx) {
			throw new DatabaseException(sqlEx);
		}
	}
	
	@Override
	/**
	 * @inheritDoc
	 */
	public String getSetting(String key) throws DatabaseException {
		opsMgr.logger.log(DEV, "Getting setting " + key);
		
		try {
			ResultSet rs = db.getSetting(key);
			
			if (rs.next()) {
				return rs.getString(1);
			}
			
			return null;
		} catch (SQLException sqlEx) {
			throw new DatabaseException(sqlEx);
		}
	}
}
