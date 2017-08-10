package ase.views.components.search_pane;

import static ase.operations.Log.LogLevel.DEBUG;
import static ase.operations.OperationsManager.opsMgr;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import ase.views.GuiSettings;

public class IndexTab extends SearchPaneTab {
	private static final long serialVersionUID = 3484614368571097424L;
	
	public IndexTab(GuiSettings settings) {
		super(settings);
		
		layout.columnWeights = new double[]{1.0, 0.5, 1.0};
		
		searchLabel.setText("Search");
		add(searchLabel, searchLabelGbc);
		
		add(searchField, searchFieldGbc);
		
		soundscapeRadioButton.addItemListener((e) -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				opsMgr.logger.log(DEBUG, "Soundscape Radio selected");
				list1Label.setText("Soundscapes");
			}
		});
		add(soundscapeRadioButton, soundscapeRadioButtonGbc);
		
		soundRadioButton.addItemListener((e) -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				opsMgr.logger.log(DEBUG, "Sound Radio selected");
				list1Label.setText("Sound Files");
			}
		});
		add(soundRadioButton, soundRadioButtonGbc);
		
		list1Label.setText("Soundscapes");
		add(list1Label, list1LabelGbc);
		
		add(list1Scroller, list1ScrollerGbc);
		
		add(toAButton, toAButtonGbc);
		add(toBButton, toBButtonGbc);
		add(previewButton, previewButtonGbc);
	}
}
