package ase.operations.events;

import ase.operations.OperationsManager.Sections;
import ase.models.SoundscapeModel;
import ase.models.SoundscapeSetModel;

/**
 * Fired when a soundscape set changes
 * 
 * @author Kevin C. Gall
 *
 */
public class ChangedSoundscapeSetEvent {
	public final Sections section;
	public final SoundscapeSetModel console;
	public final SoundscapeModel soundscape;
	public final int ssIndex;
	
	/**
	 * 
	 * @param section The section the Soundscape Set is associated with
	 * @param console The Soundscape Set that has changed
	 * @param soundscape If a soundscape has changed, this is the new instance. If
	 * it has been removed or nothing has changed, this argument is null
	 * @param ssIndex If no soundscape has changed, -1. If a soundscape has been
	 * removed, this argument will have its index, and the soundscape argument will be
	 * null. If the soundscape changed, this will have its index, and the soundscape will
	 * be the new model
	 */
	public ChangedSoundscapeSetEvent(Sections section, SoundscapeSetModel console, SoundscapeModel soundscape, int ssIndex) {
		this.section = section;
		this.console = console;
		this.soundscape = soundscape;
		this.ssIndex = ssIndex;
	}
}
