package ase.main;

import ase.database.DatabaseException;
import ase.database.IDatabase;
import ase.database.legacy.LegacyDatabaseBridge;
import ase.operations.Log;
import ase.soundengine.ISoundEngine;
import ase.soundengine.SoundEngineException;
import ase.soundengine.SoundEngineManager;
import ase.soundengine.fmodex.FmodExEngine;
import ase.views.Gui;
import ase.views.navigation.events.QuitEvent;
import ase.views.navigation.events.ShutdownEvent;

import static ase.operations.OperationsManager.opsMgr;
import static ase.operations.Log.LogLevel.*;

import com.google.common.eventbus.Subscribe;

/**
 * Entry point into the Ambient Sound Engine.
 * Handles command-line arguments. Sets up sound engine(s)
 * and GUI, both of which subscribe to a centralized OperationsManager
 * object which manages application state and data persistence through
 * the database.
 * 
 * @author Kevin
 *
 */
public class Main {
	private ISoundEngine stage = null;
	private ISoundEngine preview = null;
	private IDatabase db;
	private Settings settings;
	
	private boolean active = true;
	
	private final Log logger = opsMgr.logger;
	
	public Main (IDatabase db) {
		opsMgr.setDatabase(db);
		this.db = db;
		
		try {
			stage = new FmodExEngine();
		} catch (SoundEngineException e){
			logger.logError("Error initializing primary SoundEngine", e);
			System.exit(-1);
		}
		
		try {
			preview = new FmodExEngine();
		} catch (SoundEngineException e) {
			logger.logError("System only supports one sound card.", e);
		}
		
		logger.log(DEBUG, "Initializing SoundEngineManager");
		SoundEngineManager seMgr = new SoundEngineManager(stage, preview);
		logger.log(DEBUG,  "SoundEngineManager initialized");

		logger.log(DEBUG, "Initializing GUI");
		Gui app = Gui.initGui();
		
		settings = new Settings();
		
		opsMgr.eventBus.register(this);
	}
	
	@Subscribe public void shutdownOnQuit(ShutdownEvent e) {
		active = false;
		
		try {
			stage.shutdown();
			preview.shutdown();
		} catch (Exception ex) {
			logger.logError("", ex);
		}
		logger.log(DEBUG, "Closing application");
	}
	
	public static void main(String[] args) throws InterruptedException {
		opsMgr.logger.log(DEBUG, "In main");
		
		try (IDatabase db = new LegacyDatabaseBridge()) { //automatically closed on exiting the block
			Main main = new Main(db);
			
			while (main.active) {
				Thread.sleep(1000); //make sure we don't spin lock...
			}; //infinite loop
		} catch (Exception ex) {
			opsMgr.logger.logError("Unable to initialize database", ex);
		}
	}
}
