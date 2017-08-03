package ase.views.navigation;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import static ase.operations.OperationsManager.opsMgr;
import static ase.operations.Log.LogLevel.*;

import ase.views.GuiSettings;
import ase.views.frames.PreferencesFrame;
import ase.views.frames.SubFrame;

public class SettingsMenu extends JMenu {
	private static final long serialVersionUID = 6161566413883587365L;
	
	private final JMenuItem preferences;
	private final GuiSettings settings;
	
	public SettingsMenu(GuiSettings settings) {
		super("Settings");
		
		this.settings = settings;
		
		preferences = new JMenuItem("Preferences");
		preferences.addActionListener((e) -> {
			opsMgr.logger.log(DEV, "Open Preferences Window");
			SubFrame.launchFrame(PreferencesFrame.class, settings);
		});
		
		this.add(preferences);
		
		//register for event bus
		opsMgr.eventBus.register(this);
	}
	
	public void applySettings() {
		this.setFont(settings.smallFont);
		
		preferences.setFont(settings.smallFont);
	}
}
