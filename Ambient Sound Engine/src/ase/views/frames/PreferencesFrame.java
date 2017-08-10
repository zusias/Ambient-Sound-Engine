package ase.views.frames;

import com.google.common.eventbus.Subscribe;

import ase.views.GuiSettings;
import ase.views.events.SettingsEvent;

public class PreferencesFrame extends SubFrame {

	public PreferencesFrame(GuiSettings settings) {
		super("ASE Preferences", settings);
		
		this.setSize(settings.preferencesFrameDefaultSize);
	}
	
	@Subscribe public void applySettings(SettingsEvent e) {
		super.applySettings(e);
	}

}
