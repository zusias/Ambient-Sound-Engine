package ase.views.frames;

import com.google.common.eventbus.Subscribe;

import ase.views.GuiSettings;
import ase.views.events.SettingsEvent;

public class HelpFrame extends SubFrame {
	private static final long serialVersionUID = 7541442306340727236L;
	
	public HelpFrame(GuiSettings settings) {
		super("ASE Help", settings);
		
		//static settings that won't change
		this.setSize(settings.helpFrameDefaultSize);
	}
	
	@Subscribe public void applySettings(SettingsEvent e) {
		super.applySettings(e);
	}
}
