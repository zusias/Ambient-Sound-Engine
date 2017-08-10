package ase.views.frames;

import com.google.common.eventbus.Subscribe;

import ase.views.GuiSettings;
import ase.views.events.SettingsEvent;

public class AboutFrame extends SubFrame {
	private static final long serialVersionUID = 8864837387600530238L;
	
	public AboutFrame(GuiSettings settings) {
		super("ASE About", settings);
		
		this.setSize(settings.aboutFrameDefaultSize);
	}
	
	@Subscribe public void applySettings(SettingsEvent e) {
		super.applySettings(e);
	}
}