package ase.views.components.consolepane.events;

import ase.views.components.consolepane.ConsoleControlRow;

public class RowPlayPressedEvent extends RowEvent {
	//public final boolean currentPlayingState;
	
	public RowPlayPressedEvent(ConsoleControlRow row, int index) {
		super(row, index);
	}
	
}
