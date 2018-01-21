package ase.views.components.searchpane;

import static ase.operations.Log.LogLevel.*;
import static ase.operations.OperationsManager.opsMgr;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ase.database.DataType;
import ase.database.DatabaseException;
import ase.database.IDatabase;
import ase.models.SoundModel;
import ase.models.SoundscapeModel;
import ase.operations.OperationsManager.Sections;

/**
 * Composite class that collects UI listener classes (KeyAdapter, MouseListener, etc)
 * @author Kevin C Gall
 *
 */
public class SearchTabUiComposite {
	private final SearchTab searchTab;
	private final SearchTabUiComposite that = this;
	
	public SearchTabUiComposite(SearchTab searchTab) {
		this.searchTab = searchTab;
	}
	
	public final Action soundscapeSearchAction = new AbstractAction() {
		private static final long serialVersionUID = 7220867334991957295L;

		@Override public void actionPerformed(ActionEvent e) { searchTab.searchFocus(SearchTab.SOUNDSCAPE_SEARCH); }
	};
	
	public final Action soundSearchAction = new AbstractAction() {
		private static final long serialVersionUID = -4485931158986922100L;

		@Override public void actionPerformed(ActionEvent e) { searchTab.searchFocus(SearchTab.SOUND_SEARCH); }
	};
	
	public final KeyAdapter searchFieldKeyAdapater = new KeyAdapter() {
		@Override
		public void keyReleased(KeyEvent evt) {
			IDatabase db = opsMgr.getDatabase();
			String searchTerm = searchTab.getSearchText(); 
			SortedMap<Integer, String> dbResults = new TreeMap<>();
			
			try {
				if (!searchTerm.equals("")) {
					if (searchTab.isSoundSelected()) {
						dbResults = db.getSoundKeywords(searchTerm);
					} else if (searchTab.isSoundscapeSelected()) {
						dbResults = db.getSoundscapeKeywords(searchTerm);
					}
				}	
			} catch (DatabaseException dEx) {
				opsMgr.logger.log(DEV, dEx.getMessage());
				opsMgr.logger.log(DEBUG, dEx.getStackTrace());
			}
			
			
			searchTab.setKeywordListItems(dbResults);
		}
	};
	
	//ActionMap actions
	public final Action keywordListEnterAction = new AbstractAction() {
		private static final long serialVersionUID = -7235231031918631254L;

		@Override public void actionPerformed(ActionEvent e) { searchTab.setListFocus(SearchTab.RESULT_LIST); }
	};
	
	public final Action keywordListDecrementAction = new AbstractAction() {
		private static final long serialVersionUID = -3884342418382603447L;

		@Override public void actionPerformed(ActionEvent e) { searchTab.decrementSelection(SearchTab.MATCH_LIST); }
	};
	
	public final Action keywordListIncrementAction = new AbstractAction() {
		private static final long serialVersionUID = 6888669953835441302L;

		@Override public void actionPerformed(ActionEvent e) { searchTab.incrementSelection(SearchTab.MATCH_LIST); }
	};
	
	public final ListSelectionListener keywordListSelectionListener = new ListSelectionListener() {
		@Override public void valueChanged(ListSelectionEvent e) {
			int keywordId = searchTab.getSelectedKeywordId();
			
			if (searchTab.isSoundSelected()) {
				opsMgr.logger.log(DEBUG, "Searching for sounds on keyword ID: " + keywordId);
				getNarrowedSearchResults(keywordId, DataType.SOUND);
			} else if (searchTab.isSoundscapeSelected()) {
				opsMgr.logger.log(DEBUG, "Searching for soundscapes on keyword ID: " + keywordId);
				getNarrowedSearchResults(keywordId, DataType.SOUNDSCAPE);
			}
		}
	};
	
	private void getNarrowedSearchResults(int keywordId, DataType type) {
		IDatabase db = opsMgr.getDatabase();
		SortedMap<Integer, String> dbResults = new TreeMap<>();
		
		try {
			switch(type) {
			case SOUND:
				dbResults = db.getSoundsByKeyword(keywordId);
				break;
			case SOUNDSCAPE:
				dbResults = db.getSoundscapesByKeyword(keywordId);
				break;
			default:
				opsMgr.logger.log(DEV, "Invalid type for searching on keyword. Not setting match list");
			}
		} catch (DatabaseException dEx) {
			opsMgr.logger.log(DEV, dEx.getMessage());
			opsMgr.logger.log(DEBUG, dEx.getStackTrace());
		}
		
		searchTab.setMatchListItems(dbResults);
	}
	
	public final Action resultListDecrementAction = new AbstractAction() {
		private static final long serialVersionUID = -8029957788826371710L;

		@Override public void actionPerformed(ActionEvent e) { searchTab.decrementSelection(SearchTab.RESULT_LIST); }
	};
	
	public final Action resultListIncrementAction = new AbstractAction() {
		private static final long serialVersionUID = 3013945482455075119L;

		@Override public void actionPerformed(ActionEvent e) { searchTab.incrementSelection(SearchTab.RESULT_LIST); }
	};
	
	public final ListSelectionListener resultListSelectionListener = new ListSelectionListener() {
		@Override public void valueChanged(ListSelectionEvent e) {
			opsMgr.logger.log(DEBUG, "Data element selected, id: " + searchTab.getSelectedMatchId());
		}
	};
	
	public final MouseAdapter resultListClickListener = new MouseAdapter() {
		@Override public void mouseClicked(MouseEvent evt) {
			if (evt.getClickCount() == 2) {
				ActionEvent actionEvt = new ActionEvent(evt.getSource(), evt.getID(), "Send to Console");
				
				if (evt.getButton() == 1) {
					that.toConsole1Action.actionPerformed(actionEvt);
				} else {
					that.toConsole2Action.actionPerformed(actionEvt);
				}
			}
		}
	};
	
	public final Action toConsole1Action = new AbstractAction() {
		private static final long serialVersionUID = 1174807677859420354L;

		@Override public void actionPerformed(ActionEvent evt) {
			int matchId = searchTab.getSelectedMatchId();
			
			if (searchTab.isSoundSelected()) {
				sendSoundToConsole(matchId, Sections.CONSOLE1);
			} else if (searchTab.isSoundscapeSelected()) {
				sendSoundscapeToConsole(matchId, Sections.CONSOLE1);
			}
		}
	};
	
	public final Action toConsole2Action = new AbstractAction() {
		private static final long serialVersionUID = -9108863900201144705L;

		@Override public void actionPerformed(ActionEvent evt) {
			int matchId = searchTab.getSelectedMatchId();
			
			if (searchTab.isSoundSelected()) {
				sendSoundToConsole(matchId, Sections.CONSOLE2);
			} else if (searchTab.isSoundscapeSelected()) {
				sendSoundscapeToConsole(matchId, Sections.CONSOLE2);
			}
		}
	};
	
	private void sendSoundToConsole(int soundId, Sections section) {
		if (soundId == -1) {
			JOptionPane.showMessageDialog(searchTab, "No Sound selected");
			return;
		}
		
		IDatabase db = opsMgr.getDatabase();
		
		try {
			SoundModel sound = db.getSoundById(soundId);
			opsMgr.addSound(section, sound);
		} catch (DatabaseException dEx) {
			opsMgr.logger.log(DEV, dEx.getMessage());
			opsMgr.logger.log(DEBUG, dEx.getStackTrace());
		}
	}
	
	private void sendSoundscapeToConsole(int ssid, Sections section) {
		if (ssid == -1) {
			JOptionPane.showMessageDialog(searchTab, "No Soundscape selected");
			return;
		}
		
		IDatabase db = opsMgr.getDatabase();
		
		try {
			SoundscapeModel soundscape = db.getSoundscapeById(ssid);
			opsMgr.addSoundscape(section, soundscape);
		} catch (DatabaseException dEx) {
			opsMgr.logger.log(DEV, dEx.getMessage());
			opsMgr.logger.log(DEBUG, dEx.getStackTrace());
		}
	}
	
	/**
	 * This function acts to override the InputMap on the search field so that
	 * window-wide key listeners can be ignored.
	 */
	public final Action doNothing = new AbstractAction() {
		private static final long serialVersionUID = -2887112221019506305L;

		@Override
		public void actionPerformed(ActionEvent e) {
			opsMgr.logger.log(DEBUG, "Do nothing invocation");
		}
	};
}