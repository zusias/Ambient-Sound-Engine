package ase.operations;

//Utils
import java.util.Iterator;
import java.util.Vector;
//IO
import java.nio.file.Path;

public class SoundscapeModel implements Iterable<SoundModel> {
	public final int ssid;
	public final double masterVolume;
	private final Vector<SoundModel> sounds;
	public final boolean isPlaying;
	
	/**
	 * 
	 * @param ssid
	 * @param masterVolume
	 * @param sounds
	 */
	public SoundscapeModel(int ssid, double masterVolume, Iterable<SoundModel> sounds, boolean isPlaying) {
		this.ssid = ssid;
		this.masterVolume = masterVolume;
		this.sounds = new Vector<>();
		this.isPlaying = isPlaying;
		
		for (SoundModel sound : sounds){
			this.sounds.add(sound);
		}
	}
	
	/**
	 * Internal constructor. Does not copy the array passed to it, as an internal invocation
	 * of this constructor can either pass its own array of nothing has changed or a new
	 * array that will never be modified afterwards. Internally, this object manages shared
	 * arrays but maintains immutability
	 * @param internal Always set to true. Here to differentiate the argument signature
	 * @param ssid
	 * @param masterVolume
	 * @param sounds
	 */
	private SoundscapeModel(boolean internal, int ssid, double masterVolume, Vector<SoundModel> sounds, boolean isPlaying){
		this.ssid = ssid;
		this.masterVolume = masterVolume;
		this.sounds = sounds;
		this.isPlaying = isPlaying;
	}
	
	public Iterator<SoundModel> iterator(){
		return this.sounds.iterator();
	}
	
	/**
	 * 
	 * @param newVolume
	 * @return
	 */
	public SoundscapeModel setMasterVolume(double newVolume){
		return newVolume == this.masterVolume ? this : 
			new SoundscapeModel(true, this.ssid, newVolume, this.sounds, this.isPlaying);
	}
	
	/**
	 * 
	 * @param isPlaying
	 * @return
	 */
	public SoundscapeModel setIsPlaying(boolean isPlaying){
		return isPlaying == this.isPlaying ? this :
			new SoundscapeModel(true, this.ssid, this.masterVolume, this.sounds, isPlaying);
	}
	
	/**
	 * Add a single sound to the soundscape
	 * @param sound
	 * @return
	 */
	public SoundscapeModel addSound(SoundModel sound){
		Vector<SoundModel> newSounds = (Vector<SoundModel>)this.sounds.clone();
		
		newSounds.add(sound);
		
		return new SoundscapeModel(true, this.ssid, this.masterVolume, newSounds, this.isPlaying);
	}
	
	/**
	 * 
	 * @param filePath
	 * @param name
	 * @param currentPlayType
	 * @param isPlaying
	 * @return
	 */
	public SoundscapeModel addSound(Path filePath, String name, SoundModel.PlayType currentPlayType, boolean isPlaying) {
		SoundModel newSound = new SoundModel(filePath, name, currentPlayType, isPlaying, 1.0);
		
		Vector<SoundModel> newSounds = (Vector<SoundModel>)this.sounds.clone();
		
		newSounds.add(newSound);
		
		return new SoundscapeModel(true, this.ssid, this.masterVolume, newSounds, this.isPlaying);
	}
	
	public SoundscapeModel addSound(Path filePath, String name, SoundModel.PlayType currentPlayType) {
		return this.addSound(filePath, name, currentPlayType, true);
	}
	
	/**
	 * Add multiple sounds to the soundscape
	 * @param sounds
	 * @return
	 */
	public SoundscapeModel addSounds(Iterable<SoundModel> sounds){
		Vector<SoundModel> newSounds = (Vector<SoundModel>)this.sounds.clone();
		
		for (SoundModel s : sounds){
			newSounds.add(s);
		}
		
		return new SoundscapeModel(true, this.ssid, this.masterVolume, newSounds, this.isPlaying);
	}
	
	/**
	 * Removes the first occurrence of the object whose name matches the given
	 * sound argument. If none, throws exception
	 * @param sound
	 * @return
	 * @throws NoMatchFoundException
	 */
	public SoundscapeModel removeSound(SoundModel sound) throws NoMatchFoundException {
		Vector<SoundModel> newSounds = new Vector<>();
		
		for (SoundModel oldSound : this.sounds){
			if (!oldSound.name.equals(sound.name)){
				newSounds.add(oldSound);
			}
		}
		
		if (newSounds.size() == this.sounds.size()) {
			throw new NoMatchFoundException("No match for the sound " + sound.name + " found to remove from Soundscape " + this.ssid);
		}
		
		return new SoundscapeModel(true, this.ssid, this.masterVolume, newSounds, this.isPlaying);
	}
	
	/**
	 * Removes sound at the given index
	 * @param index
	 * @return
	 */
	public SoundscapeModel removeSound(int index){
		Vector<SoundModel> newSounds = (Vector<SoundModel>)this.sounds.clone();
		
		newSounds.remove(index);
		
		return new SoundscapeModel(true, this.ssid, this.masterVolume, newSounds, this.isPlaying);
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
	
	public SoundModel getSoundAtIndex(int index){
		return this.sounds.elementAt(index);
	}
	
	public SoundscapeModel replaceSound(int index, SoundModel sound){
		Vector<SoundModel> newSounds = (Vector<SoundModel>)this.sounds.clone();
		
		newSounds.setElementAt(sound, index);
		
		return new SoundscapeModel(true, this.ssid, this.masterVolume, newSounds, this.isPlaying);
	}

	/**
	 * Modifies the sound model at the given index, possible throwing an IndexOutOfBounds exception.
	 * @param index
	 * @param newPlayType
	 * @param isPlaying
	 * @param volume
	 * @return
	 */
	public SoundscapeModel modifySound(int index, SoundModel.PlayType newPlayType, boolean isPlaying, double volume){
		SoundModel s = this.sounds.elementAt(index);
		SoundModel s2 = s.setAll(isPlaying, newPlayType, volume);
		
		if (s == s2) {return this;}

		return replaceSound(index, s2);
	}
	public SoundscapeModel modifySound(int index, SoundModel.PlayType newPlayType){
		SoundModel s = this.sounds.elementAt(index);
		SoundModel s2 = s.setPlayType(newPlayType);
		
		if (s == s2) {return this;}
		
		return replaceSound(index, s2);
	}
	public SoundscapeModel modifySound(int index, boolean isPlaying){
		SoundModel s = this.sounds.elementAt(index);
		SoundModel s2 = s.setPlay(isPlaying);
		
		if (s == s2) {return this;}
		
		return replaceSound(index, s2);
	}
	public SoundscapeModel modifySound(int index, double volume){
		SoundModel s = this.sounds.elementAt(index);
		SoundModel s2 = s.setVolume(volume);
		
		if (s == s2) {return this;}
		
		return replaceSound(index, s2);
	}
}
