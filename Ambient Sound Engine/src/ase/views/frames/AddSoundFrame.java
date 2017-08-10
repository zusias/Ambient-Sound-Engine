package ase.views.frames;

import static ase.operations.OperationsManager.opsMgr;

import com.google.common.eventbus.Subscribe;

import static ase.operations.Log.LogLevel.*;

import ase.views.GuiSettings;
import ase.views.events.SettingsEvent;

public class AddSoundFrame extends SubFrame {
	private static final long serialVersionUID = -2847533843827430697L;
	
	AddSoundFrame (GuiSettings settings) {
		super("ASE Add a Sound File", settings);
		this.setSize(settings.addSoundFrameDefaultSize);
	}
	
	@Subscribe public void applySettings(SettingsEvent e) {
		super.applySettings(e);
	}
}
