package ase.views;

import javax.swing.ImageIcon;
//GUI imports
import javax.swing.JFrame;

import ase.views.components.consolepane.ConsolePane;
import ase.views.components.consolepane.RandomSettingsDialog;
import ase.views.components.consolepane.events.LaunchRandomSettingsEvent;
import ase.views.components.searchpane.SearchPane;
import ase.views.events.SettingsEvent;
import ase.views.frames.SubFrame;
//ASE Views
import ase.views.navigation.AseMenuBar;
import ase.views.navigation.events.QuitEvent;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
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
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
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
		onWindowClosing();
		this.dispose();
	}
	
	public void onWindowClosing () {
		opsMgr.logger.log(DEV, "Window closing");
		isOpen = false;
		SubFrame.disposeAllSubFrames();
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