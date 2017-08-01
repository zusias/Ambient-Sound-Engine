package ase.bridge;

import ase.operations.SoundModel;
import ase.operations.SoundscapeModel;
import ase.operations.ISubscriber;
import ase.operations.OperationsManager.Sections;
import ase.operations.RandomPlaySettings;

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
	 * @param section Indicates the section of the OperationsManager where this soundscape
	 * is loaded. Included so that the SoundEngine can reference the active soundscape
	 * of the given section when bubbling changes to the OperationsManager
	 * @return Array of symbols uniquely identifying the sounds in the Soundscape.
	 * This array will be in the order that the sounds were delivered via the
	 * Soundscape's iterator
	 * @throws SoundEngineException
	 */
	public abstract String[] loadSoundscape(SoundscapeModel ssModel, Sections section) throws SoundEngineException;
	
	/**
	 * Load a sound into an existing Soundscape
	 * @param id Soundscape ID
	 * @param sModel
	 * @return String symbol which is a unique identifier for the sound within the specified
	 * soundscape. This symbol could be arbitrary, but must be unique
	 * @throws SoundEngineException
	 */
	public abstract String loadSound(int id, SoundModel sModel) throws SoundEngineException;
	
	/**
	 * Clear an existing sound from the buffer. Must provide the SoundScape id and symbol of the sound.
	 * The sound stops playing immediately
	 * 
	 * @param id Soundscape ID
	 * @param symbol The symbol returned by the SoundEngine uniquely identifying the sound
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
	 * Sets the play type of the sound. The sound engine implements the mechanics of the different types.
	 * <br><br>
	 * As of 3/24/17, there are 3 play types:
	 * <ul>
	 * 	<li>SINGLE</li>
	 * 	<li>LOOP</li>
	 * 	<li>RANDOM</li>
	 * </ul>
	 * This method updates the Random Play Settings stored with the sound
	 * @param id Soundscape ID
	 * @param symbol The symbol returned by the SoundEngine uniquely identifying the sound
	 * @param playType
	 * @param randomSettings The settings object attached to a sound
	 * @throws SoundEngineException
	 */
	public abstract void setSoundPlaytype(int id, String symbol, SoundModel.PlayType playType, RandomPlaySettings randomSettings) throws SoundEngineException;
	
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
	 * will be rounded to the nearest valid value<br/>
	 * NOTE: This method is not responsible for stopping a soundscape when a fade out is over.
	 * @param id Soundscape Id
	 * @param startVolume
	 * @param endVolume
	 * @param ms Miliseconds for fade
	 * @throws SoundEngineException
	 */
	public abstract void fadeSoundscape(int id, double startVolume, double endVolume, int ms) throws SoundEngineException;
	
	/**
	 * Register a subscriber for when sounds are finished playing.
	 * When a sound finishes, the subscriber will be notified that the sound whose
	 * unique symbol is passed to the notifySubscriber method has stopped playing
	 * @param id Soundscape ID
	 * @param subscriber
	 */
	public abstract void subscribeToFinishedSounds(int id, ISubscriber<String> subscriber) throws SoundEngineException;
	
	/**
	 * Register a subscriber for when a soundscape finishes fading.
	 * The subscriber will be passed a PlayState indicating whether the
	 * soundscape has stopped or is now playing.<br/>
	 * If the fade is interrupted by another contradictory call to
	 * the engine, the fade will be interrupted, and the subscriber will
	 * never be notified that the fade has stopped, as it did not complete.
	 * @param id Soundscape ID
	 * @param subscriber
	 */
	public abstract void subscribeToFinishedFade(int id, ISubscriber<Boolean> subscriber) throws SoundEngineException;
	
	/**
	 * Shuts down the sound engine, releasing any relevant resources
	 */
	public abstract void shutdown();
}
