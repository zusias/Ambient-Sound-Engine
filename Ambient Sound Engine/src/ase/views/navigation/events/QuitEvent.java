package ase.views.navigation.events;

import java.awt.AWTEvent;

public class QuitEvent extends NavigationEvent {
	public QuitEvent(AWTEvent e) {
		super(e);
	}
	
	public QuitEvent() {
		super();
	}
}
