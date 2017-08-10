package ase.views.events;

import ase.views.GuiSettings;

public class SettingsEvent {
	private final GuiSettings newSettings;
	
	public SettingsEvent() {
		newSettings = null;
	}
	
	public SettingsEvent(GuiSettings newSettings) {
		this.newSettings = newSettings;
	}
	
	public GuiSettings getNewSettings() {
		return newSettings;
	}
}
