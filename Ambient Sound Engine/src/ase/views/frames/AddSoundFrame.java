package ase.views.frames;

import static ase.operations.OperationsManager.opsMgr;
import static ase.operations.Log.LogLevel.*;

import ase.views.GuiSettings;

public class AddSoundFrame extends SubFrame {
	private static final long serialVersionUID = -2847533843827430697L;
	
	AddSoundFrame (GuiSettings settings) {
		super("ASE Add a Sound File", settings);
		
		applySettings();
	}
	
	public void applySettings() {
		this.setSize(settings.addSoundFrameDefaultSize);
	}
}
