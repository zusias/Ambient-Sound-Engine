package ase.views.components.consolepane;

import com.google.common.eventbus.EventBus;

import ase.operations.SoundscapeModel;
import ase.views.GuiSettings;
import static ase.operations.SoundscapeModel.PlayState.*;

public class SoundscapeControlRow extends ConsoleControlRow {
	
	private SoundscapeModel soundscape;
	
	public SoundscapeControlRow(GuiSettings settings, SoundscapeModel soundscape, int rowIndex, EventBus tabEventBus) {
		super(settings, rowIndex, tabEventBus);
		
		this.soundscape = soundscape;
		
		this.layout.columnWidths[3] = 0; //close gap left by playMode button

		title.setText(soundscape.name);
		
		volumeBar.setValue(getVolume());
	}
	
	public void updateModel(SoundscapeModel soundscape) {
		this.soundscape = soundscape;
		
		volumeBar.setValue(getVolume());
		
		if (soundscape.playState == STOPPED) {
			this.playButton.setIcon(SPEAKER_OFF_ICON);
		} else {
			this.playButton.setIcon(SPEAKER_ON_ICON);
		}
		
		title.setText(soundscape.name);
	}
	
	@Override
	public int getVolume() {
		return (int) (soundscape.masterVolume * 1000);
	}
}
