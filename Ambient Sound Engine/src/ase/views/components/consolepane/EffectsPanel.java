package ase.views.components.consolepane;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.google.common.eventbus.Subscribe;

import javax.swing.JButton;

import ase.models.SoundscapeModel.PlayState;
import ase.operations.OperationsManager.Sections;
import ase.operations.events.ChangedSoundscapeEvent;
import ase.views.GuiSettings;
import ase.views.events.SettingsEvent;

import static ase.operations.OperationsManager.opsMgr;
import static ase.operations.OperationsManager.Sections.EFFECTS;;

/**
 * Class representing the effects panel. Allows for fade in and out of soundscapes
 * loaded into consoles, including crossfade and transition
 * @author Kevin C. Gall
 *
 */
public class EffectsPanel extends JPanel {
	//Enum for possible button states
	private enum ButtonState {
		FADEIN, FADEOUT, READY, INACTIVE;
	}
	
	private static final long serialVersionUID = 3043803745989042740L;
	
	private static final Sections section = EFFECTS;

	// Icons
	private static ImageIcon settingsIcon = new ImageIcon("transitionSettings.jpg");
	private static ImageIcon nothingLoaded = new ImageIcon("nothingLoaded.png");
	private static ImageIcon fadeIn = new ImageIcon("fadeIn.jpg");
	private static ImageIcon fadeOut = new ImageIcon("fadeOut.jpg");
	private static ImageIcon crossfade = new ImageIcon("crossfade.jpg");
	
	//Configuration elements
	private final JButton configButton = new JButton();
	private final GridBagConstraints configButtonGbc = new GridBagConstraints();
	private final JTextField transSoundInput = new JTextField();
	private final GridBagConstraints transSoundInputGbc = new GridBagConstraints();
	
	//Buttons / Labels
	
	//Fade 1
	private final JLabel fade1Label = new JLabel("Fade 1");
	private final GridBagConstraints fade1LabelGbc = new GridBagConstraints();
	private final JButton fade1Button = new JButton();
	private final GridBagConstraints fade1ButtonGbc = new GridBagConstraints();
	
	//Transition
	private final JLabel transitionLabel = new JLabel("Transition");
	private final GridBagConstraints transitionLabelGbc = new GridBagConstraints();
	private final JButton transitionButton = new JButton();
	private final GridBagConstraints transitionButtonGbc = new GridBagConstraints();
	
	//Crossfade
	private final JLabel crossfadeLabel = new JLabel("Crossfade");
	private final GridBagConstraints crossfadeLabelGbc = new GridBagConstraints();
	private final JButton crossfadeButton = new JButton();
	private final GridBagConstraints crossfadeButtonGbc = new GridBagConstraints();
	
	//Fade 2
	private final JLabel fade2Label = new JLabel("Fade 2");
	private final GridBagConstraints fade2LabelGbc = new GridBagConstraints();
	private final JButton fade2Button = new JButton();
	private final GridBagConstraints fade2ButtonGbc = new GridBagConstraints();
	
	//Button state information: Fade 1, Fade 2, Crossfade, Transition
	private final ButtonState[] buttonStates = new ButtonState[]
			{ButtonState.FADEIN, ButtonState.FADEIN, ButtonState.INACTIVE, ButtonState.INACTIVE};
	
	//Possibly write effects settings model class?
		//State variables, exposed through the settings menu
	private int delay = 0; //not used yet
	private int fadeTime = 5000;
	
	private final GridBagLayout layout = new GridBagLayout();
	private final Insets buttonMargin = new Insets(0,0,0,0);
	
	private final int buttonSize = 21;
	private final Dimension buttonDim = new Dimension(buttonSize, buttonSize);
	
	// Scaled Icons
	private ImageIcon scaledSettingsIcon;
	private ImageIcon scaledNothingLoaded;
	private ImageIcon scaledFadeIn;
	private ImageIcon scaledFadeOut;
	private ImageIcon scaledCrossfade;
	
	private GuiSettings settings;
	
	/**
	 * Set fade time
	 * @param timeInMs Time for fade in milliseconds
	 */
	public void setFadeTime(int timeInMs) {
		fadeTime = timeInMs;
	}
	
	/**
	 * Set delay for fades
	 * @param timeInMs
	 */
	public void setDelay(int timeInMs) {
		delay = timeInMs;
	}
	
	public EffectsPanel(GuiSettings settings) {
		this.settings = settings;
		
		layout.columnWidths = new int[]{20, 150, 65, 40, 40, 65};
		layout.columnWeights = new double[]{0.1, 1.0, 0.0, 0.0, 0.0, 0.0};
		layout.rowHeights = new int[]{23, 23, 23};
		
		//scaled buttons
		scaledSettingsIcon = getScaledIcon(settingsIcon, buttonSize);
		scaledNothingLoaded = getScaledIcon(nothingLoaded, buttonSize);
		scaledFadeIn = getScaledIcon(fadeIn, buttonSize);
		scaledFadeOut = getScaledIcon(fadeOut, buttonSize);
		scaledCrossfade = getScaledIcon(crossfade, buttonSize);
		
		setLayout(layout);
		
		setupGridBagConstraints();
		
		configButton.setToolTipText("Transition Settings");
		setupButton(configButton);
		configButton.setIcon(scaledSettingsIcon);
		add(configButton, configButtonGbc);
		
		transSoundInput.setEnabled(false);
		add(transSoundInput, transSoundInputGbc);
		
		add(fade1Label, fade1LabelGbc);
		setupButton(fade1Button);
		fade1Button.addActionListener((ActionEvent evt) -> {initiateFade(0, Sections.CONSOLE1);});
		add(fade1Button, fade1ButtonGbc);
		
		add(transitionLabel, transitionLabelGbc);
		setupButton(transitionButton);
		add(transitionButton, transitionButtonGbc);
		
		add(fade2Label, fade2LabelGbc);
		setupButton(fade2Button);
		fade2Button.addActionListener((ActionEvent evt) -> {initiateFade(1, Sections.CONSOLE2);});
		add(fade2Button, fade2ButtonGbc);
		
		add(crossfadeButton, crossfadeButtonGbc);
		setupButton(crossfadeButton);
		crossfadeButton.addActionListener((ActionEvent evt) -> {
			//check to ensure crossfade button is ready
			if (buttonStates[2] != ButtonState.READY) { return; }
			
			initiateFade(0, Sections.CONSOLE1);
			initiateFade(1, Sections.CONSOLE2);
		});
		add(crossfadeLabel, crossfadeLabelGbc);
		
		setButtonIcons();
		
		//initialize the effects panel
		opsMgr.newSoundscape(EFFECTS);
		
		opsMgr.eventBus.register(this);
	}
	
	private void setupGridBagConstraints() {
		//Config Button
		configButtonGbc.gridx = 0;
		configButtonGbc.gridy = 1;
		configButtonGbc.anchor = java.awt.GridBagConstraints.CENTER;
		configButtonGbc.fill = java.awt.GridBagConstraints.NONE;
		
		//Transition Sound Input
		transSoundInputGbc.gridx = 1;
		transSoundInputGbc.gridy = 1;
		transSoundInputGbc.anchor = java.awt.GridBagConstraints.WEST;
		transSoundInputGbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		
		//Fade 1 Label
		fade1LabelGbc.gridx = 2;
		fade1LabelGbc.gridy = 0;
		fade1LabelGbc.anchor = java.awt.GridBagConstraints.EAST;
		fade1LabelGbc.fill = java.awt.GridBagConstraints.NONE;
		
		//Fade 1 Button
		fade1ButtonGbc.gridx = 3;
		fade1ButtonGbc.gridy = 0;
		fade1ButtonGbc.anchor = java.awt.GridBagConstraints.CENTER;
		fade1ButtonGbc.fill = java.awt.GridBagConstraints.NONE;
		
		//Transition Label
		transitionLabelGbc.gridx = 2;
		transitionLabelGbc.gridy = 1;
		transitionLabelGbc.anchor = java.awt.GridBagConstraints.EAST;
		transitionLabelGbc.fill = java.awt.GridBagConstraints.NONE;
		
		//Transition Button
		transitionButtonGbc.gridx = 3;
		transitionButtonGbc.gridy = 1;
		transitionButtonGbc.anchor = java.awt.GridBagConstraints.CENTER;
		transitionButtonGbc.fill = java.awt.GridBagConstraints.NONE;
		
		//Fade 2 Label
		fade2LabelGbc.gridx = 2;
		fade2LabelGbc.gridy = 2;
		fade2LabelGbc.anchor = java.awt.GridBagConstraints.EAST;
		fade2LabelGbc.fill = java.awt.GridBagConstraints.NONE;
		
		//Fade 2 Button
		fade2ButtonGbc.gridx = 3;
		fade2ButtonGbc.gridy = 2;
		fade2ButtonGbc.anchor = java.awt.GridBagConstraints.CENTER;
		fade2ButtonGbc.fill = java.awt.GridBagConstraints.NONE;
		
		//Crossfade Button
		crossfadeButtonGbc.gridx = 4;
		crossfadeButtonGbc.gridy = 1;
		crossfadeButtonGbc.anchor = java.awt.GridBagConstraints.CENTER;
		crossfadeButtonGbc.fill = java.awt.GridBagConstraints.NONE;
		
		//Crossfade Label
		crossfadeLabelGbc.gridx = 5;
		crossfadeLabelGbc.gridy = 1;
		crossfadeLabelGbc.anchor = java.awt.GridBagConstraints.WEST;
		crossfadeLabelGbc.fill = java.awt.GridBagConstraints.NONE;
	}
	
	private void setupButton(JButton button) {
		button.setMargin(buttonMargin);
		
		button.setPreferredSize(buttonDim);
		button.setMinimumSize(buttonDim);
		button.setMaximumSize(buttonDim);
	}
	
	private void setButtonIcons() {
		//Fade 1 and 2
		setFadeButtonIcons(fade1Button, 0);
		setFadeButtonIcons(fade2Button, 1);
		
		//Crossfade
		if (buttonStates[2] == ButtonState.READY) {
			crossfadeButton.setIcon(scaledCrossfade);
		} else {
			crossfadeButton.setIcon(scaledNothingLoaded);
		}
		
		//Transition button. Not sure what icon it will be, so leave as inactive for now
		transitionButton.setIcon(scaledNothingLoaded);
	}
	
	private void setFadeButtonIcons(JButton button, int index) {
		switch(buttonStates[index]) {
			case FADEIN:
				button.setIcon(scaledFadeIn);
				break;
			case FADEOUT:
				button.setIcon(scaledFadeOut);
				break;
			case INACTIVE:
				button.setIcon(scaledNothingLoaded);
				break;
		}
	}
	
	private ImageIcon getScaledIcon(ImageIcon icon, int size) {
		Image img = icon.getImage();
		Image scaledImg = img.getScaledInstance(size, size, Image.SCALE_DEFAULT);
		
		return new ImageIcon(scaledImg);
	}
	
	/**
	 * 
	 * @param index Index of buttonStates that retrieves the intended button state
	 * @param section Section to be modified
	 */
	private void initiateFade(int index, Sections section) {
		PlayState fadeState = translatePlayState(index);
		
		if (fadeState == null) { return; } //indicates that button is inactive
		
		opsMgr.fadeSoundscape(section, fadeState, this.fadeTime);
	}
	
	private PlayState translatePlayState(int index) {
		switch(buttonStates[index]) {
			case FADEIN:
				return PlayState.FADEIN;
			case FADEOUT:
				return PlayState.FADEOUT;
			default:
				return null;
		}
	}
	
	@Subscribe public void applySettings(SettingsEvent e) {
		if (e.getNewSettings() != null) {
			this.settings = e.getNewSettings();
		}
		
		setBackground(settings.foregroundColor);
	}
	
	@Subscribe public void handleChangedSoundscape(ChangedSoundscapeEvent evt) {
		int index = -1;
		
		switch(evt.section) {
			case CONSOLE1:
				index = 0;
				break;
			case CONSOLE2:
				index = 1;
				break;
			default:
				return; //Only Console1 and Console2 cause changes to Effects Panel State
		}
		
		if (evt.soundscape.playState == PlayState.PLAYING
			|| evt.soundscape.playState == PlayState.FADEIN) {
			buttonStates[index] = ButtonState.FADEOUT;
		} else {
			buttonStates[index] = ButtonState.FADEIN;
		}
		
		// Handle change in crossfade state
		if (buttonStates[0] != buttonStates[1]
			&& buttonStates[0] != ButtonState.INACTIVE
			&& buttonStates[1] != ButtonState.INACTIVE) {
			
			buttonStates[2] = ButtonState.READY;
		} else {
			buttonStates[2] = ButtonState.INACTIVE;
		}
		
		setButtonIcons();
	}
}
