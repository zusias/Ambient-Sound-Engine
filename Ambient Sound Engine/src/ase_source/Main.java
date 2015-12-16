/*
 * Main.java
 *
 * Created on March 6, 2006, 11:13 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package ase_source;

/**
 * 
 * @author David
 * @author CKidwell
 */
public class Main {
	static Database db;
	static FmodExEngine soundEngine;
	static OperationsManager chief;
	static Gui app;

	/**
	 * Just loads stuff and shows a new GUI object
	 */
	public static void main(String[] args) throws InterruptedException {
		EnvVariables.initVars();
		EnvVariables.logMessage("Loading");
		db = new Database();
		EnvVariables.logMessage("create db");
		db.connect();
		EnvVariables.logMessage("connected to db");
		db.loadPreparedStatements();
		EnvVariables.logMessage("statements loaded");
		
		// Doesn't do anything dynamic currently
		Arg.handleArg(args);

		soundEngine = new FmodExEngine(1);

		// Never tested it, but supposedly multiple sound cards should work with something like this
		// soundEngine = new FmodExEngine(1,2);

		OperationsManager.soundEngine = soundEngine;
		OperationsManager.db = db;

		// Gui.main(args);
		app = new Gui();
		OperationsManager.app = app;
		app.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		app.setVisible(true);
		while (true) {
			Thread.sleep(500);
			soundEngine.stage.system.update();
		}
	}

}
