package ase.operations;

/**
 * <p>Abstract interface for a sound engine implementation.
 * Defines methods to load Soundscapes and individual sounds
 * to be played by the sound engine implementation.</p>
 * 
 * TODO: Code a delegation object which subscribes to ops manager and translates changes in the model to SoundEngine methods
 * 
 * @author Kevin C. Gall
 *
 */
public abstract class SoundEngine {
	
	/**
	 * Load a soundscape into the engine. Likely a channel or buffer
	 * of some kind 
	 * @param ssModel
	 * @throws SoundEngineException
	 */
	public abstract void loadSoundscape(SoundscapeModel ssModel) throws SoundEngineException;
	
	/**
	 * Modify a Soundscape
	 * @param ssid Soundscape ID
	 * @param ssModel
	 * @throws SoundEngineException
	 */
	public abstract void modifySoundscape(int ssid, SoundscapeModel ssModel) throws SoundEngineException;
	
	/**
	 * Load a sound into an existing Soundscape
	 * @param sModel
	 * @param ssid Soundscape ID
	 * @return index within soundscape of new sound.
	 * @throws SoundEngineException
	 */
	public abstract int loadSound(SoundModel sModel, int ssid) throws SoundEngineException;
	
	/**
	 * Modify an existing sound. Must provide the SoundScape id, but the sound model
	 * already has a unique name string that the soundscape will use to find the
	 * exact sound
	 * 
	 * @param ssid Soundscape ID
	 * @param soundIndex The index within the Soundscape as existed in the
	 * SoundscapeModel or returned from the loadSound method
	 * @param sModel
	 * @throws SoundEngineException
	 */
	public abstract void modifySound(int ssid, int soundIndex, SoundModel sModel) throws SoundEngineException;
	
	/**
	 * Change the volume of a particular soundscape
	 * @param ssid Soundscape ID
	 * @param newVolume Must be a floating point number between 0 and 1 (inclusive).
	 * If it is not less than or greater than those limits, will round to 0 or 1
	 * @throws SoundEngineException
	 */
	public abstract void modifyMasterVolume(int ssid, double newVolume) throws SoundEngineException;
	
	/**
	 * 
	 * @param ssid Soundscape ID
	 * @throws SoundEngineException
	 */
	public abstract void play(int ssid) throws SoundEngineException;

	/**
	 * 
	 * @param ssid Soundscape ID
	 * @throws SoundEngineException
	 */
	public abstract void pause(int ssid) throws SoundEngineException;
	
	/**
	 * 
	 * @param ssid Soundscape ID
	 * @throws SoundEngineException
	 */
	public abstract void stop(int ssid) throws SoundEngineException;
	
	/**
	 * Clear a soundscape from memory. Empty buffer, clear channel, etc
	 * @param ssid Soundscape ID
	 * @throws SoundEngineException
	 */
	public abstract void clearSoundscape(int ssid) throws SoundEngineException;
	
	/**
	 * Volume doubles must be a floating point number between 0 and 1 (inclusive).
	 * If it is not less than or greater than those limits, will round to 0 or 1 
	 * @param ssid Soundscape Id
	 * @param startVolume
	 * @param endVolume
	 * @param ms Miliseconds for fade
	 * @throws SoundEngineException
	 */
	public abstract void fade(int ssid, double startVolume, double endVolume, int ms) throws SoundEngineException;
}
