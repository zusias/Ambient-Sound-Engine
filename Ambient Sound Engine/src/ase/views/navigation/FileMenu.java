package ase.views.navigation;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import ase.views.GuiSettings;
import ase.views.navigation.events.QuitEvent;

//ASE Operations
import static ase.operations.OperationsManager.opsMgr;
import static ase.operations.Log.LogLevel.*;

import com.google.common.eventbus.EventBus;

public class FileMenu extends JMenu {
	private static final long serialVersionUID = -896113271890766440L;
	
	private final JMenuItem add;
	private final JMenuItem manage;
	private final JMenuItem changeMetadata;
	private final JMenuItem quit; 
	
	public FileMenu (GuiSettings settings) {
		super("File");
		
		this.setFont(settings.smallFont);
		
		add = new JMenuItem("Add a Sound File");
		add.setFont(settings.smallFont);
		add.addActionListener((e) -> System.out.println("Add"));
		
		this.add(add);
		
		manage = new JMenuItem("Manage...");
		manage.setFont(settings.smallFont);
		manage.addActionListener((e) -> System.out.println("Manage"));
		
		this.add(manage);
		
		changeMetadata = new JMenuItem("Change Metadata");
		changeMetadata.setFont(settings.smallFont);
		changeMetadata.addActionListener((e) -> System.out.println("ChangeMd"));
		
		this.add(changeMetadata);
		
		quit = new JMenuItem("Quit");
		quit.setFont(settings.smallFont);
		quit.addActionListener((e) -> {
			opsMgr.logger.log(DEV, "Quitting");
			opsMgr.eventBus.post(new QuitEvent(e));
		});
		
		this.add(quit);
		
		//register for event bus
		opsMgr.eventBus.register(this);
	}
}
