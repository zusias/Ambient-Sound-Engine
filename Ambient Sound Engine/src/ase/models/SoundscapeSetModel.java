package ase.models;

import java.util.Iterator;
import java.util.Vector;

import ase.operations.NoMatchFoundException;

/**
 * Immutable data structure representing a set of soundscape objects
 * Holds information like which soundscape is currently active
 * 
 * TODO: Implement access methods to be able to manipulate contents.
 * Should perhaps be not as extensive as SoundscapeModel methods.
 * Perhaps just provide methods to access Soundscapes and then easy
 * ways to insert them back into the set
 * 
 * @author Kevin
 *
 */
public class SoundscapeSetModel implements Iterable<SoundscapeModel> {
	private final Vector<SoundscapeModel> set;
	public final int activeSoundscapeIndex; //maybe get rid of this? It's a convenience, not a necessity...
	
	public final SoundscapeModel activeSoundscape;
	
	/**
	 * Set must have at least one SoundscapeModel object.<br>
	 * Sets the active element as the first in the index
	 * @param set
	 * @throws IllegalArgumentException if set contains no elements
	 */
	public SoundscapeSetModel(SoundscapeModel[] set) throws IllegalArgumentException {
		if (set.length == 0){
			throw new IllegalArgumentException();
		}
		
		this.set = new Vector<>();
		
		for (SoundscapeModel ss : set){
			this.set.add(ss);
		}
		
		this.activeSoundscape = this.set.elementAt(0);
		this.activeSoundscapeIndex = 0;
	}
	
	/**
	 * 
	 * @param set
	 * @param activeSs The active soundscape. There is no check to ensure this Soundscape
	 * exists in the passed vector. This is an internal method, so the class will ensure that
	 * it calls this constructor correctly
	 */
	private SoundscapeSetModel(Vector<SoundscapeModel> set, SoundscapeModel activeSs, int activeSsIndex){
		this.set = set;
		this.activeSoundscape = activeSs;
		this.activeSoundscapeIndex = activeSsIndex;
	}
	
	/**
	 * Here to prevent all the typing of the down-cast everywhere I need to clone this object
	 * @return
	 */
	private Vector<SoundscapeModel>cloneVector(){
		return (Vector<SoundscapeModel>)this.set.clone();
	}
	
	@Override
	public Iterator<SoundscapeModel> iterator(){
		return this.set.iterator();
	}
	
	/**
	 * Searches for the soundscape <i>by soundscape id (ssid)</i>. So passing an old reference
	 * to this object will still produce the correct result, unless the ssid has changed
	 * @param ss The soundscape to search for.
	 * @return The index, or -1 if not found If the ssid of the passed soundscape is -1,
	 * returns the first soundscape with a matching id in the set. (There could be multiple
	 * with ssid == -1 because that is the default ID when a soundscape is unsaved)
	 */
	public int getSoundscapeIndex(SoundscapeModel ss){
		int count = 0;
		for (SoundscapeModel currentSs : this.set){
			if (currentSs.ssid == ss.ssid){
				return count;
			}
			count++;
		}
		
		return -1;
	}
	
	/**
	 * 
	 * @param index
	 * @return
	 * @throws ArrayIndexOutOfBoundsException if the index is invalid
	 */
	public SoundscapeModel getSoundscapeAtIndex(int index) throws ArrayIndexOutOfBoundsException{
		return this.set.elementAt(index);
	}
	
	/**
	 * Returns soundscae by valid ssid. Note that the default initial ssid, -1, is not a valid
	 * argument for this method
	 * @param ssid ID (greater than -1)
	 * @return
	 * @throws NoMatchFoundException
	 */
	public SoundscapeModel getSoundscapeBySsid(int ssid) throws NoMatchFoundException {
		if (ssid < 0) {
			throw new IllegalArgumentException("Invalid ssid");
		}
		
		for (SoundscapeModel ss : this.set){
			if (ss.ssid == ssid){
				return ss;
			}
		}
		
		throw new NoMatchFoundException();
	}
	
	public int getTotalSoundscapes(){
		return this.set.size();
	}
	
	/**
	 * Replaces a soundscape in the set. Most often used when modifying a soundscape
	 * @param index
	 * @param ss
	 * @return
	 * @throws ArrayIndexOutOfBoundsException if index invalid
	 */
	public SoundscapeSetModel replaceSoundscape(int index, SoundscapeModel ss) throws ArrayIndexOutOfBoundsException{
		Vector<SoundscapeModel> newSet = cloneVector();
		
		newSet.setElementAt(ss, index);
		
		//ensure that if the active soundscape is being updated, the reference to it changes, too
		SoundscapeModel active = this.activeSoundscapeIndex == index ? ss : this.activeSoundscape;
		
		return new SoundscapeSetModel(newSet, active, this.activeSoundscapeIndex);
	}
	
	/**
	 * Defaults to replace the active soundscape with the passed soundscape
	 * @param ss The soundscape to replace the active soundscape
	 * @return
	 */
	public SoundscapeSetModel replaceSoundscape(SoundscapeModel ss){
		return this.replaceSoundscape(this.activeSoundscapeIndex, ss);
	}
	
	/**
	 * 
	 * @param ss
	 * @return
	 * @throws IllegalArgumentException if the soundscape (with identical ID) is already in the set
	 */
	public SoundscapeSetModel addSoundscape(SoundscapeModel ss) throws IllegalArgumentException {
		//error check
		for (SoundscapeModel currentSs : this.set){
			if (ss.ssid == currentSs.ssid && ss.ssid != -1){
				throw new IllegalArgumentException("The set cannot contain 2 soundscapes with the same SSID");
			}
		}
		
		Vector<SoundscapeModel> newSet = cloneVector();
		
		newSet.add(ss);
		
		return new SoundscapeSetModel(newSet, this.activeSoundscape, this.activeSoundscapeIndex);
	}
	
	/**
	 * 
	 * @param ss
	 * @return
	 * @throws NoMatchFoundException if the soundscape is not in the set
	 */
	public SoundscapeSetModel removeSoundscape(SoundscapeModel ss) throws NoMatchFoundException{
		if (ss.ssid < 0) {
			throw new IllegalArgumentException("Invalid ssid on soundscape");
		}
		
		Vector<SoundscapeModel> newSet = new Vector<>();
		
		for (SoundscapeModel currentSs : this.set){
			if (currentSs.ssid != ss.ssid){
				newSet.add(currentSs);
			}
		}
		if (newSet.size() == this.set.size()){
			throw new NoMatchFoundException();
		}
		
		int newActiveIndex = this.activeSoundscapeIndex;
		if (newActiveIndex == newSet.size()){
			newActiveIndex--;
		}
		
		SoundscapeModel newActiveSs = newActiveIndex > -1 ?
				newSet.get(newActiveIndex) : null; 
		
		return new SoundscapeSetModel(newSet, newActiveSs, newActiveIndex);
	}
	
	/**
	 * Remove element at the specified index
	 * @param index
	 * @return
	 * @throws ArrayIndexOutOfBoundsException if index is invalid
	 */
	public SoundscapeSetModel removeSoundscape(int index) throws ArrayIndexOutOfBoundsException{
		Vector<SoundscapeModel> newSet = cloneVector();
		
		newSet.remove(index);
		
		int newActiveIndex = this.activeSoundscapeIndex;
		if (newActiveIndex == newSet.size()){
			newActiveIndex--;
		}
		
		SoundscapeModel newActiveSs = newActiveIndex > -1 ?
				newSet.get(newActiveIndex) : null; 
		
		return new SoundscapeSetModel(newSet, newActiveSs, newActiveIndex);
	}
	
	/**
	 * 
	 * @param activeSs
	 * @return
	 * @throws IllegalArgumentException if the passed soundscape model is not in the set
	 */
	public SoundscapeSetModel setActiveSoundscape(SoundscapeModel activeSs) throws IllegalArgumentException {
		int index = set.indexOf(activeSs);
		
		if (index < 0){
			throw new IllegalArgumentException();
		}
		
		return this.activeSoundscape == activeSs ? this :
			new SoundscapeSetModel(this.set, activeSs, index);
	}
}