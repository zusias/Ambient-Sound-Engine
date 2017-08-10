package ase.main;

import ase.bridge.SoundEngine;
import ase.bridge.SoundEngineManager;
import ase.fmodex_sound_engine.FmodExEngine;
import ase.bridge.SoundEngineException;
import ase.operations.Log;
import ase.views.Gui;
import ase.views.navigation.events.QuitEvent;

import static ase.operations.OperationsManager.opsMgr;
import static ase.operations.Log.LogLevel.PROD;
import static ase.operations.Log.LogLevel.DEV;
import static ase.operations.Log.LogLevel.DEBUG;

import java.util.Scanner;

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
	private SoundEngine stage = null;
	private SoundEngine preview = null;
	private final Log logger = opsMgr.logger;
	
	public Main () {
		try {
			stage = new FmodExEngine();
		} catch (SoundEngineException e){
			logger.log(PROD, "Error initializing primary SoundEngine");
			logger.log(DEV, e.getMessage());
			System.exit(-1);
		}
		
		try {
			preview = new FmodExEngine();
		} catch (SoundEngineException e) {
			logger.log(PROD, "System only supports one sound card.");
			logger.log(DEV, e.getMessage());
		}
		
		logger.log(DEBUG, "Initializing SoundEngineManager");
		SoundEngineManager seMgr = new SoundEngineManager(stage, preview);
		logger.log(DEBUG,  "SoundEngineManager initialized");

		logger.log(DEBUG, "Initializing GUI");
		Gui app = Gui.initGui();
		
		opsMgr.eventBus.register(this);
	}
	
	@Subscribe public void shutdownOnQuit(QuitEvent e) {
		try {
			stage.shutdown();
			preview.shutdown();
		} catch (NullPointerException npe) {
			opsMgr.logger.log(DEBUG, npe.getMessage());
		}
		opsMgr.logger.log(DEBUG, "Closing application");
	}
	
	public static void main(String[] args) throws InterruptedException {
		opsMgr.logger.log(DEBUG, "In main");
		
		//bootstrap
		Main main = new Main();
	}
}
