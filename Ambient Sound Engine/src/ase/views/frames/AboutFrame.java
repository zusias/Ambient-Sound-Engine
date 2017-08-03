package ase.views.frames;

import ase.views.GuiSettings;

public class AboutFrame extends SubFrame {
	private static final long serialVersionUID = 8864837387600530238L;

	public AboutFrame(GuiSettings settings) {
		super("ASE About", settings);

		applySettings();
	}
	
	public void applySettings() {
		this.setSize(settings.aboutFrameDefaultSize);
	}
}