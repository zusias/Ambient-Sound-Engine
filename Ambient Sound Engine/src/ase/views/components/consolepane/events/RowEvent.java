package ase.views.components.consolepane.events;

import ase.views.components.consolepane.ConsoleControlRow;

public abstract class RowEvent {

	public final ConsoleControlRow row;
	public final int index;

	public RowEvent(ConsoleControlRow row, int index) {
		this.row = row;
		this.index = index;
	}

}