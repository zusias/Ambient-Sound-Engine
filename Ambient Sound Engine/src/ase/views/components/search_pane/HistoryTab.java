package ase.views.components.search_pane;

import static ase.operations.OperationsManager.opsMgr;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import com.google.common.eventbus.Subscribe;

import ase.views.GuiSettings;
import ase.views.events.SettingsEvent;

public class HistoryTab extends SearchPaneTab {
	private static final long serialVersionUID = 6556068903502231355L;
	
	public HistoryTab(GuiSettings settings) {
		super(settings);
		
		this.settings = settings;
		
		layout.rowHeights = new int[]{39,0,0,0,30,10};
		layout.columnWeights = new double[]{1.0,0,1.0};
		layout.columnWidths = new int[]{0,0};
		this.setLayout(layout);
		
		setupGridBagConstraints();
		
		add(soundscapeRadioButton, soundscapeRadioButtonGbc);
		
		add(soundRadioButton, soundRadioButtonGbc);
		
		
		list1Label.setText("History");
		add(list1Label, list1LabelGbc);
		
		add(list1Scroller, list1ScrollerGbc);
		
		previewButton.setPreferredSize(new Dimension(125, 30));
		add(previewButton, previewButtonGbc);
	}
	
	private void setupGridBagConstraints() {
		//Soundscape Radio
		soundscapeRadioButtonGbc.gridy = 0;
		
		//Sound Radio
		soundRadioButtonGbc.gridy = 0;
		
		//History Label
		list1LabelGbc.gridy = 1;
		
		//History Scroller
		list1ScrollerGbc.weighty = 1.0;
		list1ScrollerGbc.gridy = 2;
		
		//Preview Button
		previewButtonGbc.insets = new Insets(0, 7, 0, 7);
		previewButtonGbc.weightx = 1.0;
		previewButtonGbc.gridy = 3;
		previewButtonGbc.gridx = 2;
		previewButtonGbc.anchor = GridBagConstraints.WEST;
	}
	
}
