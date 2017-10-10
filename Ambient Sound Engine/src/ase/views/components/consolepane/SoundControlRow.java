package ase.views.components.consolepane;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import ase.operations.SoundModel;
import ase.views.GuiSettings;
import ase.views.events.SettingsEvent;
import static ase.operations.SoundModel.PlayType.*;

public class SoundControlRow extends ConsoleControlRow {
	private static final long serialVersionUID = 6293656847414464853L;
	
	private static final ImageIcon LOOP_PLAY_ICON = new ImageIcon("LoopPlay.gif");
	private static final ImageIcon PLAY_ONCE_ICON = new ImageIcon("PlayOnce.gif");
	private static final ImageIcon RANDOM_PLAY_ICON = new ImageIcon("RandomPlay.gif");

	private final JButton playModeButton = new JButton(LOOP_PLAY_ICON);
	private final GridBagConstraints playModeButtonGbc = new GridBagConstraints();
	
	private SoundModel sound;
	
	public SoundControlRow(GuiSettings settings, SoundModel sound, int rowIndex, EventBus tabEventBus) {
		super(settings, rowIndex, tabEventBus);
		
		setupMoreGridBagConstraints();
		
		playModeButton.setToolTipText("Set Play Mode");
		add(playModeButton, playModeButtonGbc);
		
		updateModel(sound);
	}
	
	public SoundModel getModel() {
		return sound;
	}
	
	public void updateModel(SoundModel sound) {
		this.sound = sound;
		
		volumeBar.setValue(getVolume());
		
		title.setText(sound.name);
		
		if (sound.isPlaying) {
			playButton.setIcon(SPEAKER_ON_ICON);
		} else {
			playButton.setIcon(SPEAKER_OFF_ICON);
		}
		
		switch (sound.currentPlayType) {
			case LOOP:
				playModeButton.setIcon(LOOP_PLAY_ICON);
				break;
			case SINGLE:
				playModeButton.setIcon(PLAY_ONCE_ICON);
				break;
			case RANDOM:
				playModeButton.setIcon(RANDOM_PLAY_ICON);
				break;
		}
	}
	
	@Override
	public int getVolume() {
		return (int) (sound.volume * 1000);
	}
	
	private void setupMoreGridBagConstraints() {
		//Play Mode Button
		playModeButtonGbc.gridx = 3;
		playModeButtonGbc.gridy = 0;
		playModeButtonGbc.insets = new Insets(0, 0, 0, 5);
	}
	
	@Override
	@Subscribe public void applySettings(SettingsEvent e) {
		super.applySettings(e);
		if (e.getNewSettings() != null) {
			settings = e.getNewSettings();
		}

		playModeButton.setMaximumSize(settings.buttonSize);
		playModeButton.setMinimumSize(settings.buttonSize);
		playModeButton.setPreferredSize(settings.buttonSize);
	}
}
