package ase.main;

import ase.database.DatabaseException;
import ase.database.IDatabase;

import static ase.operations.OperationsManager.opsMgr;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Model for current settings. Setter methods update the DB settings
 * immediately, so no changes ever need to be flushed
 * 
 * @author Kevin C. Gall
 *
 */
public class Settings {
	private final IDatabase db;
	private String saveTo;
	private String loadFrom;
	
	public Settings() {
		this.db = opsMgr.getDatabase();
		
		try {
			this.saveTo = db.getSetting("saveTo");
			this.loadFrom = db.getSetting("loadFrom");
		} catch (DatabaseException dbEx) {
			opsMgr.logger.logError("Cannot get settings from database", dbEx);
		}
		
		if (this.saveTo == null || this.loadFrom == null) {
			Path settingsLogPath = Paths.get("settings.log");
			
			if (Files.exists(settingsLogPath)) {
				getLegacySettings(settingsLogPath);
			} else {
				setupNewSettings();
			}
		}
	}
	
	public String getSaveTo() {
		return saveTo;
	}
	public void setSaveTo(String saveTo) {
		this.saveTo = saveTo;
		
		try {
			db.setSetting("saveTo", saveTo);
		} catch (DatabaseException dbEx) {
			opsMgr.logger.logError("Cannot save setting saveTo", dbEx);
		}
	}
	
	public String getLoadFrom() {
		return loadFrom;
	}
	public void setLoadFrom(String loadFrom) {
		this.loadFrom = loadFrom;
	}
	
	/**
	 * For the transition from 4.5 -> 5.0. This looks for a settings.log file
	 * in the current directory. Immediately loads into database and deletes
	 * the file
	 */
	private void getLegacySettings(Path settingsPath) {
		String text;
        try (BufferedReader in = Files.newBufferedReader(settingsPath)){
            text = in.readLine();
            this.loadFrom = text;
            text = in.readLine();
            this.saveTo = text;
 
            Files.delete(settingsPath);
            
        } catch(IOException ioe) {
        	System.out.println("Unable to read settings from settings.log");
            setupNewSettings();
            return;
        }
        
        try {
        	db.setSetting("saveTo", this.saveTo);
        	db.setSetting("loadFrom", this.loadFrom);
        } catch (DatabaseException dbEx) {
        	opsMgr.logger.logError("Unable to save settings to DB", dbEx);
        }
	}
	
	private void setupNewSettings() {
		try {
        	db.setSetting("saveTo", ".\\sounds\\");
        	db.setSetting("loadFrom", ".\\");
        } catch (DatabaseException dbEx) {
        	opsMgr.logger.logError("Unable to save settings to DB", dbEx);
        }
	}
}
