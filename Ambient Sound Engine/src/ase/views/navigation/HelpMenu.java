package ase.views.navigation;

import static ase.operations.OperationsManager.opsMgr;
import static ase.operations.Log.LogLevel.*;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import ase.views.GuiSettings;
import ase.views.frames.AboutFrame;
import ase.views.frames.HelpFrame;
import ase.views.frames.SubFrame;

public class HelpMenu extends JMenu {
	private static final long serialVersionUID = 7541442306340727236L;
	
	private final JMenuItem help;
	private final JMenuItem about;
	private final GuiSettings settings;
	
	public HelpMenu(GuiSettings settings) {
		super("Help");
		
		this.settings = settings;
		
		help = new JMenuItem("Help");
		help.addActionListener((e) -> {
			opsMgr.logger.log(DEV, "Opening Help Window");
			SubFrame.launchFrame(HelpFrame.class, settings);
		});
		
		this.add(help);
		
		this.about = new JMenuItem("About");
		about.addActionListener((e) -> {
			opsMgr.logger.log(DEV, "Opening About Window");
			SubFrame.launchFrame(AboutFrame.class, settings);
		});
		
		this.add(about);

		//register for event bus
		opsMgr.eventBus.register(this);
	}

	public void applySettings() {
		this.setFont(settings.smallFont);
		
		help.setFont(settings.smallFont);
		about.setFont(settings.smallFont);
	}
}
