package ase.views.components.searchpane;

import static ase.operations.Log.LogLevel.*;
import static ase.operations.OperationsManager.opsMgr;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.SortedMap;
import java.util.TreeMap;

import ase.database.DatabaseException;
import ase.database.IDatabase;

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
}
