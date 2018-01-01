package ase.views.components.searchpane;

import static ase.operations.OperationsManager.opsMgr;
import static ase.operations.Log.LogLevel.*;

import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.SortedMap;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.google.common.eventbus.Subscribe;

import ase.views.GuiSettings;

public class SearchTab extends SearchPaneTab {
	private static final long serialVersionUID = 3516973549593974478L;
	
	private int[] keywordListIds;
	private int[] matchListIds;
	
	public SearchTab(GuiSettings settings) {
		super(settings);
		
		SearchTabUiComposite uiListeners = new SearchTabUiComposite(this);
		
		layout.columnWeights = new double[]{1.0, 0.5, 1.0};
		
		searchLabel.setText("Keyword");
		add(searchLabel, searchLabelGbc);

		searchField.addKeyListener(uiListeners.searchFieldKeyAdapater);
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
			opsMgr.logger.log(DEBUG, "Button 1 pressed");
		});
		add(toAButton, toAButtonGbc);
		
		toBButton.addActionListener((e) -> {
			opsMgr.logger.log(DEBUG, "Button 2 pressed");
		});
		add(toBButton, toBButtonGbc);
		
		previewButton.addActionListener((e) -> {
			opsMgr.logger.log(DEBUG, "Preview Button pressed");
		});
		add(previewButton, previewButtonGbc);
		
		opsMgr.eventBus.register(this);
	}
	
	/**
	 * Sets the keyword match list and keeps track of corresponding IDs in an array
	 * @param items
	 */
	public void setKeywordListItems(SortedMap<Integer, String> items) {
		keywordListIds = new int[items.size()];
		setList(items, keywordListIds, list1List);
	}
	
	/**
	 * Sets the keyword match list and keeps track of corresponding IDs in an array
	 * @param items
	 */
	public void setMatchListItems(SortedMap<Integer, String> items) {
		matchListIds = new int[items.size()];
		setList(items, matchListIds, list2List);
	}
	
	private void setList(SortedMap<Integer, String> items, int[] ids, JList list) {
		String[] listItems = new String[items.size()];
		
		int index = 0;
		for (int listId : items.keySet()) {
			ids[index] = listId;
			listItems[index] = items.get(listId);
			
			index++;
		}
		
		list.setListData(listItems);
	}
}
