package ase_source_bak;

import java.util.Vector;

/**
 * Handles the soundscape object of the database. Completed
 * 
 * @author Lance
 */
public class Soundscape {

	private int ID;
	private String name;
	private Vector<SoundObject> soundSet = new Vector<SoundObject>();
	private int console;
	private int masterVolume;

	/**
	 * Constructor Empty Soundscape, takes in an int for an ID and an int for
	 * whether it's in the top or bottom console. name and description default
	 * to "New Soundscape" and volume is 0. The vector that holds the sounds to
	 * be played remains empty. the floats for listener position have no
	 * apparent current use They have no method to be set or retrieved but
	 * remain private.
	 * 
	 * @param tempSSID
	 * @param topOrBottomConsole
	 */
	public Soundscape(int tempSSID, int topOrBottomConsole) {
		ID = tempSSID;
		name = "New Soundscape";
		masterVolume = 0;
		console = topOrBottomConsole;
	}

	/**
	 * Constructor Empty Soundscape, takes in an int for an ID and an int for
	 * whether it's in the top or bottom console. Name and description are set
	 * to the Strings taken in by the constructor method. Vector holding sound
	 * Objects is empty, master volume is saved to the int taken in by the
	 * constructor the floats for listener position have no apparent use still,
	 * they have no method to be set or retrieved but remain private.
	 * 
	 * @param ssID
	 * @param topOrBottomConsole
	 * @param ssName
	 * @param desc
	 * @param volume
	 */
	public Soundscape(int ssID, int topOrBottomConsole, String ssName,
			String desc, int volume) {
		name = ssName;
		console = topOrBottomConsole;
		ID = ssID;
		masterVolume = volume;
	}

	/**
	 * adds a new SoundObject to the Soundscape, returns an integer of the
	 * SoundObject's position in the vector
	 * 
	 * @param ambient
	 * @return
	 */
	public int addSound(SoundObject ambient) {
		soundSet.add(ambient);// invokes the vector method .add() to add a new
								// soundObject to the soundscape's playlist
		return soundSet.indexOf(ambient);
	}

	/**
	 * Delete's the sound from the Soundscape at the given position
	 * 
	 * @param position
	 */
	public void deleteSound(int position) {
		int size = soundSet.size();

		soundSet.insertElementAt(soundSet.lastElement(), position);// Inserts
																	// the last
																	// sound
																	// into the
																	// position
																	// indicated
		soundSet.removeElementAt(position + 1);// deletes the sound after the
												// one that was inserted
		soundSet.removeElementAt(size - 1);// deletes the last SoundObject in
											// the vector, which would be a
											// duplicate otherwise.
	}

	/**
	 * Sets a new master volume for the Soundscape
	 * 
	 * @param volume
	 */
	public void setMasterVolume(int volume) {
		masterVolume = volume;
	}

	/**
	 * Returns an int of the masterVolume setting
	 * 
	 * @return
	 */
	public int getMasterVolume() {
		return masterVolume;
	}

	/**
	 * Sets the Soundscapes name to the sting input
	 * 
	 * @param newName
	 */
	public void setSSname(String newName) {
		name = newName;
	}

	/**
	 * Returns the name of the SoundScape
	 * 
	 * @return
	 */
	public String getSSName() {
		return name;
	}

	/**
	 * returns the ID of the Soundscape
	 * 
	 * @return
	 */
	public int getSoundscapeID() {
		return ID;
	}

	/**
	 * sets the Soundscape ID to a new ID of choice
	 * 
	 * @param newID
	 */
	public void changeSSID(int newID) {
		ID = newID;
	}

	/**
	 * Returns the console ID (top or bottom) of the Soundscape
	 * 
	 * @return
	 */
	public int getConsoleID() {
		return console;
	}

	/**
	 * returns the number of SoundObjects in the vector soundSet
	 * 
	 * @return
	 */
	public int getSoundscapeSoundsCount() {
		return soundSet.size();
	}

	/**
	 * Returns the index integer for the first time SoundObject ambient occurs
	 * in the vector
	 * 
	 * @param ambient
	 * @return
	 */
	public int getIndex(SoundObject ambient) {
		return soundSet.indexOf(ambient);
	}

	/**
	 * Returns the SoundObject at position elementPosition in the vector
	 * 
	 * @param elementPosition
	 * @return
	 */
	public SoundObject getSound(int elementPosition) {
		return soundSet.elementAt(elementPosition);
	}

	/**
	 * Returns the name of the SoundObject at the position indicated
	 * 
	 * @param ID
	 * @return
	 */
	public String getSoundName(int ID) {
		return soundSet.elementAt(ID).getName();
	}

	/**
	 * returns the soundVolume of the SoundObject at the position indicated
	 * 
	 * @param int
	 *            ID
	 * @return int volume
	 */
	public int getSingleSoundVolume(int ID) {
		return soundSet.elementAt(ID).getVolume();
	}

	/**
	 * returns the playback mode of the sound indicated
	 * 
	 * @param int
	 *            ID of the sound in question
	 * @return int 0 for looped sounds, 1 and 2 for non repeating
	 */
	public int getSingleSoundPlaybackType(int ID) {
		return soundSet.elementAt(ID).getPlaybackType();
	}

}
