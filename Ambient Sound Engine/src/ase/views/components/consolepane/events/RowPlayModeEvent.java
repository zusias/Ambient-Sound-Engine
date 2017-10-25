package ase.views.components.consolepane.events;

import ase.views.components.consolepane.SoundControlRow;

public class RowPlayModeEvent extends RowEvent {
	public final boolean isRightClick;
	public final SoundControlRow row;
	
	public RowPlayModeEvent(SoundControlRow row, int index) {
		super(row, index);
		this.row = row;
		this.isRightClick = false;
	}
	
	public RowPlayModeEvent(SoundControlRow row, int index, boolean isRightClick) {
		super(row, index);
		this.row = row;
		this.isRightClick = isRightClick;
	}
}
