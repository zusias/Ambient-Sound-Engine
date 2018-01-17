package ase.views;

import javax.swing.ImageIcon;
//GUI imports
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import ase.database.DatabaseException;
import ase.database.IDatabase;
import ase.views.components.consolepane.ConsolePane;
import ase.views.components.consolepane.RandomSettingsDialog;
import ase.views.components.consolepane.events.LaunchRandomSettingsEvent;
import ase.views.components.searchpane.SearchPane;
import ase.views.events.SettingsEvent;
import ase.views.frames.SubFrame;
//ASE Views
import ase.views.navigation.AseMenuBar;
import ase.views.navigation.events.QuitEvent;
import ase.views.navigation.events.ShutdownEvent;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

//ASE Operations
import static ase.operations.OperationsManager.opsMgr;
import static ase.operations.Log.LogLevel.*;

//Guava
import com.google.common.eventbus.Subscribe;

public class Gui extends JFrame {
	private static Gui gui = null;
	
	private static final long serialVersionUID = -7766031219620439138L;
	private GuiSettings settings = new GuiSettings.SettingsBuilder().build();
	private boolean isOpen = true;
	
	//Components
	private final AseMenuBar menuBar;
	private final SearchPane searchPane;
	private final ConsolePane consolePane;
	
	//GridBagConstraint instances
	private GridBagConstraints searchPaneGbc = new GridBagConstraints();
	private GridBagConstraints consolePaneGbc = new GridBagConstraints();
	
	public static Gui initGui() {
		if (gui == null) {
			gui = new Gui();
		}
		
		return gui;
	}
	
	private Gui () {
		super("Ambient Sound Engine");
		this.setName("ASE");
		
		opsMgr.logger.log(DEBUG, "Gui constructor");
		this.addWindowListener(new WindowListeners());
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); //will close manually after save confirmations
		this.setVisible(true);
		
		//static settings that won't change
		this.setResizable(true);
		
		Container contentPane = this.getContentPane();
		
		contentPane.setLayout(new GridBagLayout());

		//init components
		setupGridBagConstraints();
		
		this.menuBar = new AseMenuBar(settings);
		this.setJMenuBar(menuBar);
		
		this.searchPane = new SearchPane(settings);
		contentPane.add(searchPane, searchPaneGbc);
		
		this.consolePane = new ConsolePane(settings);
		contentPane.add(consolePane, consolePaneGbc);
		
		//register for events on EventBus
		opsMgr.eventBus.register(this);
		
		//set location / size
		setSizeLocation();
		
		//Apply settings for all components
		opsMgr.eventBus.post(new SettingsEvent());
	}
	
	private void setupGridBagConstraints() {
		//Search Pane
		searchPaneGbc.weightx = 0.3;
		searchPaneGbc.weighty = 1.0;
		searchPaneGbc.fill = GridBagConstraints.BOTH;
		searchPaneGbc.gridx = 0;
		searchPaneGbc.gridy = 0;
		searchPaneGbc.anchor = GridBagConstraints.NORTHWEST;
		
		//Console Pane
		consolePaneGbc.fill = GridBagConstraints.BOTH;
		consolePaneGbc.weighty = 1.0;
		consolePaneGbc.weightx = 1.0;
		consolePaneGbc.gridx = 1;
		consolePaneGbc.gridy = 0;
		consolePaneGbc.anchor = GridBagConstraints.EAST;
	}
	
	@Subscribe public void quitCleanup(QuitEvent qe) {
		if (confirmClose()) {
			closeWindow();
		}
	}
	
	public boolean confirmClose () {
		//check for unsaved changes
		if (consolePane.hasUnsavedChanges()) {
			int quitConfirmation = JOptionPane.showConfirmDialog(
				this,
				"You have unsaved changes. Do you really want to quit?",
				"Unsaved Changes",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE);
			
			if (quitConfirmation == JOptionPane.NO_OPTION) { return false; }
		}
		
		return true;
	}
	
	private void closeWindow() {
		opsMgr.logger.log(DEV, "Window closing");
		isOpen = false;
		SubFrame.disposeAllSubFrames();
		
		try {
			saveState();
		} catch (DatabaseException dbEx) {
			opsMgr.logger.log(PROD, "Unable to save GUI state");
			opsMgr.logger.log(DEV, "Database Error! " + dbEx.getMessage());
			opsMgr.logger.log(DEBUG, dbEx.getStackTrace());
		}
		
		this.dispose();
		
		opsMgr.eventBus.post(new ShutdownEvent());
	}
	
	private void saveState() throws DatabaseException {
		IDatabase db = opsMgr.getDatabase();
		
		db.setSetting("width", String.valueOf(this.getWidth()));
		db.setSetting("height", String.valueOf(this.getHeight()));
		
		Point location = this.getLocation();
		db.setSetting("xLocation", String.valueOf((int) location.getX()));
		db.setSetting("yLocation", String.valueOf((int) location.getY()));
	}
	
	private void setSizeLocation() {
		IDatabase db = opsMgr.getDatabase();
		
		try {

			this.setSize(
				Integer.parseInt(db.getSetting("width")),
				Integer.parseInt(db.getSetting("height")));
			this.setLocation(
				Integer.parseInt(db.getSetting("xLocation")),
				Integer.parseInt(db.getSetting("yLocation")));
			
		} catch (DatabaseException dbEx) {
			opsMgr.logger.log(PROD, "Unable to retrieve GUI state");
			opsMgr.logger.log(DEV, "Database Error! " + dbEx.getMessage());
			opsMgr.logger.log(DEBUG, dbEx.getStackTrace());
		}
	}
	
	public boolean isOpen() {
		return isOpen;
	}
	
	@Subscribe public void applySettings(SettingsEvent e) {
		if (e.getNewSettings() != null) {
			this.settings = e.getNewSettings();
		}
		
		this.setMinimumSize(settings.minimumWindowSize);
		this.getContentPane().setBackground(settings.backgroundColor);
	}
	
	@Subscribe public void launchRandomSettingsDialog(LaunchRandomSettingsEvent evt) {
		RandomSettingsDialog dialog = new RandomSettingsDialog(this, evt);
	}
	
	private class WindowListeners extends WindowAdapter {
		@Override
		public void windowClosing (WindowEvent e) {
			opsMgr.eventBus.post(new QuitEvent(e));
		}
	}
}