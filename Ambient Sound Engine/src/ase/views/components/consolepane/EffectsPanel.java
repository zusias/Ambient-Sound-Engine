package ase.views.components.consolepane;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.google.common.eventbus.Subscribe;

import javax.swing.JButton;

import ase.operations.OperationsManager.Sections;
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
	private int delay = 200;
	private int fadeTime = 5000;
	
	private final GridBagLayout layout = new GridBagLayout();
	private final Insets buttonMargin = new Insets(0,0,0,0);
	
	private final int buttonSize = 30;
	private final Dimension buttonDim = new Dimension(buttonSize, buttonSize);
	
	private GuiSettings settings;
	
	public EffectsPanel(GuiSettings settings) {
		this.settings = settings;
		
		layout.columnWidths = new int[]{20, 150, 65, 40, 40, 65};
		layout.columnWeights = new double[]{0.1, 1.0, 0.0, 0.0, 0.0, 0.0};
		layout.rowHeights = new int[]{32, 32, 32};
		
		setLayout(layout);
		
		setupGridBagConstraints();
		
		configButton.setToolTipText("Transition Settings");
		setupButton(configButton, settingsIcon);
		add(configButton, configButtonGbc);
		
		transSoundInput.setEnabled(false);
		add(transSoundInput, transSoundInputGbc);
		
		add(fade1Label, fade1LabelGbc);
		setupButton(fade1Button, fadeIn);
		add(fade1Button, fade1ButtonGbc);
		
		add(transitionLabel, transitionLabelGbc);
		setupButton(transitionButton, nothingLoaded);
		add(transitionButton, transitionButtonGbc);
		
		add(fade2Label, fade2LabelGbc);
		setupButton(fade2Button, fadeIn);
		add(fade2Button, fade2ButtonGbc);
		
		add(crossfadeButton, crossfadeButtonGbc);
		setupButton(crossfadeButton, nothingLoaded);
		add(crossfadeLabel, crossfadeLabelGbc);
		
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
	
	private void setupButton(JButton button, ImageIcon buttonIcon) {
		button.setMargin(buttonMargin);
		
		button.setPreferredSize(buttonDim);
		button.setMinimumSize(buttonDim);
		button.setMaximumSize(buttonDim);
		
		button.setIcon(getScaledIcon(buttonIcon, buttonSize));
	}
	
	private ImageIcon getScaledIcon(ImageIcon icon, int size) {
		Image img = icon.getImage();
		Image scaledImg = img.getScaledInstance(size, size, Image.SCALE_DEFAULT);
		
		return new ImageIcon(scaledImg);
	}
	
	@Subscribe public void applySettings(SettingsEvent e) {
		if (e.getNewSettings() != null) {
			this.settings = e.getNewSettings();
		}
		
		setBackground(settings.foregroundColor);
	}
}
