package ase.bridge;

import ase.operations.SoundModel;
import ase.operations.SoundscapeModel;

/**
 * <p>Abstract interface for a sound engine implementation.
 * Defines methods to load Soundscapes and individual sounds
 * to be played by the sound engine implementation.</p>
 * 
 * <p>Each instance of this class represents one unique sound driver in
 * the current machine. Attempting to instantiate 2 SoundEngine
 * concrete classes for the same soundcard driver is undefined
 * by this interface, but should probably throw an error.</p>
 * 
 * 
 * @author Kevin C. Gall
 *
 */
public abstract class SoundEngine {
	
	/**
	 * Load a soundscape into the engine. Likely a channel or buffer
	 * of some kind. Also reads the Soundscape's state and acts accordingly.
	 * For instance, if play state is "Playing", should play the soundscape.
	 * <br><br>
	 * You can access this soundscape in the sound engine using the 
	 * {@link ase.operations.SoundscapeModel#runtimeId} property
	 * @param ssModel
	 * @return Array of symbols uniquely identifying the sounds in the Soundscape.
	 * This array will be in the order that the sounds were delivered via the
	 * Soundscape's iterator
	 * @throws SoundEngineException
	 */
	public abstract String[] loadSoundscape(SoundscapeModel ssModel) throws SoundEngineException;
	
	/**
	 * Load a sound into an existing Soundscape
	 * @param sModel
	 * @param id Soundscape ID
	 * @return String symbol which is a unique identifier for the sound within the specified
	 * soundscape. This symbol could be arbitrary, but must be unique
	 * @throws SoundEngineException
	 */
	public abstract String loadSound(SoundModel sModel, int id) throws SoundEngineException;
	
	/**
	 * Clear an existing sound from the buffer. Must provide the SoundScape id, and index of the sound.
	 * The index of all sounds with a higher index than this will be shifted left to fill the gap
	 * 
	 * @param id Soundscape ID
	 * @param symbol The symbol returned by the SoundEngine uniquely identifying the sound
	 * @param sModel
	 * @throws SoundEngineException
	 */
	public abstract void clearSound(int id, String symbol) throws SoundEngineException;
	
	/**
	 * Play a sound
	 * @param id Soundscape ID
	 * @param symbol The symbol returned by the SoundEngine uniquely identifying the sound
	 * @throws SoundEngineException
	 */
	public abstract void playSound(int id, String symbol) throws SoundEngineException;
	
	/**
	 * Stop a sound
	 * @param id Soundscape ID
	 * @param symbol The symbol returned by the SoundEngine uniquely identifying the sound
	 * @throws SoundEngineException
	 */
	public abstract void stopSound(int id, String symbol) throws SoundEngineException;
	
	/**
	 * Pause a sound
	 * @param id Soundscape ID
	 * @param symbol The symbol returned by the SoundEngine uniquely identifying the sound
	 * @throws SoundEngineException
	 */
	public abstract void pauseSound(int id, String symbol) throws SoundEngineException;
	
	/**
	 * 
	 * @param id Soundscape ID
	 * @param symbol The symbol returned by the SoundEngine uniquely identifying the sound
	 * @param newVolume Floating point between 0 and 1 inclusive. If not within limits,
	 * will be rounded to the nearest valid value
	 * @throws SoundEngineException
	 */
	public abstract void setSoundVolume(int id, String symbol, double newVolume) throws SoundEngineException;
	
	
	/**
	 * Sets the play type of the sound. The sound engine implements the mechanics of the different types.
	 * <br><br>
	 * As of 3/24/17, there are 3 play types:
	 * <ul>
	 * 	<li>SINGLE</li>
	 * 	<li>LOOP</li>
	 * 	<li>RANDOM</li>
	 * </ul>
	 * @param id Soundscape ID
	 * @param symbol The symbol returned by the SoundEngine uniquely identifying the sound
	 * @param playType
	 * @throws SoundEngineException
	 */
	public abstract void setSoundPlaytype(int id, String symbol, SoundModel.PlayType playType) throws SoundEngineException;
	
	/**
	 * Change the volume of a particular soundscape
	 * @param id Soundscape ID
	 * @param newVolume Floating point between 0 and 1 inclusive. If not within limits,
	 * will be rounded to the nearest valid value
	 * @throws SoundEngineException
	 */
	public abstract void setSoundscapeVolume(int id, double newVolume) throws SoundEngineException;
	
	/**
	 * 
	 * @param id Soundscape ID
	 * @throws SoundEngineException
	 */
	public abstract void playSoundscape(int id) throws SoundEngineException;

	/**
	 * 
	 * @param id Soundscape ID
	 * @throws SoundEngineException
	 */
	public abstract void pauseSoundscape(int id) throws SoundEngineException;
	
	/**
	 * 
	 * @param id Soundscape ID
	 * @throws SoundEngineException
	 */
	public abstract void stopSoundscape(int id) throws SoundEngineException;
	
	/**
	 * Clear a soundscape from memory. Empty buffer, clear channel, etc
	 * @param id Soundscape ID
	 * @throws SoundEngineException
	 */
	public abstract void clearSoundscape(int id) throws SoundEngineException;
	
	/**
	 * Volume doubles are floating point values between 0 and 1 inclusive. If not within limits,
	 * will be rounded to the nearest valid value
	 * @param id Soundscape Id
	 * @param startVolume
	 * @param endVolume
	 * @param ms Miliseconds for fade
	 * @throws SoundEngineException
	 */
	public abstract void fadeSoundscape(int id, double startVolume, double endVolume, int ms) throws SoundEngineException;
}
