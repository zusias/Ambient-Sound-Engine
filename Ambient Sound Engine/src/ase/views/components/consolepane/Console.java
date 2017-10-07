package ase.views.components.consolepane;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import ase.operations.SoundscapeModel;
import ase.operations.TestDataProvider;
import ase.views.GuiSettings;
import ase.views.events.SettingsEvent;
import ase.views.navigation.events.QuitEvent;

import static ase.operations.OperationsManager.opsMgr;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Console extends JTabbedPane {
	private static final long serialVersionUID = 2059540762779066181L;

	private GuiSettings settings;
	private final JPanel blankPanel = new JPanel();
	private final JLabel blankPanelLabel = new JLabel("No Soundscape Loaded");
	
	private final EventBus eventBus;
	
	public Console(GuiSettings settings, EventBus eventBus) {
		this.settings = settings;
		this.eventBus = eventBus;

		setMinimumSize(new java.awt.Dimension(460, 200));
		setPreferredSize(new java.awt.Dimension(460, 260));
		
		//initialize with a blank panel
		blankPanel.add(blankPanelLabel);
		
		this.add("", blankPanel);
		
		eventBus.register(this);
	}
	
	@Subscribe public void applySettings(SettingsEvent e) {
		if (e.getNewSettings() != null) {
			this.settings = e.getNewSettings();
		}
		
		blankPanel.setBackground(settings.foregroundColor);
	}
	
	public static void main(String[] args) throws InterruptedException {
		JFrame testFrame = new JFrame();
		JTabbedPane testPane = new JTabbedPane();
		
		SoundscapeModel ss = TestDataProvider.testSoundscape(
				new String[] {".\\03_Waves.mp3", ".\\klaxon alarm aoogah.mp3", ".\\bowling ambience.mp3"});
		
		EventBus eventBus = new EventBus();
		testPane.add(ss.name, new SoundscapeTab(new GuiSettings.SettingsBuilder().build(), ss, eventBus));
		
		testFrame.add(testPane);
		
		testFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing (WindowEvent e) {
				System.exit(0);
			}
		});
		
		testFrame.setVisible(true);
		testFrame.setSize(500, 500);
		
		eventBus.post(new SettingsEvent());
	}
}
