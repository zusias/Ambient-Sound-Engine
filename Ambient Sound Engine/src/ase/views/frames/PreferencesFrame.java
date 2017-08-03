package ase.views.frames;

import ase.views.GuiSettings;

public class PreferencesFrame extends SubFrame {

	public PreferencesFrame(GuiSettings settings) {
		super("ASE Preferences", settings);

		applySettings();
	}
	
	public void applySettings() {
		this.setSize(settings.preferencesFrameDefaultSize);
	}

}
