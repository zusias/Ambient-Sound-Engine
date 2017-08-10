package ase.views.navigation;

import static ase.operations.OperationsManager.opsMgr;

import javax.swing.JMenuBar;

import com.google.common.eventbus.Subscribe;

import ase.views.GuiSettings;
import ase.views.events.SettingsEvent;

public class AseMenuBar extends JMenuBar {
	private GuiSettings settings;
	
	public AseMenuBar(GuiSettings settings) {
		this.settings = settings;
		
		//Add menus
		this.add(new FileMenu(settings));
		this.add(new SettingsMenu(settings));
		this.add(new HelpMenu(settings));
		
		//register for event bus
		opsMgr.eventBus.register(this);
	}
	
	@Subscribe public void applySettings(SettingsEvent e) {
		if (e.getNewSettings() != null) {
			settings = e.getNewSettings();
		}
		
		this.setFont(settings.smallFont);
	}
}
