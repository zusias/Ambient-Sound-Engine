/*
 * SoundscapeSet.java
 *
 * Created on March 3, 2006, 5:08 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package ase_source_bak;

import java.util.Vector;

/**
 * Completed
 * 
 * @author Lance
 * @author CKidwell
 */

public class SoundscapeSet {
	Vector<Soundscape> activeSet;

	/**
	 * Constructor, creates a new instance of SoundscapeSet creates an empty
	 * vector for taking in Soundscapes
	 */
	public SoundscapeSet() {
		activeSet = new Vector<Soundscape>();
	}

	/**
	 * Returns a soundscape from the set if it matches both the ID and the
	 * consoleID given
	 */
	public Soundscape loadFromSoundscapeBank(int ssID, int console) {
		int position = 0;
		int size = activeSet.size();
		while (position < size) {
			if ((activeSet.elementAt(position).getSoundscapeID() == ssID)
					&& (activeSet.elementAt(position).getConsoleID() == console)) {

				return activeSet.elementAt(position);
			}
			position++;
		}
		return null;
	}

	/** adds a soundscape to the vector in the soundscape set */
	public void addSoundscape(Soundscape soundscape) {
		activeSet.add(soundscape);
	}

	/** invokes the vector method remove to remove the soundscape */
	public boolean deleteSoundscape(Soundscape soundscape) {
		return (activeSet.remove(soundscape));
	}
}
