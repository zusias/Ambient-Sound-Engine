package ase.views.navigation;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import ase.views.GuiSettings;
import ase.views.events.SettingsEvent;
import ase.views.frames.SubFrame;
import ase.views.frames.AddSoundFrame;
import ase.views.frames.ManageFrame;
import ase.views.frames.MetadataFrame;
import ase.views.navigation.events.QuitEvent;

//ASE Operations
import static ase.operations.OperationsManager.opsMgr;
import static ase.operations.Log.LogLevel.*;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class FileMenu extends JMenu {
	private static final long serialVersionUID = -896113271890766440L;
	
	private final JMenuItem add;
	private final JMenuItem manage;
	private final JMenuItem changeMetadata;
	private final JMenuItem quit;
	private GuiSettings settings;
	
	public FileMenu (GuiSettings settings) {
		super("File");
		
		this.settings = settings;
		
		add = new JMenuItem("Add a Sound File");
		add.addActionListener((e) -> {
			opsMgr.logger.log(DEV, "Open Add Sound Menu");
			SubFrame.launchFrame(AddSoundFrame.class, settings);
		});
		
		this.add(add);
		
		manage = new JMenuItem("Manage...");
		manage.addActionListener((e) -> {
			opsMgr.logger.log(DEV, "Open Manage Menu");
			SubFrame.launchFrame(ManageFrame.class, settings);
		});
		
		this.add(manage);
		
		changeMetadata = new JMenuItem("Change Metadata");
		changeMetadata.addActionListener((e) -> {
			opsMgr.logger.log(DEV, "Open Metadata Menu");
			SubFrame.launchFrame(MetadataFrame.class, settings);
		});
		
		this.add(changeMetadata);
		
		quit = new JMenuItem("Quit");
		quit.addActionListener((e) -> {
			opsMgr.logger.log(DEV, "Quitting");
			opsMgr.eventBus.post(new QuitEvent(e));
		});
		
		this.add(quit);
		
		//register for event bus
		opsMgr.eventBus.register(this);
	}
	
	@Subscribe public void applySettings(SettingsEvent e) {
		if (e.getNewSettings() != null) {
			settings = e.getNewSettings();
		}
		
		this.setFont(settings.smallFont);
		
		add.setFont(settings.smallFont);
		manage.setFont(settings.smallFont);
		changeMetadata.setFont(settings.smallFont);
		quit.setFont(settings.smallFont);
	}
}
