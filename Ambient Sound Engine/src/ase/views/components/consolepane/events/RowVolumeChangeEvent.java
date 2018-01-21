package ase.views.components.consolepane.events;

import ase.views.components.consolepane.ConsoleControlRow;

public class RowVolumeChangeEvent extends RowEvent {
	public final int newVolume;
	
	public RowVolumeChangeEvent(ConsoleControlRow row, int index, int newVolume) {
		super(row, index);
		
		this.newVolume = newVolume;
	}
}
