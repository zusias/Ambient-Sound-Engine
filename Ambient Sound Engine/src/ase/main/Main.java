package ase.main;

import ase.bridge.SoundEngine;
import ase.bridge.SoundEngineManager;
import ase.bridge.SoundEngineException;
import ase.sound_engine.FmodExEngine;
import ase.operations.Log;
import static ase.operations.OperationsManager.opsMgr;
import static ase.operations.Log.LogLevel.PROD;
import static ase.operations.Log.LogLevel.DEV;
import static ase.operations.Log.LogLevel.DEBUG;

import java.util.Scanner;

import com.sun.media.jfxmedia.logging.Logger;

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
	public static void main(String[] args) {
		SoundEngine stage = null, preview = null;
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
		//Test code!
		Scanner scan = new Scanner(System.in);
		String s = scan.next();
		while (!s.equals("q")) {
			logger.log(DEBUG, "getting user input");
			s = scan.next();
			logger.log(DEBUG,  "got " + s);
		}
	}

}
