package ase.views.navigation.events;

import java.awt.event.ActionEvent;

public abstract class NavigationEvent {
	private final ActionEvent e;
	
	public ActionEvent getEvent() {
		return e;
	}
	
	public NavigationEvent (ActionEvent e) {
		this.e = e;
	}
	
	public NavigationEvent () {
		this.e = null;
	}
}
