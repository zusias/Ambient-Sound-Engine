package ase.views.components.searchpane;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.google.common.eventbus.Subscribe;

import ase.views.GuiSettings;
import ase.views.events.SettingsEvent;
import static ase.operations.OperationsManager.opsMgr;

public class SearchPane extends JTabbedPane {
	private static final long serialVersionUID = 4305458399682847468L;
	private GuiSettings settings;
	//Search Panel
	private final JPanel searchPanel;
	
	//History Panel
	private final JPanel historyPanel;
	
	//Index Panel
	private final JPanel indexPanel;
	
	public SearchPane (GuiSettings settings) {
		this.settings = settings;
		
		searchPanel = new SearchTab(settings);
		addTab("Search", searchPanel);
		
		
		historyPanel = new HistoryTab(settings);
		addTab("History", historyPanel);
		
		indexPanel = new IndexTab(settings);
		addTab("Index", indexPanel);
		
		setPreferredSize(new Dimension(299, 722));
		
		opsMgr.eventBus.register(this);
	}
	
	@Subscribe public void applyChanges(SettingsEvent e) {
		if (e.getNewSettings() != null) {
			this.settings = e.getNewSettings();
		}
		
		setFont(settings.smallFont);
	}
}
