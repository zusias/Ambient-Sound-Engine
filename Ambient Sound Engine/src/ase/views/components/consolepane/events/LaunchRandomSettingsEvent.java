package ase.views.components.consolepane.events;

import ase.models.SoundModel;
import ase.operations.OperationsManager.Sections;

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
