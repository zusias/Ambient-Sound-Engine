package ase.views.frames;

import static ase.operations.Log.LogLevel.DEV;
import static ase.operations.OperationsManager.opsMgr;

import com.google.common.eventbus.Subscribe;

import ase.views.GuiSettings;
import ase.views.events.SettingsEvent;

public class ManageFrame extends SubFrame {
	private static final long serialVersionUID = -1133586752432492746L;

	ManageFrame (GuiSettings settings) {
		super("ASE Manage", settings);
		
		this.setSize(settings.manageFrameDefaultSize);
	}
	
	@Subscribe public void applySettings(SettingsEvent e) {
		super.applySettings(e);
	}
}
