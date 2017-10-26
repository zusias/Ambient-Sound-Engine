package ase.views.components.consolepane.events;

import ase.operations.OperationsManager.Sections;
import ase.operations.SoundModel;

public class LaunchRandomSettingsEvent {
	public final Sections section;
	public final int index;
	public final SoundModel sound;
	
	public LaunchRandomSettingsEvent(Sections section, int index, SoundModel sound) {
		this.section = section;
		this.index = index;
		this.sound = sound;
	}
}
