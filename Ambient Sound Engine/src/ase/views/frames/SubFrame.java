package ase.views.frames;

import static ase.operations.Log.LogLevel.*;
import static ase.operations.OperationsManager.opsMgr;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import ase.views.GuiSettings;

public abstract class SubFrame extends JFrame {
	private static final long serialVersionUID = 6347968712410658454L;
	private static final Map<Class<? extends SubFrame>, SubFrame> currentFrames = new HashMap<>();
	protected final GuiSettings settings;
	
	private final Class<? extends SubFrame> type;

	public SubFrame(String title, GuiSettings settings) {
		super(title);
		
		this.settings = settings;
		
		this.type = this.getClass();
		
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		this.setVisible(true);
		
		opsMgr.eventBus.register(this);
	}
	
	public static void launchFrame(Class<? extends SubFrame> type, GuiSettings settings) {
		opsMgr.logger.log(DEV, "Opening new subframe window");
		opsMgr.logger.log(DEBUG, "Subframe type: " + type.getSimpleName());
		
		SubFrame frame = currentFrames.get(type);
		
		if (frame == null) {
			try {
				Constructor<? extends SubFrame> frameConstructor = type.getDeclaredConstructor(GuiSettings.class);
				currentFrames.put(type, frameConstructor.newInstance(settings));
			} catch (ReflectiveOperationException  ex) {
				opsMgr.logger.log(PROD, "Critical failure in launching window");
				opsMgr.logger.log(DEV, "Improper implementation of SubFrame. Error " + ex.getMessage());
				opsMgr.logger.log(DEBUG, ex.getStackTrace());
			}
		} else {
			opsMgr.logger.log(DEBUG, "Not creating new window. Focusing existing one.");
			frame.setVisible(true);
			frame.toFront();
		}
	}
	
	public static void disposeAllSubFrames() {
		opsMgr.logger.log(DEV, "Dispose all subframes");
		for (SubFrame frame : currentFrames.values()) {
			frame.dispose();
		}
		
		currentFrames.clear();
	}
}
