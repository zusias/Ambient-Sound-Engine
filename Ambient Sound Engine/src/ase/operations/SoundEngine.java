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
	 * @return ID: if ssid != -1, will simply return the ssid. If ssid == -1,
	 * returns a <i>negative</i> number that uniquely identifies the soundscape until
	 * modifySoundscape is invoked to replace the ssid.
	 */
	public abstract int loadSoundscape(SoundscapeModel ssModel);
	
	/**
	 * Modify a Soundscape
	 * @param ssid Soundscape ID
	 * @param ssModel
	 * @return success?
	 */
	public abstract boolean modifySoundscape(int ssid, SoundscapeModel ssModel);
	
	/**
	 * Load a sound into an existing Soundscape
	 * @param sModel
	 * @param ssid Soundscape ID
	 * @return index within soundscape of new sound. -1 if failure
	 */
	public abstract int loadSound(SoundModel sModel, int ssid);
	
	/**
	 * Modify an existing sound. Must provid the SoundScape id, but the sound model
	 * already has a unique name string that the soundscape will use to find the
	 * exact sound
	 * 
	 * @param ssid Soundscape ID
	 * @param soundIndex The index within the Soundscape as existed in the
	 * SoundscapeModel or returned from the loadSound method
	 * @param sModel
	 * @return success?
	 */
	public abstract boolean modifySound(int ssid, int soundIndex, SoundModel sModel);
	
	/**
	 * Change the volume of a particular soundscape
	 * @param ssid Soundscape ID
	 * @param newVolume Must be a floating point number between 0 and 1 (inclusive).
	 * If it is not less than or greater than those limits, will round to 0 or 1 
	 * @return success?
	 */
	public abstract boolean modifyMasterVolume(int ssid, double newVolume);
	
	/**
	 * 
	 * @param ssid Soundscape ID
	 * @return success?
	 */
	public abstract boolean play(int ssid);

	/**
	 * 
	 * @param ssid Soundscape ID
	 * @return success?
	 */
	public abstract boolean pause(int ssid);
	
	/**
	 * 
	 * @param ssid Soundscape ID
	 * @return success?
	 */
	public abstract boolean stop(int ssid);
	
	/**
	 * Clear a soundscape from memory. Empty buffer, clear channel, etc
	 * @param ssid Soundscape ID
	 * @return success?
	 */
	public abstract boolean clearSoundscape(int ssid);
	
	/**
	 * Volume doubles must be a floating point number between 0 and 1 (inclusive).
	 * If it is not less than or greater than those limits, will round to 0 or 1 
	 * @param ssid Soundscape Id
	 * @param startVolume
	 * @param endVolume
	 * @param ms Miliseconds for fade
	 * @return
	 */
	public abstract boolean fade(int ssid, double startVolume, double endVolume, int ms);
}
