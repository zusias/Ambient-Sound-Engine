package ase.operations;

//Utils
import java.util.Iterator;
import java.util.Vector;
//IO
import java.nio.file.Path;

/**
 * Immutable data structure to represent a soundscape.
 * @author Kevin
 *
 */
public class SoundscapeModel implements Iterable<SoundModel> {
	public static enum PlayState{
		FADEIN, FADEOUT, PLAYING, STOPPED
	}
	
	public final int ssid; //database id
	public final int runtimeId; //runtime id - guaranteed unique each run
	public final double masterVolume;
	private final Vector<SoundModel> sounds;
	public final PlayState playState;
	public final int fadeDuration;
	public final String name;
	
	/**
	 * 
	 * @param ssid
	 * @param runtimeId TODO
	 * @param masterVolume
	 * @param sounds
	 * @param name
	 * @param playState
	 * @param fadeDuration Duration of the fade in milliseconds. Defaulted to 0 if invalid
	 * given playState
	 */
	public SoundscapeModel(int ssid, int runtimeId,
			double masterVolume, SoundModel[] sounds, String name, PlayState playState, int fadeDuration) 
	{	
		if (playState == PlayState.PLAYING || playState == PlayState.STOPPED || fadeDuration < 0){
			fadeDuration = 0;
		}
		
		this.ssid = ssid;
		this.runtimeId = runtimeId;
		this.masterVolume = masterVolume;
		this.sounds = new Vector<>();
		this.playState = playState;
		this.fadeDuration = fadeDuration;
		this.name = name;
		
		for (SoundModel sound : sounds){
			this.sounds.add(sound);
		}
	}
	
	/**
	 * Internal constructor. Does not copy the array passed to it, as an internal invocation
	 * of this constructor can either pass its own array of nothing has changed or a new
	 * array that will never be modified afterwards. Internally, this object manages shared
	 * arrays but maintains immutability
	 * @param ssid
	 * @param runtimeId TODO
	 * @param masterVolume
	 * @param sounds
	 * @param name
	 * @param playState
	 * @param fadeDuration Duration of the fade in milliseconds. Defaulted to 0 if invalid
	 * given playState
	 */
	private SoundscapeModel(int ssid, int runtimeId,
			double masterVolume, Vector<SoundModel> sounds, String name, PlayState playState, int fadeDuration)
	{
		if (playState == PlayState.PLAYING || playState == PlayState.STOPPED || fadeDuration < 0){
			fadeDuration = 0;
		}
		
		this.ssid = ssid;
		this.runtimeId = runtimeId;
		this.masterVolume = masterVolume;
		this.sounds = sounds;
		this.playState = playState;
		this.fadeDuration = fadeDuration;
		this.name = name;
	}
	
	private Vector<SoundModel> cloneVector(){
		return (Vector<SoundModel>) sounds.clone();
	}
	
	@Override
	public Iterator<SoundModel> iterator(){
		return this.sounds.iterator();
	}
	
	/**
	 * 
	 * @param newVolume
	 * @return
	 */
	SoundscapeModel setMasterVolume(double newVolume){
		if (newVolume < 0.0) newVolume = 0.0;
		if (newVolume > 1.0) newVolume = 1.0;
		
		return newVolume == this.masterVolume ? this : 
			new SoundscapeModel(this.ssid, this.runtimeId, newVolume,
					this.sounds, this.name, this.playState, this.fadeDuration);
	}
	
	/**
	 * Sets the isPlaying property <i>and the Fade State (fade property)</i>
	 * @param isPlaying
	 * @return
	 */
	SoundscapeModel setIsPlaying(boolean isPlaying){
		PlayState playState = isPlaying ? PlayState.PLAYING : PlayState.STOPPED;
		
		return playState == this.playState ? this :
			new SoundscapeModel(this.ssid, this.runtimeId,
					this.masterVolume, this.sounds, this.name, playState, 0);
	}
	
	/**
	 * This method is partially redundant with setIsPlaying, but specifically
	 * allows specifying of a fade duration. This method will not
	 * throw errors if a non-fade state is passed.
	 * @param fade
	 * @param fadeDuration The duration in milliseconds for the fade to execute.
	 * Defaults to 0 if not an actively fading fade state
	 * @return
	 */
	SoundscapeModel setFadeState(PlayState fade, int fadeDuration) {
		return fade == this.playState ? this :
			new SoundscapeModel(this.ssid, this.runtimeId,
					this.masterVolume, this.sounds, this.name, fade, fadeDuration);
	}
	
	/**
	 * 
	 * @param newName
	 * @return
	 */
	SoundscapeModel rename(String newName){
		return newName == this.name ? this :
			new SoundscapeModel(this.ssid, this.runtimeId,
					this.masterVolume, this.sounds, newName, this.playState, this.fadeDuration);
	}
	
	/**
	 * ssid should only be set when saving a new soundscape into the database. Otherwise, should always
	 * pull from the database
	 * @param ssid
	 * @return
	 */
	SoundscapeModel setSsid(int ssid){
		return ssid == this.ssid ? this :
			new SoundscapeModel(ssid, this.runtimeId,
					this.masterVolume, this.sounds, this.name, this.playState, this.fadeDuration);
	}
	
	/**
	 * Add a single sound to the soundscape
	 * @param sound
	 * @return
	 */
	SoundscapeModel addSound(SoundModel sound){
		Vector<SoundModel> newSounds = cloneVector();
		
		newSounds.add(sound);
		
		return new SoundscapeModel(this.ssid, this.runtimeId,
				this.masterVolume, newSounds, this.name, this.playState, this.fadeDuration);
	}
	
	/**
	 * 
	 * @param filePath
	 * @param name
	 * @param currentPlayType
	 * @param isPlaying
	 * @return
	 */
	SoundscapeModel addSound(Path filePath, String name, SoundModel.PlayType currentPlayType, boolean isPlaying) {
		SoundModel newSound = new SoundModel(filePath, name, currentPlayType, isPlaying, 1.0);
		
		Vector<SoundModel> newSounds = cloneVector();
		
		newSounds.add(newSound);
		
		return new SoundscapeModel(this.ssid, this.runtimeId,
				this.masterVolume, newSounds, this.name, this.playState, this.fadeDuration);
	}
	
	SoundscapeModel addSound(Path filePath, String name, SoundModel.PlayType currentPlayType) {
		return this.addSound(filePath, name, currentPlayType, true);
	}
	
	/**
	 * Add multiple sounds to the soundscape
	 * @param sounds
	 * @return
	 */
	SoundscapeModel addSounds(SoundModel[] sounds){
		Vector<SoundModel> newSounds = cloneVector();
		
		for (SoundModel s : sounds){
			newSounds.add(s);
		}
		
		return new SoundscapeModel(this.ssid, this.runtimeId,
				this.masterVolume, newSounds, this.name, this.playState, this.fadeDuration);
	}
	
	/**
	 * Removes the first occurrence of the object whose name matches the given
	 * sound argument. If none, throws exception
	 * @param sound
	 * @return
	 * @throws NoMatchFoundException
	 */
	SoundscapeModel removeSound(SoundModel sound) throws NoMatchFoundException {
		Vector<SoundModel> newSounds = new Vector<>();
		
		for (SoundModel oldSound : this.sounds){
			if (!oldSound.name.equals(sound.name)){
				newSounds.add(oldSound);
			}
		}
		
		if (newSounds.size() == this.sounds.size()) {
			throw new NoMatchFoundException("No match for the sound " + sound.name + " found to remove from Soundscape " + this.ssid);
		}
		
		return new SoundscapeModel(this.ssid, this.runtimeId,
				this.masterVolume, newSounds, this.name, this.playState, this.fadeDuration);
	}
	
	/**
	 * Removes sound at the given index
	 * @param index
	 * @return
	 * @throws ArrayIndexOutOfBoundsException if invalid index
	 */
	SoundscapeModel removeSound(int index) throws ArrayIndexOutOfBoundsException {
		Vector<SoundModel> newSounds = cloneVector();
		
		newSounds.remove(index);
		
		return new SoundscapeModel(this.ssid, this.runtimeId,
				this.masterVolume, newSounds, this.name, this.playState, this.fadeDuration);
	}
	
	/**
	 * Get the index of the sound within the Soundscape based on string name.
	 * Variants include passing a SoundModel object (which still searches by
	 * name, not object reference) and not passing occurrence, which defaults
	 * it to 1.
	 * @param soundName
	 * @param occurrence n. If less than 1, defaults to 1 
	 * @return index of nth occurrence of the sound. Returns -1 if none found
	 */
	public int getSoundIndex(String soundName, int occurrence){
		int count = 0;
		int elementIndex = -1;
		int index = 0;
		occurrence = occurrence < 1 ? 1 : occurrence;

		//increment count if name == the given argument's name
		for (SoundModel currentSound : this.sounds){
			if (currentSound.name.equals(soundName) && ++count == occurrence){
				elementIndex = index;
			}
			index++;
		}
		
		return elementIndex;
	}
	
	/**
	 * Default to 1st occurrence
	 * @param soundName
	 * @return
	 */
	public int getSoundIndex(String soundName){
		return this.getSoundIndex(soundName, 1);
	}
	
	/**
	 * Get the index of the sound within the Soundscape
	 * @param sound
	 * @param occurrence n
	 * @return index of nth occurrence of the sound. Returns -1 if none found
	 */
	public int getSoundIndex(SoundModel sound, int occurrence) {
		return this.getSoundIndex(sound.name, occurrence);
	}
	
	/**
	 * Default to 1st occurrence
	 * @param sound
	 * @return
	 */
	public int getSoundIndex(SoundModel sound){
		return this.getSoundIndex(sound.name, 1);
	}
	
	/**
	 * 
	 * @param index
	 * @return
	 * @throws ArrayIndexOutOfBoundsException if the index is invalid
	 */
	public SoundModel getSoundAtIndex(int index) throws ArrayIndexOutOfBoundsException {
		return this.sounds.elementAt(index);
	}
	
	/**
	 * Replaces a sound at the specified index
	 * @param index
	 * @param sound
	 * @return
	 * @throws ArrayIndexOutOfBoundsException if the index is invalid
	 */
	SoundscapeModel replaceSound(int index, SoundModel sound) throws ArrayIndexOutOfBoundsException {
		Vector<SoundModel> newSounds = cloneVector();
		
		newSounds.setElementAt(sound, index);
		
		return new SoundscapeModel(this.ssid, this.runtimeId,
				this.masterVolume, newSounds, this.name, this.playState, this.fadeDuration);
	}
}
