package ase.views.components.consolepane;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;

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
		
		addChangeListener((ChangeEvent evt) -> {
			int currentSelection = getSelectedIndex();
			
			if (currentSelection != this.soundscapeSet.activeSoundscapeIndex
					&& currentSelection > -1) {
				//turn off current soundscape so that when user comes back, it doesn't start playing automatically
				opsMgr.setSoundscapeIsPlaying(section, false);
				
				opsMgr.setActiveSoundscape(section, this.soundscapeSet.getSoundscapeAtIndex(currentSelection).ssid);
			}
		});
		
		initTabs();
		
		opsMgr.eventBus.register(this);
	}
	
	private void initTabs() {
		
		int count = 0;
		for (SoundscapeModel ss : soundscapeSet) {
			SoundscapeTab newTab = new SoundscapeTab(settings,ss, section);
			SoundscapeTabTitle tabTitle = new SoundscapeTabTitle(settings, ss.name, section, count);
			add(newTab);
			setTabComponentAt(count, tabTitle);
			
			count++;
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
		
		//if 1st 2 conditions indicate a soundscape was deleted. Last comparison checks to see
		//if a tab was closed in the GUI, and so the tab representing the soundscape being
		//destroyed is already gone
		if (evt.soundscape == null && evt.ssIndex > -1
				&& oldModel.getTotalSoundscapes() == getTabCount()) {
			SoundscapeTab deadTab = (SoundscapeTab) getComponentAt(evt.ssIndex);
			SoundscapeTabTitle tabTitle = (SoundscapeTabTitle) getTabComponentAt(evt.ssIndex);
			deadTab.destroy();
			tabTitle.destroy();
			remove(evt.ssIndex);
			
			//reset tab title component index to account for gap
			for (int i = evt.ssIndex; i < getTabCount(); i++) {
				SoundscapeTabTitle nextTabTitle = (SoundscapeTabTitle) getTabComponentAt(i);
				nextTabTitle.setIndex(i);
			}
			
		} else if (evt.soundscape != null && evt.ssIndex == oldModel.getTotalSoundscapes()) { //Check to see if there is a new soundscape
			SoundscapeTab newTab = new SoundscapeTab(settings, evt.soundscape, section);
			SoundscapeTabTitle tabTitle = new SoundscapeTabTitle(settings, evt.soundscape.name, section, evt.ssIndex);
			add(evt.soundscape.name, newTab);
			setTabComponentAt(evt.ssIndex, tabTitle);
			
			newTab.applySettings(new SettingsEvent());
			
		} else if (evt.soundscape != null) {
			SoundscapeTabTitle tabTitle = (SoundscapeTabTitle) getTabComponentAt(evt.ssIndex);
			tabTitle.setTitle(evt.soundscape.name);
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
