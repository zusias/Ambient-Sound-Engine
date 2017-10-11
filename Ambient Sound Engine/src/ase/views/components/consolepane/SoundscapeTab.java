package ase.views.components.consolepane;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import ase.operations.OperationsManager.Sections;
import ase.operations.SoundModel;
import ase.operations.SoundscapeModel;
import ase.operations.SoundscapeModel.PlayState;
import ase.operations.events.ChangedSoundscapeEvent;
import ase.views.GuiSettings;
import ase.views.components.consolepane.events.RowClickedEvent;
import ase.views.components.consolepane.events.RowPlayPressedEvent;
import ase.views.events.SettingsEvent;
import static ase.operations.OperationsManager.opsMgr;
import static ase.operations.Log.LogLevel.*;

public class SoundscapeTab extends JPanel {
	private static final long serialVersionUID = 7911673691113487122L;
	
	private static final ImageIcon NEW_SOUNDSCAPE_ICON = new ImageIcon("newSoundscape.gif");
	private static final ImageIcon COPY_ICON = new ImageIcon("copy.gif");
	private static final ImageIcon SAVE_ICON = new ImageIcon("save.gif");
	
	private static final Color SINGLE_SELECT_COLOR = new Color(212, 247, 252);
	private static final Color MULTI_SELECT_COLOR = new Color(103, 221, 239);
	private static final Color SOUNDSCAPE_SELECT_COLOR = new Color(252, 239, 196);

	private GuiSettings settings;
	
	private final GridBagLayout layout = new GridBagLayout();
	
	private final EventBus tabEventBus = new EventBus(); //passed to row controllers
	
	//Soundscape control components
	private SoundscapeControlRow soundscapeController;
	private Vector<SoundControlRow> soundControllers = new Vector<>();
	
	private Vector<ConsoleControlRow> selectedRows = new Vector<>();
	
	private final JPanel soundscapeControlPane = new JPanel();
	private final GridLayout soundscapeControlPaneLayout = new GridLayout(1, 1);
	private final GridBagConstraints soundscapeControlPaneGbc = new GridBagConstraints();
	private final JScrollPane soundRowScroller = new JScrollPane();
	private final GridBagConstraints soundRowScrollerGbc = new GridBagConstraints();
	private final JPanel soundRowPanel = new JPanel();
	private final GridLayout soundRowPanelLayout = new GridLayout(0, 1);
	
	private final JSlider volumeController = new JSlider(0, 1000, 0);
	private final GridBagConstraints volumeControllerGbc = new GridBagConstraints();
	private boolean ignoreVolumeControl = false;
	
	private final JPanel buttonPanel = new JPanel();
	private final GridBagLayout buttonPanelLayout = new GridBagLayout();
	private final GridBagConstraints buttonPanelGbc = new GridBagConstraints();

	private final JButton newSoundscapeButton = new JButton(NEW_SOUNDSCAPE_ICON);
	private final GridBagConstraints newSoundscapeButtonGbc = new GridBagConstraints();
	private final JButton saveSoundscapeButton = new JButton(SAVE_ICON);
	private final GridBagConstraints saveSoundscapeButtonGbc = new GridBagConstraints();
	private final JButton copySoundscapeButton = new JButton(COPY_ICON);
	private final GridBagConstraints copySoundscapeButtonGbc = new GridBagConstraints();
	
	private final Dimension minimumSoundControllerSize = new Dimension(250, 50);
	
	//data model
	private SoundscapeModel soundscape;
	private Sections section; //so that this tab can inform Ops Mgr of changes without going up through hierarchy
	private boolean loadedInPreview = false;
	
	//state for volume control w/ multi-select rows
	private int volumeControlLastValue = -1;
	
	/**
	 * Access the model this tab represents. This is to help verify which tabs correspond
	 * with which soundscapes
	 * @return The SoundscapeModel this tab represents
	 */
	public SoundscapeModel getModel() {
		return soundscape;
	}
	
	public SoundscapeTab(GuiSettings settings, SoundscapeModel soundscape, Sections section) {
		this.settings = settings;
		this.soundscape = soundscape;
		this.section = section;
		
		setMinimumSize(new Dimension(450, 280));
		setMaximumSize(new Dimension(600, 390));
		setPreferredSize(new Dimension(457, 284));

		layout.columnWidths = new int[]{305, 48};
		layout.rowHeights = new int[]{35, 236};
		layout.columnWeights = new double[]{0.9, 0.1};
		layout.rowWeights = new double[]{0.0, 2.5}; 
		
		setLayout(layout);
		
		setupGridBagConstraints();
		
		Dimension soundscapeControlSize = new Dimension(300, 40);
		soundscapeControlPane.setMaximumSize(soundscapeControlSize);
		soundscapeControlPane.setMinimumSize(soundscapeControlSize);
		soundscapeControlPane.setPreferredSize(soundscapeControlSize);
		soundscapeControlPane.setLayout(soundscapeControlPaneLayout);
		add(soundscapeControlPane, soundscapeControlPaneGbc);

		soundscapeController = new SoundscapeControlRow(settings, soundscape, -1, tabEventBus);
		soundscapeControlPane.add(soundscapeController);
		soundscapeController.applySettings(new SettingsEvent());

		buttonPanelLayout.columnWidths = new int[]{16, 16, 16};
		buttonPanel.setLayout(buttonPanelLayout);
		
		newSoundscapeButton.setToolTipText("New Soundscape");
		buttonPanel.add(newSoundscapeButton, newSoundscapeButtonGbc);
		
		saveSoundscapeButton.setToolTipText("Save Soundscape");
		buttonPanel.add(saveSoundscapeButton, saveSoundscapeButtonGbc);
		
		copySoundscapeButton.setToolTipText("Copy Soundscape");
		buttonPanel.add(copySoundscapeButton, copySoundscapeButtonGbc);
		
		add(buttonPanel, buttonPanelGbc);
		
		add(soundRowScroller, soundRowScrollerGbc);
		soundRowPanel.setLayout(soundRowPanelLayout);
		soundRowScroller.setViewportView(soundRowPanel);
		initializeSoundControllers();
		
		volumeController.setOrientation(SwingConstants.VERTICAL);
		volumeController.setMaximumSize(new Dimension(32767, 50));
		volumeController.setEnabled(false);
		volumeController.setMajorTickSpacing(100);
		volumeController.setPaintTicks(true);
		add(volumeController, volumeControllerGbc);
		
		volumeController.addChangeListener(this::handleVolumeChange);
		
		opsMgr.eventBus.register(this);
		
		//register to own event bus to listen to row controller events
		tabEventBus.register(this);
	}
	
	private void initializeSoundControllers() {
		int i = 0;
		for (SoundModel sound : soundscape) {
			SoundControlRow soundController = new SoundControlRow(settings, sound, i++, tabEventBus);
			
			soundController.setMinimumSize(minimumSoundControllerSize);
			soundControllers.addElement(soundController);
			soundRowPanel.add(soundController);
			
			soundController.applySettings(new SettingsEvent());
		}
	}
	
	private void setupGridBagConstraints() {
		//Soundscape Control Pane
		soundscapeControlPaneGbc.weighty = 0.1;
		soundscapeControlPaneGbc.weightx = 0.9;
		soundscapeControlPaneGbc.anchor = GridBagConstraints.WEST;
		soundscapeControlPaneGbc.fill = GridBagConstraints.HORIZONTAL;
		soundscapeControlPaneGbc.insets = new Insets(5, 5, 5, 10);
		soundscapeControlPaneGbc.gridx = 0;
		soundscapeControlPaneGbc.gridy = 0;
		
		//Button Panel
		buttonPanelGbc.gridx = 1;
		buttonPanelGbc.gridy = 0;
		buttonPanelGbc.anchor = GridBagConstraints.CENTER;
		buttonPanelGbc.fill = GridBagConstraints.NONE;
		
		Insets buttonInsets = new Insets(5, 0, 5, 5);
		
		newSoundscapeButtonGbc.insets = buttonInsets;
		newSoundscapeButtonGbc.gridx = 0;
		newSoundscapeButtonGbc.gridy = 0;

		saveSoundscapeButtonGbc.insets = buttonInsets;
		saveSoundscapeButtonGbc.gridx = 1;
		saveSoundscapeButtonGbc.gridy = 0;

		copySoundscapeButtonGbc.insets = buttonInsets;
		copySoundscapeButtonGbc.gridx = 2;
		copySoundscapeButtonGbc.gridy = 0;
		
		//Sound Row Scroller
		soundRowScrollerGbc.weightx = 0.9;
		soundRowScrollerGbc.fill = GridBagConstraints.BOTH;
		soundRowScrollerGbc.insets = new Insets(0, 5, 5, 10);
		soundRowScrollerGbc.gridx = 0;
		soundRowScrollerGbc.gridy = 1;
		
		//Volume Control
		volumeControllerGbc.fill = GridBagConstraints.VERTICAL;
		volumeControllerGbc.gridx = 1;
		volumeControllerGbc.gridy = 1;
		volumeControllerGbc.weightx = 0;
	}
	
	public void destroy() {
		opsMgr.eventBus.unregister(this);
		tabEventBus.unregister(this);
		
		soundscapeController.destroy();
		for (SoundControlRow row : soundControllers) {
			row.destroy();
		}
	}
	
	//handle various events
	
	//Swing Events - Subscriber Model
	public void handleVolumeChange(ChangeEvent evt) {
		if (ignoreVolumeControl) {return;}
		
		double newVolume = (double) volumeController.getValue() / 1000.0;
		opsMgr.logger.log(DEBUG, "Setting Volume to " + newVolume);
		
		if (selectedRows.size() == 0) {
			opsMgr.setSoundscapeVolume(section, newVolume);
		} else if (selectedRows.size() == 1){
			ConsoleControlRow row = selectedRows.get(0);
			opsMgr.setSoundVolume(section, row.getIndex(), newVolume);
		} else {
			//calculate volume diff for each sound
			for (ConsoleControlRow row : selectedRows) {
				//do something
				double soundVolume = (double) 
					calculateSoundDiff(
						volumeControlLastValue,
						volumeController.getValue(),
						row.getVolume()) / 1000.0;
				
				opsMgr.setSoundVolume(section, row.getIndex(), soundVolume);
			}
			
			volumeControlLastValue = volumeController.getValue();
		}
	}
	
	private int calculateSoundDiff(int lastVolumeValue, int currentVolumeValue, int rowVolumeValue) {
		//TODO implement
		return 500;
	}
	
	//Operations events
	@Subscribe public void applySettings(SettingsEvent e) {
		if (e.getNewSettings() != null) {
			settings = e.getNewSettings();
		}
		
		setBackground(settings.foregroundColor);
		buttonPanel.setBackground(settings.foregroundColor);
		volumeController.setBackground(settings.foregroundColor);
		
		//buttons
		newSoundscapeButton.setMaximumSize(settings.buttonSize);
		newSoundscapeButton.setMinimumSize(settings.buttonSize);
		newSoundscapeButton.setPreferredSize(settings.buttonSize);
		
		saveSoundscapeButton.setMaximumSize(settings.buttonSize);
		saveSoundscapeButton.setMinimumSize(settings.buttonSize);
		saveSoundscapeButton.setPreferredSize(settings.buttonSize);

		copySoundscapeButton.setMaximumSize(settings.buttonSize);
		copySoundscapeButton.setMinimumSize(settings.buttonSize);
		copySoundscapeButton.setPreferredSize(settings.buttonSize);
	}
	
	@Subscribe public void handleChangedSoundscape(ChangedSoundscapeEvent evt) {
		//handle when it is a preview
		if (evt.section == Sections.PREVIEW) {
			handlePreview(evt);
			return;
		} else if (evt.soundscape.runtimeId != soundscape.runtimeId) {
			return;
		}
		
		soundscape = evt.soundscape;
		soundscapeController.updateModel(evt.soundscape);
		
		//check for specific sound update
		if (evt.sound != null) {
			if (evt.soundIndex == soundControllers.size()) {
				SoundControlRow soundController = new SoundControlRow(settings, evt.sound, evt.soundIndex, tabEventBus);
				
				soundController.setMinimumSize(minimumSoundControllerSize);
				soundControllers.addElement(soundController);
				soundRowPanel.add(soundController);
				
				soundController.applySettings(new SettingsEvent());
			} else {
				soundControllers.get(evt.soundIndex).updateModel(evt.sound);
			}
		} else if (evt.soundIndex != -1) {
			//removed sound
			SoundControlRow deadRow = soundControllers.remove(evt.soundIndex);
			soundRowPanel.remove(deadRow);
			deadRow.destroy();
			
			//decrement index for all rows after the row that was removed
			for (int i = evt.soundIndex; i < soundControllers.size(); i++) {
				soundControllers.get(i).decrementIndex();
			}
		}
	}
	
	private void handlePreview(ChangedSoundscapeEvent evt) {
		if (loadedInPreview && evt.soundscape.runtimeId != soundscape.runtimeId) {
			loadedInPreview = false;
			soundscapeController.setPreviewOff();
			
			for (SoundControlRow row : soundControllers) {
				row.setPreviewOff();
			}
		} else if (!loadedInPreview && evt.soundscape.runtimeId == soundscape.runtimeId) {
			loadedInPreview = true;
		}
		
		//now turn on the buttons if necessary
		if (loadedInPreview) {
			if (evt.soundscape.playState == PlayState.PLAYING) {
				soundscapeController.setPreviewOn();
			}
			
			int count = 0;
			for (SoundModel sound : evt.soundscape) {
				if (sound.isPlaying) {
					soundControllers.get(count).setPreviewOn();
				} else {
					soundControllers.get(count).setPreviewOff();
				}
				count++;
			}
		}
		
		
	}
	
	//ConsolePane events
	@Subscribe public void handleRowClicked(RowClickedEvent evt) {
		if (evt.mouseButton == 1 || evt.index == -1 || selectedRows.size() == 0) {
			setSingleRowFocus(evt.row, evt.index);
		} else {
			setMultiRowFocus(evt.row, evt.index);
		}
	}
	
	private void setSingleRowFocus(ConsoleControlRow row, int index) {
		resetRowFocus();
		
		
		if (index > -1) {
			row.setBackground(SINGLE_SELECT_COLOR);
			selectedRows.addElement(row);
		} else {
			row.setBackground(SOUNDSCAPE_SELECT_COLOR);
		}
		
		volumeController.setValue(row.getVolume());
		volumeController.setEnabled(true);
		ignoreVolumeControl = false;
	}
	
	private void setMultiRowFocus(ConsoleControlRow row, int index) {
		if (selectedRows.removeElement(row)) {
			row.setBackground(Color.white);
			
			if (selectedRows.size() == 1) {
				ConsoleControlRow remainingRow = selectedRows.get(0);
				remainingRow.setBackground(SINGLE_SELECT_COLOR);
				volumeController.setValue(remainingRow.getVolume());
				
				volumeControlLastValue = -1;
			}
		} else {
			if (selectedRows.size() == 1) {
				selectedRows.get(0).setBackground(MULTI_SELECT_COLOR);
			}
			
			selectedRows.addElement(row);
			row.setBackground(MULTI_SELECT_COLOR);

			ignoreVolumeControl = true;
			int volume = getAverageSelectedSoundVolume();
			volumeController.setValue(getAverageSelectedSoundVolume());
			ignoreVolumeControl = false;
			
			volumeControlLastValue = volume;
		}
	}
	
	private int getAverageSelectedSoundVolume() {
		int sum = 0;
		for (ConsoleControlRow row : selectedRows) {
			sum += row.getVolume();
		}
		
		return sum / selectedRows.size();
	}
	
	private void resetRowFocus() {
		soundscapeController.setBackground(Color.white);

		for (SoundControlRow soundController : soundControllers) {
			soundController.setBackground(Color.white);
		}

		selectedRows.clear();
		
		ignoreVolumeControl = true;
		volumeController.setEnabled(false);
		volumeController.setValue(0);
	}
	
	@Subscribe public void handlePlayEvent(RowPlayPressedEvent evt) {
		if (evt.index == -1) { //soundscape
			opsMgr.toggleSoundscapePlay(section);
		} else {
			opsMgr.toggleSoundPlay(section, evt.index);
		}
	}
}