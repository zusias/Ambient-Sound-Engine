package ase.views.components.searchpane;

import static ase.operations.OperationsManager.opsMgr;
import static ase.operations.Log.LogLevel.*;

import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.SortedMap;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.google.common.eventbus.Subscribe;

import ase.operations.OperationsManager.Sections;
import ase.views.GuiSettings;

public class SearchTab extends SearchPaneTab {
	private static final long serialVersionUID = 3516973549593974478L;
	
	public static final int MATCH_LIST = 0;
	public static final int RESULT_LIST = 1;
	
	public static final int SOUNDSCAPE_SEARCH = 2;
	public static final int SOUND_SEARCH = 3;
	
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
				resetLists();
			}
		});
		add(soundscapeRadioButton, soundscapeRadioButtonGbc);
		
		soundRadioButton.addItemListener((e) -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				opsMgr.logger.log(DEBUG, "Sound Radio selected");
				resetLists();
			}
		});
		add(soundRadioButton, soundRadioButtonGbc);
		
		//Match Scroll and list
		list1Label.setText("Matches");
		add(list1Label, list1LabelGbc);
		
		list1List.addListSelectionListener(uiListeners.keywordListSelectionListener);
		
		add(list1Scroller, list1ScrollerGbc);
		
		list2Label.setText("Results");
		add(list2Label, list2LabelGbc);
		
		list2List.addListSelectionListener(uiListeners.resultListSelectionListener);
		list2List.addMouseListener(uiListeners.resultListClickListener);
		
		add(list2Scroller, list2ScrollerGbc);
		
		toAButton.addActionListener(uiListeners.toConsole1Action);
		add(toAButton, toAButtonGbc);
		
		toBButton.addActionListener(uiListeners.toConsole2Action);
		add(toBButton, toBButtonGbc);
		
		previewButton.addActionListener((e) -> {
			opsMgr.logger.log(DEBUG, "Preview Button pressed");
		});
		add(previewButton, previewButtonGbc);
		
		setupInputMaps(uiListeners);
		
		opsMgr.eventBus.register(this);
	}
	
	private void setupInputMaps(SearchTabUiComposite uiListeners) {
		ActionMap keywordListActionMap = list1List.getActionMap();
		keywordListActionMap.put("setResultFocus", uiListeners.keywordListEnterAction);
		keywordListActionMap.put("incrementSelection", uiListeners.keywordListIncrementAction);
		keywordListActionMap.put("decrementSelection", uiListeners.keywordListDecrementAction);
		
		InputMap keywordListInputMap = list1List.getInputMap();
		keywordListInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "setResultFocus");
		keywordListInputMap.put(KeyStroke.getKeyStroke('d'), "incrementSelection");
		keywordListInputMap.put(KeyStroke.getKeyStroke('e'), "decrementSelection");
		
		ActionMap resultListActionMap = list2List.getActionMap();
		resultListActionMap.put("toConsole1", uiListeners.toConsole1Action);
		resultListActionMap.put("toConsole2", uiListeners.toConsole2Action);
		resultListActionMap.put("incrementSelection", uiListeners.resultListIncrementAction);
		resultListActionMap.put("decrementSelection", uiListeners.resultListDecrementAction);
		
		InputMap resultListInputMap = list2List.getInputMap();
		resultListInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "toConsole1");
		resultListInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.SHIFT_DOWN_MASK), "toConsole2");
		resultListInputMap.put(KeyStroke.getKeyStroke('d'), "incrementSelection");
		resultListInputMap.put(KeyStroke.getKeyStroke('e'), "decrementSelection");
		
		ActionMap searchFieldActionMap = searchField.getActionMap();
		searchFieldActionMap.put("soundscapeSearch", uiListeners.soundscapeSearchAction);
		searchFieldActionMap.put("soundSearch", uiListeners.soundSearchAction);
		searchFieldActionMap.put("doNothing", uiListeners.doNothing);
		
		InputMap searchFieldInputMap = searchField.getInputMap(WHEN_IN_FOCUSED_WINDOW);
		searchFieldInputMap.put(KeyStroke.getKeyStroke('s'), "soundscapeSearch");
		searchFieldInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S,KeyEvent.SHIFT_DOWN_MASK), "soundSearch");
		
		/* Below overwrite actions that occur on keystrokes while the text input field is focused.
		 * TODO: Refactor actions that occur when the window is focused to be set up by the GUI class.
		 * The GUI class can then dispatch events informing components of the actions they need to take
		 * in response. This would allow me to have an event the Gui listens for that enables and disables
		 * keystroke shortcuts. As it is, the below is dealing with keystroke actions that occur in the effects
		 * panel, which is a poor separation of concerns...
		 */
		InputMap searchFieldInputMapWhenFocused = searchField.getInputMap(WHEN_FOCUSED);
		searchFieldInputMapWhenFocused.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), "doNothing");
		searchFieldInputMapWhenFocused.put(KeyStroke.getKeyStroke(KeyEvent.VK_S,KeyEvent.SHIFT_DOWN_MASK), "doNothing");
		searchFieldInputMapWhenFocused.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), "doNothing");
		searchFieldInputMapWhenFocused.put(KeyStroke.getKeyStroke(KeyEvent.VK_B, 0), "doNothing");
		searchFieldInputMapWhenFocused.put(KeyStroke.getKeyStroke(KeyEvent.VK_T, 0), "doNothing");
	}
	
	public void searchFocus(int type) {
		opsMgr.logger.log(DEV, "Setting foucs to search bar");
		
		JRadioButton target = type == SOUND_SEARCH ? soundRadioButton : soundscapeRadioButton;
		
		target.setSelected(true);
		searchField.setText("");
		searchField.requestFocus();
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
	
	/**
	 * Requests focus for the passed list
	 * @param list See constants for possible values
	 */
	public void setListFocus(int list) {
		final JList<String> target = list == 0 ? list1List : list2List;
		
		target.requestFocus();
		target.setSelectedIndex(0);
	}
	
	public void incrementSelection(int list) {
		final JList<String> target = list == 0 ? list1List : list2List;
		
		int index = target.getSelectedIndex();
		if (index < target.getModel().getSize()) {
			target.setSelectedIndex(index + 1);
		}
	}
	
	public void decrementSelection(int list) {
		final JList<String> target = list == 0 ? list1List : list2List;
		
		int index = target.getSelectedIndex();
		if (index > 0) {
			target.setSelectedIndex(index - 1);
		}
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
	
	private void resetLists() {
		String[] emptyArray = new String[0]; 
		list1List.setListData(emptyArray);
		list2List.setListData(emptyArray);
	}
}
