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
	public final int ssid;
	public final double masterVolume;
	private final Vector<SoundModel> sounds;
	public final boolean isPlaying;
	public final String name;
	
	/**
	 * 
	 * @param ssid
	 * @param masterVolume
	 * @param sounds
	 * @param name 
	 */
	public SoundscapeModel(int ssid, double masterVolume, Iterable<SoundModel> sounds, boolean isPlaying, String name) {
		this.ssid = ssid;
		this.masterVolume = masterVolume;
		this.sounds = new Vector<>();
		this.isPlaying = isPlaying;
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
	 * @param internal Always set to true. Here to differentiate the argument signature
	 * @param ssid
	 * @param masterVolume
	 * @param sounds
	 * @param name 
	 */
	private SoundscapeModel(boolean internal, int ssid, double masterVolume, Vector<SoundModel> sounds, boolean isPlaying, String name){
		this.ssid = ssid;
		this.masterVolume = masterVolume;
		this.sounds = sounds;
		this.isPlaying = isPlaying;
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
	public SoundscapeModel setMasterVolume(double newVolume){
		if (newVolume < 0.0) newVolume = 0.0;
		if (newVolume > 1.0) newVolume = 1.0;
		
		return newVolume == this.masterVolume ? this : 
			new SoundscapeModel(true, this.ssid, newVolume, this.sounds, this.isPlaying, this.name);
	}
	
	/**
	 * 
	 * @param isPlaying
	 * @return
	 */
	public SoundscapeModel setIsPlaying(boolean isPlaying){
		return isPlaying == this.isPlaying ? this :
			new SoundscapeModel(true, this.ssid, this.masterVolume, this.sounds, isPlaying, this.name);
	}
	
	/**
	 * 
	 * @param newName
	 * @return
	 */
	public SoundscapeModel rename(String newName){
		return newName == this.name ? this :
			new SoundscapeModel(true, this.ssid, this.masterVolume, this.sounds, isPlaying, newName);
	}
	
	/**
	 * ssid should only be set when saving a new soundscape into the database. Otherwise, should always
	 * pull from the database
	 * @param ssid
	 * @return
	 */
	public SoundscapeModel setSsid(int ssid){
		return ssid == this.ssid ? this :
			new SoundscapeModel(true, ssid, this.masterVolume, this.sounds, this.isPlaying, this.name);
	}
	
	/**
	 * Add a single sound to the soundscape
	 * @param sound
	 * @return
	 */
	public SoundscapeModel addSound(SoundModel sound){
		Vector<SoundModel> newSounds = cloneVector();
		
		newSounds.add(sound);
		
		return new SoundscapeModel(true, this.ssid, this.masterVolume, newSounds, this.isPlaying, this.name);
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
		
		Vector<SoundModel> newSounds = cloneVector();
		
		newSounds.add(newSound);
		
		return new SoundscapeModel(true, this.ssid, this.masterVolume, newSounds, this.isPlaying, this.name);
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
		Vector<SoundModel> newSounds = cloneVector();
		
		for (SoundModel s : sounds){
			newSounds.add(s);
		}
		
		return new SoundscapeModel(true, this.ssid, this.masterVolume, newSounds, this.isPlaying, this.name);
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
		
		return new SoundscapeModel(true, this.ssid, this.masterVolume, newSounds, this.isPlaying, this.name);
	}
	
	/**
	 * Removes sound at the given index
	 * @param index
	 * @return
	 * @throws ArrayIndexOutOfBoundsException if invalid index
	 */
	public SoundscapeModel removeSound(int index) throws ArrayIndexOutOfBoundsException {
		Vector<SoundModel> newSounds = cloneVector();
		
		newSounds.remove(index);
		
		return new SoundscapeModel(true, this.ssid, this.masterVolume, newSounds, this.isPlaying, this.name);
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
	public SoundscapeModel replaceSound(int index, SoundModel sound) throws ArrayIndexOutOfBoundsException {
		Vector<SoundModel> newSounds = cloneVector();
		
		newSounds.setElementAt(sound, index);
		
		return new SoundscapeModel(true, this.ssid, this.masterVolume, newSounds, this.isPlaying, this.name);
	}
}
