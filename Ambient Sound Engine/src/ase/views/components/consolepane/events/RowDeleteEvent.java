package ase.views.components.consolepane.events;

import ase.views.components.consolepane.ConsoleControlRow;

public class RowDeleteEvent extends RowEvent {
	public RowDeleteEvent(ConsoleControlRow row, int index) {
		super(row, index);
	}
}
