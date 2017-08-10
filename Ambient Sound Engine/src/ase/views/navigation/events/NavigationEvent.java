package ase.views.navigation.events;

import java.awt.AWTEvent;

public abstract class NavigationEvent {
	private final AWTEvent e;
	
	public AWTEvent getEvent() {
		return e;
	}
	
	public NavigationEvent (AWTEvent e) {
		this.e = e;
	}
	
	public NavigationEvent () {
		this.e = null;
	}
}
