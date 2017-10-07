package ase.views.components.consolepane.events;

import ase.views.components.consolepane.ConsoleControlRow;

public class RowClickedEvent {
	public final ConsoleControlRow row;
	public final int index;
	public final int mouseButton;
	
	public RowClickedEvent (ConsoleControlRow row, int index, int mouseButton) {
		this.row = row;
		this.index = index;
		this.mouseButton = mouseButton;
	}
}
