package ase.views.components.consolepane;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

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
	
	private final JPanel effectsPanel = new JPanel(); //placeholder
	private final GridBagConstraints effectsPanelGbc = new GridBagConstraints();
	
	private final JLabel console2Label = new JLabel("Console 2");
	private final GridBagConstraints console2LabelGbc = new GridBagConstraints();
	private final Console console2;
	private final GridBagConstraints console2Gbc = new GridBagConstraints();
	
	//Console event busses. Allows the console to direct Ops Manager events
	private final EventBus console1EventBus = new EventBus();
	private final EventBus console2EventBus = new EventBus();
	
	public ConsolePane(GuiSettings settings) {
		this.settings = settings;
		
		layout.columnWeights = new double[]{1.0};
		layout.rowWeights = new double[]{0.1, 1.0, 1.0, 0.1, 1.0};
		layout.rowHeights = new int[]{0, 0, 0};
		setLayout(layout);
		
		setMinimumSize(new Dimension(500, 600));
		setPreferredSize(new Dimension(500, 720));
		
		setupGridBagConstraints();
		
		add(console1Label, console1LabelGbc);
		
		console1 = new Console(settings, console1EventBus);
		add(console1, console1Gbc);
		
		//Effects Panel Placeholder
		effectsPanel.setMinimumSize(new Dimension(400, 100));
		effectsPanel.setPreferredSize(new java.awt.Dimension(450, 150));
		add(effectsPanel, effectsPanelGbc);
		
		add(console2Label, console2LabelGbc);
		
		console2 = new Console(settings, console2EventBus);
		add(console2, console2Gbc);
		
		opsMgr.eventBus.register(this);
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
		
		//Effects Panel
		effectsPanelGbc.insets = consoleInsets;
		effectsPanelGbc.gridx = 0;
		effectsPanelGbc.gridy = 2;
		effectsPanelGbc.fill = GridBagConstraints.BOTH;
		
		//Console B Label
		console2LabelGbc.gridx = 0;
		console2LabelGbc.gridy = 3;
		console2LabelGbc.anchor = GridBagConstraints.WEST;
		console2LabelGbc.insets = consoleInsets;
		
		//Console B
		console2Gbc.insets = consoleInsets;
		console2Gbc.gridx = 0;
		console2Gbc.gridy = 4;
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
		console2Label.setFont(settings.largeFont);
		console2Label.setForeground(settings.lightText);
		
		//broadcast event to sub-components
		console1EventBus.post(e);
		console2EventBus.post(e);
	}
}
