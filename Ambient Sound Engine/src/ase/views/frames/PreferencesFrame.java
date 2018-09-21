package ase.views.frames;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.google.common.eventbus.Subscribe;

import ase.views.GuiSettings;
import ase.views.events.SettingsEvent;
import ase_source_bak.EnvVariables;

import static ase.operations.OperationsManager.opsMgr;

public class PreferencesFrame extends SubFrame {
	private static final long serialVersionUID = -2937425522333033445L;
	
	private static final ImageIcon SAVE_PATH_ICON = new ImageIcon("soundfiles.gif");

	private final GridBagLayout layout = new GridBagLayout();
	
	private final JLabel saveToLabel = new JLabel("Save Sound Files To...");
	private final GridBagConstraints saveToGbc = new GridBagConstraints();
	private final JLabel saveValueLabel = new JLabel();
	private final GridBagConstraints saveValueGbc = new GridBagConstraints();
	private final JButton getPathButton = new JButton(SAVE_PATH_ICON);
	private final GridBagConstraints getPathButtonGbc = new GridBagConstraints();
	
	private final JPanel bottomPanel = new JPanel();
	private final GridBagConstraints bottomPanelGbc = new GridBagConstraints();
	private final JButton okButton = new JButton("Save");
	private final JButton cancelButton = new JButton("Cancel");
	
	private String currentSavedPath;
	
	public PreferencesFrame(GuiSettings settings) {
		super("ASE Preferences", settings);
		
		this.setSize(settings.preferencesFrameDefaultSize);
		layout.columnWeights = new double[] {0.0, 1.0, 0.0};
		layout.rowHeights = new int[] {20, 30};
		setLayout(layout);
		
		setupGridbagConstraints();
		
		add(saveToLabel, saveToGbc);
		
		currentSavedPath = opsMgr.getAppSettings().getSaveTo();
		
		//saveValueLabel.setMinimumSize(new Dimension(200, 20));
		saveValueLabel.setBorder(BorderFactory.createLoweredBevelBorder());
		saveValueLabel.setText(currentSavedPath);
		add(saveValueLabel, saveValueGbc);
		
		getPathButton.addActionListener((ActionEvt) -> {
			handleGetPath();
		});
		add(getPathButton, getPathButtonGbc);
		
		okButton.addActionListener((ActionEvent evt) -> {
			handleSave();
		});
		bottomPanel.add(okButton, BorderLayout.WEST);
		
		cancelButton.addActionListener((ActionEvent evt) -> {
			handleCancel();
		});
		bottomPanel.add(cancelButton, BorderLayout.EAST);
		
		add(bottomPanel, bottomPanelGbc);
	}
	
	private void setupGridbagConstraints() {
		saveToGbc.gridx = 0;
		saveToGbc.gridy = 0;
		getPathButtonGbc.anchor = GridBagConstraints.EAST;
		
		saveValueGbc.gridx = 1;
		saveValueGbc.gridy = 0;
		saveValueGbc.anchor = GridBagConstraints.CENTER;
		saveValueGbc.fill = GridBagConstraints.HORIZONTAL;
		
		getPathButtonGbc.gridx = 2;
		getPathButtonGbc.gridy = 0;
		getPathButtonGbc.fill = GridBagConstraints.NONE;
		getPathButtonGbc.anchor = GridBagConstraints.WEST;
		
		bottomPanelGbc.gridwidth = 3;
		bottomPanelGbc.fill = GridBagConstraints.HORIZONTAL;
		bottomPanelGbc.gridy = 1;
		bottomPanelGbc.insets = new Insets(10, 0, 0, 0);
	}
	
	private void handleGetPath() {
		JFileChooser fileChooser = new JFileChooser(saveValueLabel.getText());
		File selectedDirectory;

		String runDirectory = System.getProperty("user.dir");
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int status = fileChooser.showDialog(null, "Select Save Location");
		if (status == JFileChooser.APPROVE_OPTION) {
			selectedDirectory = fileChooser.getSelectedFile();
			String selectedDirectoryPath = selectedDirectory.getAbsolutePath();
			if(selectedDirectoryPath.startsWith(runDirectory)){
				selectedDirectoryPath = selectedDirectoryPath.replace(runDirectory, ".");
			}
			saveValueLabel.setText(selectedDirectoryPath+"\\");
		}
	}
	
	private void handleSave() {
		currentSavedPath = saveValueLabel.getText();
		opsMgr.getAppSettings().setSaveTo(currentSavedPath);
		this.setVisible(false);
	}
	
	private void handleCancel() {
		saveValueLabel.setText(currentSavedPath);
		this.setVisible(false);
	}
	
	@Subscribe public void applySettings(SettingsEvent e) {
		super.applySettings(e);
		
		saveValueLabel.setFont(settings.smallFont);
		getPathButton.setMaximumSize(settings.buttonSize);
	}

}
