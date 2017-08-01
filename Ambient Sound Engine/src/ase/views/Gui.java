package ase.views;

//GUI imports
import javax.swing.JFrame;

//ASE Views
import ase.views.navigation.AseMenuBar;
import ase.views.navigation.events.QuitEvent;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

//ASE Operations
import static ase.operations.OperationsManager.opsMgr;
import static ase.operations.Log.LogLevel.*;

//Guava
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class Gui extends JFrame {
	private static final long serialVersionUID = -7766031219620439138L;
	private final GuiSettings settings = new GuiSettings();
	private boolean isOpen = true;
	
	public Gui () {
		super("Ambient Sound Engine");
		this.setName("ASE");
		
		opsMgr.logger.log(DEBUG, "Gui constructor");
		this.addWindowListener(new WindowListeners());
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		//set styles
		this.setMinimumSize(settings.minimumWindowSize);
		this.setBackground(settings.backgroundColor);
		
		//init components
		this.setJMenuBar(new AseMenuBar(settings));
		
		this.setVisible(true);
		
		//register for events on EventBus
		opsMgr.eventBus.register(this);
	}
	
	@Subscribe public void quitCleanup(QuitEvent qe) {
		onWindowClosing();
		this.dispose();
	}
	
	public void onWindowClosing () {
		opsMgr.logger.log(DEV, "Window closing");
		isOpen = false;
	}
	
	public boolean isOpen() {
		return isOpen;
	}
	
	private class WindowListeners extends WindowAdapter {
		@Override
		public void windowClosing (WindowEvent e) {
			onWindowClosing();
		}
	}
}