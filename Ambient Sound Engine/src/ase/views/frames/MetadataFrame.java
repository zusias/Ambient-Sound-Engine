package ase.views.frames;

import static ase.operations.Log.LogLevel.DEV;
import static ase.operations.OperationsManager.opsMgr;

import ase.views.GuiSettings;

public class MetadataFrame extends SubFrame {
	private static final long serialVersionUID = -7107741943967677015L;

	MetadataFrame (GuiSettings settings) {
		super("ASE Change Metadata", settings);
		
		applySettings();
	}
	
	public void applySettings() {
		this.setSize(settings.metadataFrameDefaultSize);
	}

}
