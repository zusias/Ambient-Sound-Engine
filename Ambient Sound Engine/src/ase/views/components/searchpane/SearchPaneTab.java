package ase.views.components.searchpane;

import static ase.operations.OperationsManager.opsMgr;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import com.google.common.eventbus.Subscribe;

import ase.views.GuiSettings;
import ase.views.events.SettingsEvent;

public abstract class SearchPaneTab extends JPanel {
	private static final long serialVersionUID = -1842708671291124035L;
	
	protected GuiSettings settings;
	protected final GridBagLayout layout = new GridBagLayout();
	
	//Components
	protected final JLabel searchLabel = new JLabel();
	protected final GridBagConstraints searchLabelGbc = new GridBagConstraints();
	protected final JTextField searchField = new JTextField();
	protected final GridBagConstraints searchFieldGbc = new GridBagConstraints();
	
	protected final ButtonGroup radioGroup = new ButtonGroup();
	protected final JRadioButton soundscapeRadioButton = new JRadioButton("Soundscapes");
	protected final GridBagConstraints soundscapeRadioButtonGbc = new GridBagConstraints();
	protected final JRadioButton soundRadioButton = new JRadioButton("Sound Files");
	protected final GridBagConstraints soundRadioButtonGbc = new GridBagConstraints();
	
	protected final JLabel list1Label = new JLabel();
	protected final GridBagConstraints list1LabelGbc = new GridBagConstraints();
	protected final JScrollPane list1Scroller = new JScrollPane();
	protected final GridBagConstraints list1ScrollerGbc = new GridBagConstraints();
	protected final JList<String> list1List = new JList<>();
	
	protected final JLabel list2Label = new JLabel();
	protected final GridBagConstraints list2LabelGbc = new GridBagConstraints();
	protected final JScrollPane list2Scroller = new JScrollPane();
	protected final GridBagConstraints list2ScrollerGbc = new GridBagConstraints();
	protected final JList<String> list2List = new JList<>();
	
	protected final JButton toAButton = new JButton("To Console 1");
	protected final GridBagConstraints toAButtonGbc = new GridBagConstraints();
	protected final JButton toBButton = new JButton("To Console 2");
	protected final GridBagConstraints toBButtonGbc = new GridBagConstraints();
	protected final JButton previewButton = new JButton("Preview");
	protected final GridBagConstraints previewButtonGbc = new GridBagConstraints();

	public SearchPaneTab(GuiSettings settings) {
		this.settings = settings;
		
		setLayout(layout);
		
		setupGridBagConstraints();
		
		searchLabel.setHorizontalAlignment(SwingConstants.LEFT);
		searchLabel.setHorizontalTextPosition(SwingConstants.LEFT);
		
		//Radio Buttons
		Dimension radioPreferredSize = new Dimension(100, 18);
		
		soundscapeRadioButton.setPreferredSize(radioPreferredSize);
		soundscapeRadioButton.setHorizontalAlignment(SwingConstants.TRAILING);
		soundscapeRadioButton.setHorizontalTextPosition(SwingConstants.LEADING);
		soundscapeRadioButton.setSelected(true);

		soundRadioButton.setPreferredSize(radioPreferredSize);
		soundRadioButton.setHorizontalAlignment(SwingConstants.TRAILING);
		soundRadioButton.setHorizontalTextPosition(SwingConstants.LEADING);
		
		radioGroup.add(soundscapeRadioButton);
		radioGroup.add(soundRadioButton);
		
		//List scrollers
		list1Label.setHorizontalTextPosition(SwingConstants.LEFT);
		list1Scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		list1Scroller.setPreferredSize(new Dimension(280, 375));
		list1Scroller.setViewportView(list1List);

		list1List.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list1List.setPreferredSize(null);

		list2Label.setHorizontalTextPosition(SwingConstants.LEFT);
		list2Scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		list2Scroller.setPreferredSize(new Dimension(280, 375));
		list2Scroller.setViewportView(list2List);
		
		list2List.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list2List.setPreferredSize(null);
		
		Dimension buttonPreferredSize = new Dimension(125, 20);
		toAButton.setPreferredSize(buttonPreferredSize);
		toBButton.setPreferredSize(buttonPreferredSize);
		previewButton.setPreferredSize(buttonPreferredSize);
		
		opsMgr.eventBus.register(this);
	}
	
	private void setupGridBagConstraints() {
		//Keyword Label GBC
		searchLabelGbc.gridwidth = 3;
		searchLabelGbc.gridx = 0;
		searchLabelGbc.gridy = 0;
		searchLabelGbc.anchor = GridBagConstraints.WEST;
		searchLabelGbc.insets = new Insets(7, 7, 5, 0);
		
		//Keyword Search GBC
		searchFieldGbc.fill = GridBagConstraints.HORIZONTAL;
		searchFieldGbc.gridx = 0;
		searchFieldGbc.gridy = 1;
		searchFieldGbc.gridwidth = 3;
		searchFieldGbc.anchor = GridBagConstraints.WEST;
		searchFieldGbc.insets = new Insets(0, 7, 5, 7);
		
		//Soundscape Radio GBC
		soundscapeRadioButtonGbc.fill = GridBagConstraints.HORIZONTAL;
		soundscapeRadioButtonGbc.gridx = 0;
		soundscapeRadioButtonGbc.gridy = 2;
		soundscapeRadioButtonGbc.anchor = GridBagConstraints.EAST;
		soundscapeRadioButtonGbc.insets = new Insets(0, 7, 7, 5);
		
		//Sound Radio GBC
		soundRadioButtonGbc.fill = GridBagConstraints.HORIZONTAL;
		soundRadioButtonGbc.gridx = 2;
		soundRadioButtonGbc.gridy = 2;
		soundRadioButtonGbc.anchor = GridBagConstraints.EAST;
		soundRadioButtonGbc.insets = new Insets(0, 0, 7, 7);
		
		//Match Label
		list1LabelGbc.gridwidth = 3;
		list1LabelGbc.gridx = 0;
		list1LabelGbc.gridy = 3;
		list1LabelGbc.anchor = GridBagConstraints.WEST;
		list1LabelGbc.insets = new Insets(0, 7, 5, 0);
		
		//Match scroll pane
		list1ScrollerGbc.weighty = 0.5;
		list1ScrollerGbc.weightx = 1.0;
		list1ScrollerGbc.fill = GridBagConstraints.BOTH;
		list1ScrollerGbc.gridx = 0;
		list1ScrollerGbc.gridy = 4;
		list1ScrollerGbc.gridwidth = 3;
		list1ScrollerGbc.anchor = GridBagConstraints.WEST;
		list1ScrollerGbc.insets = new Insets(0, 7, 7, 7);
		
		//Result Label
		list2LabelGbc.gridx = 0;
		list2LabelGbc.gridy = 5;
		list2LabelGbc.anchor = GridBagConstraints.WEST;
		list2LabelGbc.insets = new Insets(0, 7, 5, 5);
		
		//Result Scroll Pane
		list2ScrollerGbc.weightx = 1.0;
		list2ScrollerGbc.weighty = 0.5;
		list2ScrollerGbc.fill = GridBagConstraints.BOTH;
		list2ScrollerGbc.gridx = 0;
		list2ScrollerGbc.gridy = 6;
		list2ScrollerGbc.gridwidth = 3;
		list2ScrollerGbc.anchor = GridBagConstraints.WEST;
		list2ScrollerGbc.insets = new Insets(0, 7, 7, 7);
		
		//To A Button
		toAButtonGbc.fill = GridBagConstraints.HORIZONTAL;
		toAButtonGbc.weightx = 0.7;
		toAButtonGbc.gridx = 2;
		toAButtonGbc.gridy = 7;
		toAButtonGbc.insets = new Insets(0, 20, 5, 7);
		
		//To B Button
		toBButtonGbc.fill = GridBagConstraints.HORIZONTAL;
		toBButtonGbc.weightx = 0.7;
		toBButtonGbc.gridx = 2;
		toBButtonGbc.gridy = 8;
		toBButtonGbc.anchor = GridBagConstraints.EAST;
		toBButtonGbc.insets = new Insets(0, 20, 7, 7);
		
		//Preview Button
		previewButtonGbc.fill = GridBagConstraints.HORIZONTAL;
		previewButtonGbc.weightx = 0.7;
		previewButtonGbc.gridx = 0;
		previewButtonGbc.gridy = 7;
		previewButtonGbc.gridheight = 2;
		previewButtonGbc.anchor = GridBagConstraints.WEST;
		previewButtonGbc.insets = new Insets(0, 7, 7, 25);
	}
	
	@Subscribe public void applySettings(SettingsEvent e) {
		if (e.getNewSettings() != null) {
			this.settings = e.getNewSettings();
		}
		
		setBackground(settings.foregroundColor);
		
		searchLabel.setFont(settings.mediumFont);
		searchField.setFont(settings.smallFont);
		
		soundscapeRadioButton.setFont(settings.smallFont);
		soundscapeRadioButton.setBackground(settings.foregroundColor);
		soundRadioButton.setFont(settings.smallFont);
		soundRadioButton.setBackground(settings.foregroundColor);
		
		list1Label.setFont(settings.mediumFont);
		list1List.setFont(settings.smallFont);
		
		list2Label.setFont(settings.mediumFont);
		list2List.setFont(settings.smallFont);
		
		toAButton.setFont(settings.smallFont);
		toBButton.setFont(settings.smallFont);
		previewButton.setFont(settings.smallFont);
	}
}
