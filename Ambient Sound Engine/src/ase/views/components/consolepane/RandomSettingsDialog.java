package ase.views.components.consolepane;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import ase.operations.OperationsManager.Sections;
import ase.operations.RandomPlaySettings;
import ase.operations.SoundModel;
import ase.views.components.consolepane.events.LaunchRandomSettingsEvent;
import static ase.operations.OperationsManager.opsMgr;

public class RandomSettingsDialog extends JDialog {
	private static final long serialVersionUID = 4221615548857666223L;
	
	private JLabel minPlayLabel = new JLabel("Minimum Repeats");
	private JLabel maxPlayLabel = new JLabel("Maximum Repeats");
	private JLabel minDelayLabel = new JLabel("Minimum Delay");
	private JLabel maxDelayLabel = new JLabel("Maximum Delay");
	private JButton saveButton = new JButton("Save Changes and Exit");
	private JButton discardButton = new JButton("Discard Changes");
	private JTextField minPlayField = new JTextField(4);
	private JTextField maxPlayField = new JTextField(4);
	private JTextField minDelayField = new JTextField(4);
	private JTextField maxDelayField = new JTextField(4);
	
	private final Sections section;
	private final int index;
	private final SoundModel sound;

	public RandomSettingsDialog(JFrame owner, LaunchRandomSettingsEvent launchEvt) {
		super(owner, "Random Play Settings for " + launchEvt.sound.name, true);
		
		this.section = launchEvt.section;
		this.index = launchEvt.index;
		this.sound = launchEvt.sound;
		
		GridLayout layout = new GridLayout(5, 2, 5, 5);
		setLayout(layout);
		
		add(minPlayLabel);
		add(minDelayLabel);
		
		RandomPlaySettings currentSettings = this.sound.randomSettings;
		
		minPlayField.setText(String.valueOf(currentSettings.minRepeats));
		minDelayField.setText(String.valueOf(currentSettings.minDelay));
		add(minPlayField);
		add(minDelayField);

		add(maxPlayLabel);
		add(maxDelayLabel);
		
		maxPlayField.setText(String.valueOf(currentSettings.maxRepeats));
		maxDelayField.setText(String.valueOf(currentSettings.maxDelay));
		add(maxPlayField);
		add(maxDelayField);
		
		saveButton.addActionListener(this::savePressed);
		discardButton.addActionListener(this::cancelPressed); 
		
		add(saveButton);
		add(discardButton);
		
		pack();
		setLocationRelativeTo(owner);
		setVisible(true);
	}
	
	private void cancelPressed(ActionEvent evt) {
		setVisible(false);
		dispose();
	}
	
	private void savePressed(ActionEvent evt) {
		RandomPlaySettings newSettings =
			new RandomPlaySettings(
					Integer.parseInt(minDelayField.getText()),
					Integer.parseInt(maxDelayField.getText()),
					Integer.parseInt(minPlayField.getText()),
					Integer.parseInt(maxPlayField.getText()));
		
		SoundModel newSound = sound.setRandomPlaySettings(newSettings);
		
		opsMgr.modifySound(section, index, newSound);
		
		setVisible(false);
		dispose();
	}
}
