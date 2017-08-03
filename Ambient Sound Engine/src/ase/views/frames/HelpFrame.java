package ase.views.frames;

import ase.views.GuiSettings;

public class HelpFrame extends SubFrame {
	private static final long serialVersionUID = 7541442306340727236L;
	
	public HelpFrame(GuiSettings settings) {
		super("ASE Help", settings);

		applySettings();
	}
	
	public void applySettings() {
		this.setSize(settings.helpFrameDefaultSize);
	}
}
