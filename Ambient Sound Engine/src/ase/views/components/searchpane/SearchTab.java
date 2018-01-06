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
		
		list1List.addKeyListener(uiListeners.keywordListKeyAdapter);
		list1List.addListSelectionListener(uiListeners.keywordListSelectionListener);
		
		add(list1Scroller, list1ScrollerGbc);
		
		list2Label.setText("Results");
		add(list2Label, list2LabelGbc);
		
		/* TODO: POSSIBLY REMOVE PREVIOUS KEY LISTENERS */
		list2List.addKeyListener(uiListeners.matchListKeyAdapter);
		list2List.addListSelectionListener(uiListeners.matchListSelectionListener);
		
		add(list2Scroller, list2ScrollerGbc);
		
		toAButton.addActionListener(uiListeners.toConsole1Listener);
		add(toAButton, toAButtonGbc);
		
		toBButton.addActionListener(uiListeners.toConsole2Listener);
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
	 * Get the database ID of the selected keyword
	 * @return keyword ID, or -1 if nothing is selected
	 */
	public int getSelectedKeywordId() {
		int selectedIndex = list1List.getSelectedIndex();
		
		return selectedIndex == -1 ? selectedIndex : keywordListIds[selectedIndex];
	}
	
	/**
	 * Sets the keyword match list and keeps track of corresponding IDs in an array
	 * @param items
	 */
	public void setMatchListItems(SortedMap<Integer, String> items) {
		matchListIds = new int[items.size()];
		setList(items, matchListIds, list2List);
	}
	
	/**
	 * Get the database ID of the selected keyword
	 * @return keyword ID, or -1 if nothing is selected
	 */
	public int getSelectedMatchId() {
		int selectedIndex = list2List.getSelectedIndex();
		
		return selectedIndex == -1 ? selectedIndex : matchListIds[selectedIndex];
	}
	
	private void setList(SortedMap<Integer, String> items, int[] ids, JList<String> list) {
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
