package ase.views.components.consolepane;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import ase.operations.SoundscapeModel;
import ase.operations.SoundscapeSetModel;
import ase.operations.TestDataProvider;
import ase.main.Main;
import ase.operations.OperationsManager.Sections;
import ase.operations.events.ChangedSoundscapeSetEvent;
import ase.views.GuiSettings;
import ase.views.InvalidModelException;
import ase.views.events.SettingsEvent;
import ase.views.navigation.events.QuitEvent;

import static ase.operations.OperationsManager.opsMgr;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Console extends JTabbedPane {
	private static final long serialVersionUID = 2059540762779066181L;

	private GuiSettings settings;
	public final Sections section;
	
	private SoundscapeSetModel soundscapeSet;
	
	public Console(GuiSettings settings, Sections section, SoundscapeSetModel soundscapeSet) {
		this.settings = settings;
		this.section = section;
		this.soundscapeSet = soundscapeSet;

		setMinimumSize(new java.awt.Dimension(460, 200));
		setPreferredSize(new java.awt.Dimension(460, 260));
		
		initTabs();
		
		opsMgr.eventBus.register(this);
	}
	
	private void initTabs() {
		for (SoundscapeModel ss : soundscapeSet) {
			SoundscapeTab newTab = new SoundscapeTab(settings,ss, section);
			add(ss.name, newTab);
		}
		
		setSelectedIndex(soundscapeSet.activeSoundscapeIndex);
	}
	//Listening to Operations
	@Subscribe public void applySettings(SettingsEvent e) {
		if (e.getNewSettings() != null) {
			this.settings = e.getNewSettings();
		}
		
		setBackground(settings.foregroundColor);
	}
	
	@Subscribe public void changedSoundscapeSetListener(ChangedSoundscapeSetEvent evt) {
		if (evt.section != section) {return;}
		
		SoundscapeSetModel oldModel = soundscapeSet;
		soundscapeSet = evt.console;
		
		if (oldModel == soundscapeSet) {
			return;
		}
		
		if (evt.soundscape == null && evt.ssIndex > -1) {
			SoundscapeTab deadTab = (SoundscapeTab) getComponentAt(evt.ssIndex);
			deadTab.destroy();
			remove(evt.ssIndex);
			
		} else if (evt.soundscape != null && evt.ssIndex == oldModel.getTotalSoundscapes()) { //Check to see if there is a new soundscape
			SoundscapeTab newTab = new SoundscapeTab(settings, evt.soundscape, section);
			add(evt.soundscape.name, newTab);
			
			newTab.applySettings(new SettingsEvent());
			
		} else if (evt.soundscape != null) {
			setTitleAt(evt.ssIndex, evt.soundscape.name);
		}
		
		//Each individual tab is in charge of handling updated soundscapes
		//with the ChangedSoundscapeEvent
		if (getTabCount() > 0) {
			setSelectedIndex(soundscapeSet.activeSoundscapeIndex);
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		
		SoundscapeModel ss = TestDataProvider.testSoundscape(
				new String[] {".\\03_Waves.mp3", ".\\klaxon alarm aoogah.mp3", ".\\bowling ambience.mp3"});
		
		Main m = new Main();
		
		opsMgr.removeSoundscape(Sections.CONSOLE1, 0);
		opsMgr.addSoundscape(Sections.CONSOLE1, ss);
	}
}
