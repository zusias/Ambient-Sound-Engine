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
	
	public SearchTabUiComposite(SearchTab searchTab) {
		this.searchTab = searchTab;
	}
	
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
	
	public final KeyAdapter keywordListKeyAdapter = new KeyAdapter() {
		@Override public void keyReleased(KeyEvent e) {
			opsMgr.logger.log(DEBUG, "Key release on keyword list: " + e.getKeyCode());
		}
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
	
	public final KeyAdapter matchListKeyAdapter = new KeyAdapter() {
		@Override public void keyReleased(KeyEvent e) {
			opsMgr.logger.log(DEBUG, "Key release on match list: " + e.getKeyCode());
		}
	};
	
	public final ListSelectionListener matchListSelectionListener = new ListSelectionListener() {
		@Override public void valueChanged(ListSelectionEvent e) {
			opsMgr.logger.log(DEBUG, "Data element selected, id: " + searchTab.getSelectedMatchId());
		}
	};
	
	public final ActionListener toConsole1Listener = new ActionListener() {
		@Override public void actionPerformed(ActionEvent evt) {
			int matchId = searchTab.getSelectedMatchId();
			
			if (searchTab.isSoundSelected()) {
				sendSoundToConsole(matchId, Sections.CONSOLE1);
			} else if (searchTab.isSoundscapeSelected()) {
				sendSoundscapeToConsole(matchId, Sections.CONSOLE1);
			}
		}
	};
	
	public final ActionListener toConsole2Listener = new ActionListener() {
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
}