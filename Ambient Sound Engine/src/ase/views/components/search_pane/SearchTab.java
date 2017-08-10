package ase.views.components.search_pane;

import static ase.operations.OperationsManager.opsMgr;
import static ase.operations.Log.LogLevel.*;

import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.google.common.eventbus.Subscribe;

import ase.views.GuiSettings;

public class SearchTab extends SearchPaneTab {
	private static final long serialVersionUID = 3516973549593974478L;
	
	
	public SearchTab(GuiSettings settings) {
		super(settings);
		
		layout.columnWeights = new double[]{1.0, 0.5, 1.0};
		
		searchLabel.setText("Keyword");
		add(searchLabel, searchLabelGbc);

		searchField.addKeyListener(new KeyAdapter() {
			@Override public void keyReleased(KeyEvent e) {
				opsMgr.logger.log(DEBUG, "Search key released: " + e.getKeyChar());
			}
		});
		add(searchField, searchFieldGbc);
		
		soundscapeRadioButton.addItemListener((e) -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				opsMgr.logger.log(DEBUG, "Soundscape Radio selected");
			}
		});
		add(soundscapeRadioButton, soundscapeRadioButtonGbc);
		
		soundRadioButton.addItemListener((e) -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				opsMgr.logger.log(DEBUG, "Sound Radio selected");
			}
		});
		add(soundRadioButton, soundRadioButtonGbc);
		
		//Match Scroll and list
		list1Label.setText("Matches");
		add(list1Label, list1LabelGbc);
		
		/* TODO: POSSIBLY REMOVE PREVIOUS KEY LISTENERS */
		list1List.addKeyListener(new KeyAdapter() {
			@Override public void keyReleased(KeyEvent e) {
				opsMgr.logger.log(DEBUG, "Key release on match list: " + e.getKeyCode());
			}
		});
		list1List.addListSelectionListener(new ListSelectionListener() {
			@Override public void valueChanged(ListSelectionEvent e) {
				opsMgr.logger.log(DEBUG, "Match list item selected. " + e.getFirstIndex());
			}
		});
		list1List.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				opsMgr.logger.log(DEBUG, "Mouse click on match list");
			}
		});
		
		add(list1Scroller, list1ScrollerGbc);
		
		list2Label.setText("Results");
		add(list2Label, list2LabelGbc);
		
		/* TODO: POSSIBLY REMOVE PREVIOUS KEY LISTENERS */
		list2List.addKeyListener(new KeyAdapter() {
			@Override public void keyReleased(KeyEvent e) {
				opsMgr.logger.log(DEBUG, "Key release on result list: " + e.getKeyCode());
			}
		});
		list2List.addListSelectionListener(new ListSelectionListener() {
			@Override public void valueChanged(ListSelectionEvent e) {
				opsMgr.logger.log(DEBUG, "Result list item selected. " + e.getFirstIndex());
			}
		});
		list2List.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				opsMgr.logger.log(DEBUG, "Mouse click on result list");
			}
		});
		
		add(list2Scroller, list2ScrollerGbc);
		
		toAButton.addActionListener((e) -> {
			opsMgr.logger.log(DEBUG, "Button A pressed");
		});
		add(toAButton, toAButtonGbc);
		
		toBButton.addActionListener((e) -> {
			opsMgr.logger.log(DEBUG, "Button B pressed");
		});
		add(toBButton, toBButtonGbc);
		
		previewButton.addActionListener((e) -> {
			opsMgr.logger.log(DEBUG, "Preview Button pressed");
		});
		add(previewButton, previewButtonGbc);
		
		opsMgr.eventBus.register(this);
	}
}
