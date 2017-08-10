package ase.views.frames;

import static ase.operations.Log.LogLevel.DEV;
import static ase.operations.OperationsManager.opsMgr;

import com.google.common.eventbus.Subscribe;

import ase.views.GuiSettings;
import ase.views.events.SettingsEvent;

public class MetadataFrame extends SubFrame {
	private static final long serialVersionUID = -7107741943967677015L;

	MetadataFrame (GuiSettings settings) {
		super("ASE Change Metadata", settings);
		
		this.setSize(settings.metadataFrameDefaultSize);
	}
	
	@Subscribe public void applySettings(SettingsEvent e) {
		super.applySettings(e);
		
	}

}
