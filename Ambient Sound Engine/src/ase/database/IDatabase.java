package ase.database;


import java.util.SortedMap;

import ase.models.SoundModel;
import ase.models.SoundscapeModel;

/**
 * <p>Defines the Database methods / ops used by the Ambient Sound Engine
 * to be implemented by concrete databases. Using an interface allows
 * for Database implementations to be swapped out as needed.</p>
 * 
 * <p>Extends AutoCloseable so it can be used in try-with-resources</p>
 * 
 * <p><b>NOTE:</b> As of 12/31/2017, interface defined with only a few getter
 * methods. This interface must be expanded to meet full needs of ASE, but
 * limited for now to be able to get an up-and-running instance of the full
 * ASE for Testing</p>
 * 
 * @author Kevin
 *
 */
public interface IDatabase extends AutoCloseable {
	/**
	 * Connects to the database.
	 * 
	 * @throws DatabaseException if there is an issue with the connection
	 */
	void connect() throws DatabaseException;
	
	/**
	 * Retrieves keywords using the identifier as the prefix search term
	 * @param identifier Used as prefix keyword search
	 * @return map of Keyword Id (from database) to keyword
	 * @throws DatabaseException
	 */
	SortedMap<Integer, String> getKeywords(String identifier) throws DatabaseException;
	
	/**
	 * Retrieves settings corresponding to search term. Pass empty string to retrieve
	 * all settings
	 * @param identifier
	 * @return
	 * @throws DatabaseException
	 */
	SortedMap<String, String> getSettings(String identifier) throws DatabaseException;
	
	/**
	 * Searches DB for keywords associated with sound files
	 * 
	 * @param identifier Used as prefix keyword search
	 * @return Map of Keyword Id to Keyword
	 * @throws DatabaseException
	 */
	SortedMap<Integer, String> getSoundKeywords(String identifier) throws DatabaseException;
	
	/**
	 * Searches DB for sound name based on keyword parameter.
	 * 
	 * @param keywordId Id of keyword to search for sound relationships
	 * @return Map of Sound Id to Sound Name
	 * @throws DatabaseException
	 */
	SortedMap<Integer, String> getSoundsByKeyword(int keywordId) throws DatabaseException;
	
	/**
	 * Searches DB for keywords associated with soundscapes
	 * 
	 * @param identifier Used as prefix keyword search
	 * @return Map of Keyword ID to Keyword
	 * @throws DatabaseException
	 */
	SortedMap<Integer, String> getSoundscapeKeywords(String identifier) throws DatabaseException;
	
	/**
	 * Searches DB for soundscape based on keyword parameter.
	 * 
	 * @param keywordIdId of keyword to search for soundscape relationships
	 * @return Map of Soundscape ID to Soundscape Name
	 * @throws DatabaseException
	 */
	SortedMap<Integer, String> getSoundscapesByKeyword(int keywordId) throws DatabaseException;
	
	/**
	 * Retrieves an individual Sound from the database. Sound not attached to any particular soundscape,
	 * so context-specific metadata (playtype, volume, etc) set to defaults
	 * @param soundId Database ID of sound to be retrieved
	 * @return SoundModel representing the sound
	 * @throws DatabaseException if soundId retrieves more or less than 1 sound
	 */
	SoundModel getSoundById(int soundId) throws DatabaseException;
	
	/**
	 * Retrieves a soundscape from the database, including all its associated sounds and context-specific
	 * metadata (playtype, volume, etc).
	 * @param ssId Soundscape Id
	 * @return SoundscapeModel representing the soundscape (including all sounds associated with soundscape)
	 * @throws DatabaseException if ssId retrieves more or less than 1 soundscape
	 */
	SoundscapeModel getSoundscapeById(int ssId) throws DatabaseException;
}
