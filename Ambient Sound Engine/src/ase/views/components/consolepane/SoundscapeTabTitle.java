package ase.views.components.consolepane;

import static ase.operations.OperationsManager.opsMgr;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.google.common.eventbus.Subscribe;

import ase.operations.OperationsManager.Sections;
import ase.views.GuiSettings;
import ase.views.events.SettingsEvent;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

public class SoundscapeTabTitle extends JPanel {
	private static final long serialVersionUID = 1858756556163648036L;
	private static final ImageIcon DELETE_ICON = new ImageIcon("delete.gif");

	private GuiSettings settings;
	private final Sections section;
	private int index;
	
	private final GridBagLayout layout = new GridBagLayout();
	
	private final JLabel title = new JLabel();
	private final GridBagConstraints titleGbc = new GridBagConstraints();
	
	private final JButton closeButton = new JButton(DELETE_ICON);
	private final GridBagConstraints closeButtonGbc = new GridBagConstraints();
	
	private final SoundscapeTab tab;
	
	
	public SoundscapeTabTitle(GuiSettings settings, SoundscapeTab tab, Sections section, int index) {
		this.settings = settings;
		this.section = section;
		this.index = index;
		this.tab = tab;
		
		
		
		//layout init
		layout.columnWeights = new double[] {1.0, 0.0};
		layout.columnWidths = new int[] {0, 16};
		layout.rowHeights = new int[] {16};
		
		title.setText(tab.getModel().name);
		add(title, titleGbc);
		
		//button click handler
		closeButton.addActionListener((ActionEvent evt) -> {
			int saveAnswer = JOptionPane.NO_OPTION;
			
			if (tab.getIsChanged()) {
				saveAnswer = tab.savePrompt();
			}
			
			if (saveAnswer != JOptionPane.CANCEL_OPTION) {
				opsMgr.removeSoundscape(section, getIndex());
			}
		});
		
		Dimension buttonSize = new Dimension(12, 12);
		
		closeButton.setMinimumSize(buttonSize);
		closeButton.setMaximumSize(buttonSize);
		closeButton.setPreferredSize(buttonSize);
		closeButton.setBorder(null);
		add(closeButton, closeButtonGbc);
		
		applySettings(new SettingsEvent());
		opsMgr.eventBus.register(this);
	}
	
	public void setTitle(String name) {
		title.setText(name);
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	private int getIndex() {
		return this.index;
	}
	
	public void destroy() {
		opsMgr.eventBus.unregister(this);
	}
	
	private void setupGridBagConstraints() {
		titleGbc.gridx = 0;
		titleGbc.gridy = 0;
		
		closeButtonGbc.gridx = 1;
		closeButtonGbc.gridy = 0;
	}
	
	@Subscribe public void applySettings(SettingsEvent evt) {
		if (evt.getNewSettings() != null) {
			settings = evt.getNewSettings();
		}
		
		
		setOpaque(false);
	}
}
