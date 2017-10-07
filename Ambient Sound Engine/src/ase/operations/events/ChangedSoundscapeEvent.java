package ase.operations.events;

import ase.operations.SoundscapeModel;
import ase.operations.OperationsManager.Sections;
import ase.operations.SoundModel;

/**
 * Fired from OperationsManager when a Soundscape changes
 * @author Kevin C. Gall
 *
 */
public class ChangedSoundscapeEvent {
	public final Sections section;
	public final SoundscapeModel soundscape;
	public final SoundModel sound;
	public final int soundIndex;
	
	/**
	 * This event is fired when a soundscape changes or is completely replaced.
	 * Listening events should check the soundscape's runtime ID to determine whether
	 * the soundscape has been replaced
	 * @param section The section the Soundscape is associated with
	 * @param soundscape The soundscape that changed
	 * @param sound If a sound changed, this is the new instance. Otherwise, null
	 * @param soundIndex If no sound has changed, -1. If a sound has been
	 * removed, this argument will have its index, and the sound argument will be
	 * null. If the sound changed, this will have its index, and the sound will
	 * be the new instance of the model
	 */
	public ChangedSoundscapeEvent (Sections section, SoundscapeModel soundscape, SoundModel sound, int soundIndex) {
		this.section = section;
		this.soundscape = soundscape;
		this.sound = sound;
		this.soundIndex = soundIndex;
	}
}
