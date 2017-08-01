package ase.views.navigation;

import static ase.operations.OperationsManager.opsMgr;

import javax.swing.JMenuBar;

import ase.views.GuiSettings;

public class AseMenuBar extends JMenuBar {
	
	public AseMenuBar(GuiSettings settings) {
		
		this.setFont(settings.smallFont);
		
		//Add menus
		this.add(new FileMenu(settings));
		
		//register for event bus
		opsMgr.eventBus.register(this);
	}
}
