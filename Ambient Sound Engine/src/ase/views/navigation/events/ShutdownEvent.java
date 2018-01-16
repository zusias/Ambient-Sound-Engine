package ase.views.navigation.events;

import java.awt.AWTEvent;

public class ShutdownEvent extends NavigationEvent {
	public ShutdownEvent(AWTEvent e) {
		super(e);
	}
	
	public ShutdownEvent() {
		super();
	}
}
