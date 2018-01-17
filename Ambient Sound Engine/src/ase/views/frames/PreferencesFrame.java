package ase.views.frames;

import javax.swing.JLabel;

import com.google.common.eventbus.Subscribe;

import ase.views.GuiSettings;
import ase.views.events.SettingsEvent;

public class PreferencesFrame extends SubFrame {
	private static final long serialVersionUID = -2937425522333033445L;

	private final JLabel saveToLabel = new JLabel("Save Sound Files To...");
	
	public PreferencesFrame(GuiSettings settings) {
		super("ASE Preferences", settings);
		
		this.setSize(settings.preferencesFrameDefaultSize);
	}
	
	@Subscribe public void applySettings(SettingsEvent e) {
		super.applySettings(e);
	}

}
