package ase.views.components.consolepane.events;

import ase.views.components.consolepane.ConsoleControlRow;

public class RowClickedEvent extends RowEvent {
	public final int mouseButton;
	
	public RowClickedEvent (ConsoleControlRow row, int index, int mouseButton) {
		super(row, index);
		this.mouseButton = mouseButton;
	}
}
