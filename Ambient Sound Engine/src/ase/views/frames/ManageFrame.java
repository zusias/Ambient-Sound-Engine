package ase.views.frames;

import static ase.operations.Log.LogLevel.DEV;
import static ase.operations.OperationsManager.opsMgr;

import ase.views.GuiSettings;

public class ManageFrame extends SubFrame {
	private static final long serialVersionUID = -1133586752432492746L;

	ManageFrame (GuiSettings settings) {
		super("ASE Manage", settings);
		
		applySettings();
	}
	
	public void applySettings() {
		this.setSize(settings.manageFrameDefaultSize);
	}
}
