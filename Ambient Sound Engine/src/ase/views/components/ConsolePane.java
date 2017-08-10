package ase.views.components;

import javax.swing.JPanel;

import com.google.common.eventbus.Subscribe;

import ase.views.GuiSettings;
import ase.views.events.SettingsEvent;
import static ase.operations.OperationsManager.opsMgr;

public class ConsolePane extends JPanel {
	private static final long serialVersionUID = 62796215627796661L;

	private GuiSettings settings;
	
	public ConsolePane(GuiSettings settings) {
		this.settings = settings;
		
		opsMgr.eventBus.register(this);
	}
	
	@Subscribe public void applySettings(SettingsEvent e) {
		if (e.getNewSettings() != null) {
			settings = e.getNewSettings();
		}
		
		setBackground(settings.backgroundColor);
	}
}
