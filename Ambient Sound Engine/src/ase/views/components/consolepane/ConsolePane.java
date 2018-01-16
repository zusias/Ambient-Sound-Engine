package ase.views.components.consolepane;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import ase.operations.OperationsManager.Sections;
import ase.operations.events.ChangedSoundscapeSetEvent;
import ase.views.GuiSettings;
import ase.views.events.SettingsEvent;
import static ase.operations.OperationsManager.opsMgr;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class ConsolePane extends JPanel {
	private static final long serialVersionUID = 62796215627796661L;

	private GuiSettings settings;
	private final GridBagLayout layout = new GridBagLayout();

	private final JLabel console1Label = new JLabel("Console 1");
	private final GridBagConstraints console1LabelGbc = new GridBagConstraints();
	private final Console console1;
	private final GridBagConstraints console1Gbc = new GridBagConstraints();

	private final JLabel effectsLabel = new JLabel("Effects");
	private final GridBagConstraints effectsLabelGbc = new GridBagConstraints();
	private final EffectsPanel effectsPanel;
	private final GridBagConstraints effectsPanelGbc = new GridBagConstraints();
	
	private final JLabel console2Label = new JLabel("Console 2");
	private final GridBagConstraints console2LabelGbc = new GridBagConstraints();
	private final Console console2;
	private final GridBagConstraints console2Gbc = new GridBagConstraints();
	
	public ConsolePane(GuiSettings settings) {
		this.settings = settings;
		
		layout.columnWeights = new double[]{1.0};
		layout.rowWeights = new double[]{0.1, 1.0, 0.1, 0.0, 0.1, 1.0};
		layout.rowHeights = new int[]{0, 0, 0};
		setLayout(layout);
		
		setMinimumSize(new Dimension(500, 600));
		setPreferredSize(new Dimension(500, 720));
		
		setupGridBagConstraints();
		
		add(console1Label, console1LabelGbc);
		
		console1 = new Console(settings, Sections.CONSOLE1, opsMgr.getConsole1());
		add(console1, console1Gbc);
		
		add(effectsLabel, effectsLabelGbc);
		
		effectsPanel = new EffectsPanel(settings);
		
		Dimension effectsPanelSize = new Dimension(400, 70);
		effectsPanel.setMinimumSize(effectsPanelSize);
		effectsPanel.setPreferredSize(effectsPanelSize);
		add(effectsPanel, effectsPanelGbc);
		
		add(console2Label, console2LabelGbc);
		
		console2 = new Console(settings, Sections.CONSOLE2, opsMgr.getConsole2());
		add(console2, console2Gbc);
		
		opsMgr.eventBus.register(this);
	}
	
	public boolean hasUnsavedChanges() {
		return console1.getChanged() || console2.getChanged();
	}
	
	private void setupGridBagConstraints() {
		Insets consoleInsets = new Insets(0, 15, 0, 15);
		
		//Console A Label
		console1LabelGbc.gridx = 0;
		console1LabelGbc.gridy = 0;
		console1LabelGbc.anchor = GridBagConstraints.WEST;
		console1LabelGbc.insets = consoleInsets;
		
		//Console A
		console1Gbc.insets = consoleInsets;
		console1Gbc.gridx = 0;
		console1Gbc.gridy = 1;
		console1Gbc.fill = GridBagConstraints.BOTH;
		console1Gbc.ipady = 50;
		
		//Effects Label
		effectsLabelGbc.gridx = 0;
		effectsLabelGbc.gridy = 2;
		effectsLabelGbc.anchor = GridBagConstraints.WEST;
		effectsLabelGbc.insets = consoleInsets;
		
		//Effects Panel
		effectsPanelGbc.insets = consoleInsets;
		effectsPanelGbc.gridx = 0;
		effectsPanelGbc.gridy = 3;
		effectsPanelGbc.fill = GridBagConstraints.BOTH;
		
		//Console B Label
		console2LabelGbc.gridx = 0;
		console2LabelGbc.gridy = 4;
		console2LabelGbc.anchor = GridBagConstraints.WEST;
		console2LabelGbc.insets = consoleInsets;
		
		//Console B
		console2Gbc.insets = consoleInsets;
		console2Gbc.gridx = 0;
		console2Gbc.gridy = 5;
		console2Gbc.fill = GridBagConstraints.BOTH;
		console2Gbc.ipady = 50;
		console2Gbc.anchor = GridBagConstraints.SOUTH;
	}
	
	@Subscribe public void applySettings(SettingsEvent e) {
		if (e.getNewSettings() != null) {
			settings = e.getNewSettings();
		}
		
		setBackground(settings.backgroundColor);
		
		console1Label.setFont(settings.largeFont);
		console1Label.setForeground(settings.lightText);
		effectsLabel.setFont(settings.largeFont);
		effectsLabel.setForeground(settings.lightText);
		console2Label.setFont(settings.largeFont);
		console2Label.setForeground(settings.lightText);
	}
}
