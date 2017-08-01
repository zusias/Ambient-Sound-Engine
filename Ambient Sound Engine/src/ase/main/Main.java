package ase.main;

import ase.bridge.SoundEngine;
import ase.bridge.SoundEngineManager;
import ase.fmodex_sound_engine.FmodExEngine;
import ase.bridge.SoundEngineException;
import ase.operations.Log;
import ase.views.Gui;

import static ase.operations.OperationsManager.opsMgr;
import static ase.operations.Log.LogLevel.PROD;
import static ase.operations.Log.LogLevel.DEV;
import static ase.operations.Log.LogLevel.DEBUG;

import java.util.Scanner;

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
	public static void main(String[] args) throws InterruptedException {
		SoundEngine stage = null;
		SoundEngine preview = null;
		Log logger = opsMgr.logger;
		logger.log(DEBUG, "In main");
		
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
		Gui app = new Gui();
		
		//TODO: Keep an eye out here. This might block UI interaction. Not sure
		//exactly how Swing works in that regard...
		while (app.isOpen()) {
			Thread.sleep(500);
		}
		
		try {
			stage.shutdown();
			preview.shutdown();
		} catch (NullPointerException e) {
			logger.log(DEBUG, e.getMessage());
		}
		logger.log(DEBUG, "Closing application");
	}
}
