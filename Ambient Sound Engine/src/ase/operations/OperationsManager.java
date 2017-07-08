package ase.operations;


//Java
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Files;

//Utilities
import java.util.LinkedList;

import ase.operations.SoundscapeModel.PlayState;
import static ase.operations.Log.LogLevel.DEBUG;
import static ase.operations.Log.LogLevel.DEV;
import static ase.operations.Log.LogLevel.PROD;

/**
 * Central business logic controller.
 * <ul>Fulfills patterns:
 * 	<li>Singleton</li>
 * 	<li>Subject (of subject / observer)</li>
 * </ul>
 * 
 * Holds the data models for the full application and notifies subscribers
 * of any changes in the model
 * <br><br>
 * 
 * NOTE: The Operations Manager is guaranteed to always add sounds to the end of the data structures.
 * You may remove one from the middle, but never add to the middle or reorder existing sounds<br><br>
 * 
 * TODO: General to do list for project
 * <ul>
 * 	<li>Write Fmodex implementation of SoundEngine</li>
 * </ul>
 * 
 * @author Kevin C. Gall
 *
 */
public class OperationsManager {
	//Utility enum to identify consoles
	public static enum Sections {
		CONSOLE1, CONSOLE2, EFFECTS, PREVIEW
	}
	
	//singleton
	public static OperationsManager opsMgr = new OperationsManager();
	
	//Main app objects
	//public final Database db;
	//public final SoundEngine soundEngine;
	//public final Gui gui;
	
	//utility app objects
	public final Log logger;
	
	//Data models
	private SoundscapeSetModel console1;
	private SoundscapeSetModel console2;
	private SoundscapeModel effects;
	
	//This data model is not rendered in the UI except by a preview button
	private SoundscapeModel preview;
	
	//Subscriber sets
	//Chose linked list because iteration is cheap, growing is cheap, insertion will not be needed, and deletion is unlikely
	private final LinkedList<ISubscriber<SoundscapeSetModel, SoundscapeModel>> console1Subscribers = new LinkedList<>();
	private final LinkedList<ISubscriber<SoundscapeSetModel, SoundscapeModel>> console2Subscribers = new LinkedList<>();
	private final LinkedList<ISubscriber<SoundscapeModel, SoundModel>> console1ActiveSsSubscribers = new LinkedList<>();
	private final LinkedList<ISubscriber<SoundscapeModel, SoundModel>> console2ActiveSsSubscribers = new LinkedList<>();
	private final LinkedList<ISubscriber<SoundscapeModel, SoundModel>> effectsSubscribers = new LinkedList<>();
	private final LinkedList<ISubscriber<SoundscapeModel, SoundModel>> previewSubscribers = new LinkedList<>();
	
	//utility properties
	private static int runtimeId;
	private static SoundscapeModel defaultSs;
	
	//utility static methods
	public static long getFileSize(Path filePath) throws IOException {
		String sizeString = Files.getAttribute(filePath, "size").toString();
		
		return Long.parseLong(sizeString, 10);
	}
	
	private OperationsManager() {
		//init static variables
		runtimeId = 1;
		defaultSs = new SoundscapeModel(-1, runtimeId++, 1.0, null,
						"New Soundscape", SoundscapeModel.PlayState.STOPPED, 0);
		this.logger = new Log(DEBUG); //hard code Debug level for now
		
		logger.log(DEV, "Logger active");
		
		/* initialize soundscapes as empty defaults
		 * 
		 * TODO: Implement a mechanism that can load in a previous session's
		 * workspace. Probably by auto-saving the workspace on window close and
		 * storing it in the database under some kind of session state table
		 */
		//Making use of immutability of object to efficiently clone
		SoundscapeModel defaultEffects = defaultSs.rename("Effects Panel").copy(runtimeId++);
		SoundscapeModel defaultPreview = defaultSs.rename("Preview Soundscape").copy(runtimeId++);
		
		this.console1 = new SoundscapeSetModel(new SoundscapeModel[] {defaultSs});
		this.console2 = new SoundscapeSetModel(new SoundscapeModel[] {defaultSs.copy(runtimeId++)});

		this.effects = defaultEffects;
		this.preview = defaultPreview;
		
		logger.log(DEBUG, "Ending Ops Constructor");
	}
	
	/**
	 * Notify subscribers of a particular console of a change in a SoundscapeModel(s)
	 * @param console
	 * @param subscribers
	 */
	private void notify(SoundscapeSetModel console, int removedIndex, LinkedList<ISubscriber<SoundscapeSetModel, SoundscapeModel>> subscribers){
		for (ISubscriber<SoundscapeSetModel, SoundscapeModel> subscriber : subscribers){
			subscriber.notify(console, removedIndex);
		}
	}
	
	private void notify(int index, SoundscapeSetModel console, SoundscapeModel ss, LinkedList<ISubscriber<SoundscapeSetModel, SoundscapeModel>> subscribers) {
		for (ISubscriber<SoundscapeSetModel, SoundscapeModel> subscriber : subscribers){
			subscriber.notify(index, console, ss);
		}
	}
	
	/**
	 * Notify subscribers of a particular soundscape of a change in the model
	 * @param ss
	 * @param subscribers
	 */
	private void notify(SoundscapeModel ss, int removedIndex, LinkedList<ISubscriber<SoundscapeModel, SoundModel>> subscribers){
		for (ISubscriber<SoundscapeModel, SoundModel> subscriber : subscribers){
			subscriber.notify(ss, removedIndex);
		}
	}
	
	private void notify(int index, SoundscapeModel ss, SoundModel sound, LinkedList<ISubscriber<SoundscapeModel, SoundModel>> subscribers){
		for (ISubscriber<SoundscapeModel, SoundModel> subscriber : subscribers){
			subscriber.notify(index, ss, sound);
		}
	}
	
	/**
	 * 
	 * @param c
	 * @param subscriber
	 */
	public void subscribeToConsole(Sections c, ISubscriber<SoundscapeSetModel, SoundscapeModel> subscriber){
		switch (c){
		case CONSOLE1:
			this.console1Subscribers.add(subscriber);
			break;
		case CONSOLE2:
			this.console2Subscribers.add(subscriber);
			break;
		default:
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * 
	 * @param c
	 * @param subscriber
	 */
	public void subscribeToActiveSoundscape(Sections c, ISubscriber<SoundscapeModel, SoundModel> subscriber){
		switch(c){
		case CONSOLE1:
			this.console1ActiveSsSubscribers.add(subscriber);
			break;
		case CONSOLE2:
			this.console2ActiveSsSubscribers.add(subscriber);
			break;
		default:
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * 
	 * @param subscriber
	 */
	public void subscribeToEffects(ISubscriber<SoundscapeModel, SoundModel> subscriber){
		this.effectsSubscribers.add(subscriber);
	}
	
	/**
	 * 
	 * @param subscriber
	 */
	public void subscribeToPreview(ISubscriber<SoundscapeModel, SoundModel> subscriber){
		this.previewSubscribers.add(subscriber);
	}
	
	/* Start Defining interaction methods. These methods change the data and notify
	 * subscribers with the new models.
	 */

	/**
	 * Sets the active soundscape for a console - correspond to active tab in gui
	 * @param console
	 * @param ss
	 * @throws IllegalArgumentException if console is not either CONSOLE1 or CONSOLE2
	 * @throws NoMatchFoundException if ssid not in console
	 */
	public void setActiveSoundscape(Sections console, int ssid) throws IllegalArgumentException, NoMatchFoundException {
		if (console == OperationsManager.Sections.CONSOLE1){
			this.console1 = this.console1.setActiveSoundscape(this.console1.getSoundscapeBySsid(ssid));
			this.notify(this.console1, -1, this.console1Subscribers);
			this.notify(this.console1.activeSoundscape, -1, this.console1ActiveSsSubscribers);
		} else if (console == OperationsManager.Sections.CONSOLE2){
			this.console2 = this.console2.setActiveSoundscape(this.console2.getSoundscapeBySsid(ssid));
			this.notify(this.console2, -1, this.console2Subscribers);
			this.notify(this.console2.activeSoundscape, -1, this.console2ActiveSsSubscribers);
		} else{
			throw new IllegalArgumentException("Invalid section passed");
		}
	}
	
	/* All below methods operate on the active soundscape. In order for a soundscape
	 * to be modified by the gui, it must be active
	 */
	
	/**
	 * Sets the master volume for a soundscape, not for any particular sound
	 * @param section
	 * @param volume If greater than 1 or less than 0, will be rounded
	 * @throws IllegalArgumentException if section is invalid
	 */
	public void setSoundscapeVolume(Sections section, double volume) throws IllegalArgumentException {
		switch (section){
		
		case CONSOLE1:
			this.console1 = setSoundscapeVolume(this.console1, volume);
			notify(this.console1.activeSoundscapeIndex, this.console1, this.console1.activeSoundscape, this.console1Subscribers);
			notify(this.console1.activeSoundscape, -1, this.console1ActiveSsSubscribers);
			break;
			
		case CONSOLE2:
			this.console2 = setSoundscapeVolume(this.console2, volume);
			notify(this.console2.activeSoundscapeIndex, this.console2, this.console2.activeSoundscape, this.console2Subscribers);
			notify(this.console2.activeSoundscape, -1, this.console2ActiveSsSubscribers);
			break;
			
		case EFFECTS:
			this.effects = this.effects.setMasterVolume(volume);
			notify(this.effects, -1, this.effectsSubscribers);
			break;
			
		case PREVIEW:
			this.preview = this.preview.setMasterVolume(volume);
			notify(this.preview, -1, this.previewSubscribers);
			break;
			
		default:
			throw new IllegalArgumentException("Invalid section");
		}
	}
	
	private SoundscapeSetModel setSoundscapeVolume(SoundscapeSetModel set, double volume){
		SoundscapeModel ss = set.activeSoundscape;
		return set.replaceSoundscape(ss.setMasterVolume(volume));
	}
	
	/**
	 * Save a soundscape to the DB. If not already assigned, assigns an ssid to the soundscape
	 * @param section
	 */
	public void saveSoundscape(Sections section){
		//TODO Implement save function. Will use Database. Ideally should use existing DB methods
		//Will return an SSID, so possibly trigger a change in the models and resulting notification
	}
	
	/*
	 * TODO Review DB access model, implement
	 * Below is a stub representing a set of DB access functions.
	 * May consider exposing a 'create query' method of some kind
	 * that returns another controller object that crafts a query.<br>
	 * For instance:
	 *  - startQuery().Soundscape.bySsid(int ssid).execute()
	 *  - startQuery().Soundscape.byKeyword(String keyword).execute()
	 *  - startQuery().Sound.byName(String name).execute()
	 * etc...
	 */
	public SoundscapeModel getSoundscapeFromDb(int ssid){
		//TODO Implement
		//This method should retrieve a soundscape from the db
		
		return defaultSs;
	}
	
	/**
	 * Adds a new soundscape to Consoles 1 and 2, clears existing soundscapes in either
	 * preview or transition
	 * @param section
	 */
	public void newSoundscape(Sections section){
		SoundscapeModel newSs = defaultSs.copy(runtimeId++);

		this.addSoundscape(section, newSs);
	}
	
	/**
	 * While the intention is to have a soundscape copied within a console to a new soundscape
	 * within the same console, this method can effectively be used to copy any soundscape
	 * you have a reference to into any sound section as a new soundscape (with a new
	 * uninitialized (i.e. negative) ssid
	 * @param section
	 * @param ss
	 * @throws IllegalArgumentException if section is not a sound section
	 */
	public void copySoundscape(Sections section, SoundscapeModel ss) throws IllegalArgumentException {
		switch (section){
		
		case CONSOLE1:
		case CONSOLE2:
		case EFFECTS:
		case PREVIEW:
			SoundscapeModel newSs = ss.copy(runtimeId++).setSsid(-1);
			this.addSoundscape(section, newSs);
			break;
			
		default:
			throw new IllegalArgumentException("Not a valid section");
		}
	}
	
	/**
	 * For Consoles 1 and 2, adds a new soundscape to the set and makes it active. For the Preview and
	 * Effects sections, replaces the current soundscape
	 * @param section
	 * @param ss
	 * @throws IllegalArgumentException if the section passed is invalid
	 */
	public void addSoundscape(Sections section, SoundscapeModel ss) throws IllegalArgumentException {
		switch(section){
		
		case CONSOLE1:
			this.console1 = this.console1.addSoundscape(ss).setActiveSoundscape(ss);
			notify(this.console1.getTotalSoundscapes() - 1, this.console1, ss, this.console1Subscribers);
			notify(this.console1.activeSoundscape, -1, this.console1ActiveSsSubscribers);
			break;
			
		case CONSOLE2:
			this.console2 = this.console2.addSoundscape(ss).setActiveSoundscape(ss);
			notify(this.console2.getTotalSoundscapes() - 1, this.console2, ss, this.console2Subscribers);
			notify(this.console2.activeSoundscape, -1, this.console2ActiveSsSubscribers);
			break;
			
		case EFFECTS:
			this.effects = ss;
			notify(this.effects, -1, this.effectsSubscribers);
			break;
			
		case PREVIEW:
			this.preview = ss;
			notify(this.preview, -1, this.previewSubscribers);
			break;
			
		default:
			throw new IllegalArgumentException("Not a valid section");
		}
	}
	
	/**
	 * Toggles play state to either PLAYING or STOPPED, stepping on
	 * any current fades
	 * @param section
	 * @throws IllegalArgumentException if invalid section
	 */
	public void toggleSoundscapePlay(Sections section) throws IllegalArgumentException {
		SoundscapeModel ss;
		
		switch(section){
		
		case CONSOLE1:
			ss = this.console1.activeSoundscape
				.setIsPlaying(this.console1.activeSoundscape.playState != SoundscapeModel.PlayState.STOPPED);
			this.console1 = this.console1.replaceSoundscape(ss);
			this.notify(this.console1.activeSoundscapeIndex, this.console1, this.console1.activeSoundscape, this.console1Subscribers);
			this.notify(this.console1.activeSoundscape, -1, this.console1ActiveSsSubscribers);
			break;
			
		case CONSOLE2:
			ss = this.console2.activeSoundscape
				.setIsPlaying(this.console2.activeSoundscape.playState != SoundscapeModel.PlayState.STOPPED);
			this.console2 = this.console2.replaceSoundscape(ss);
			this.notify(this.console2.activeSoundscapeIndex, this.console2, this.console2.activeSoundscape, this.console2Subscribers);
			this.notify(this.console2.activeSoundscape, -1, this.console2ActiveSsSubscribers);
			break;
			
		case EFFECTS:
			this.effects = this.effects
				.setIsPlaying(this.effects.playState != SoundscapeModel.PlayState.STOPPED);
			this.notify(this.effects, -1, this.effectsSubscribers);
			break;
			
		case PREVIEW:
			this.preview = this.preview
				.setIsPlaying(this.preview.playState != SoundscapeModel.PlayState.STOPPED);
			this.notify(this.preview, -1, this.previewSubscribers);
			break;
			
		default:
			throw new IllegalArgumentException("Invalid section");
		}
	}
	
	public void addSound(Sections section, SoundModel sound) throws IllegalArgumentException {
		SoundscapeModel ss;
		
		switch(section){
		
		case CONSOLE1:
			ss = this.console1.activeSoundscape.addSound(sound);
			this.console1 = this.console1.replaceSoundscape(ss);
			this.notify(this.console1.activeSoundscapeIndex, this.console1, this.console1.activeSoundscape, this.console1Subscribers);
			this.notify(this.console1.activeSoundscape.getSoundIndex(sound), this.console1.activeSoundscape, sound, this.console1ActiveSsSubscribers);
			break;
			
		case CONSOLE2:
			ss = this.console2.activeSoundscape.addSound(sound);
			this.console2 = this.console2.replaceSoundscape(ss);
			this.notify(this.console2.activeSoundscapeIndex, this.console2, this.console2.activeSoundscape, this.console2Subscribers);
			this.notify(this.console2.activeSoundscape.getSoundIndex(sound), this.console2.activeSoundscape, sound, this.console2ActiveSsSubscribers);
			break;
			
		case EFFECTS:
			this.effects = this.effects.addSound(sound);
			this.notify(this.effects.getSoundIndex(sound), this.effects, sound, this.effectsSubscribers);
			break;
			
		case PREVIEW:
			this.preview = this.preview.addSound(sound);
			this.notify(this.preview.getSoundIndex(sound), this.preview, sound, this.previewSubscribers);
			break;
			
		default:
			throw new IllegalArgumentException("Invalid section");
		}
	}
	
	public void removeSound(Sections section, int index) throws ArrayIndexOutOfBoundsException {
		SoundscapeModel ss;
		
		switch(section){
		
		case CONSOLE1:
			ss = this.console1.activeSoundscape.removeSound(index);
			this.console1 = this.console1.replaceSoundscape(ss);
			this.notify(this.console1.activeSoundscapeIndex, this.console1, this.console1.activeSoundscape, this.console1Subscribers);
			this.notify(this.console1.activeSoundscape, index, this.console1ActiveSsSubscribers);
			break;
			
		case CONSOLE2:
			ss = this.console2.activeSoundscape.removeSound(index);
			this.console2 = this.console2.replaceSoundscape(ss);
			this.notify(this.console2.activeSoundscapeIndex, this.console2, this.console2.activeSoundscape, this.console2Subscribers);
			this.notify(this.console2.activeSoundscape, index, this.console2ActiveSsSubscribers);
			break;
			
		case EFFECTS:
			this.effects = this.effects.removeSound(index);
			this.notify(this.effects, index, this.effectsSubscribers);
			break;
			
		case PREVIEW:
			this.preview = this.preview.removeSound(index);
			this.notify(this.preview, index, this.previewSubscribers);
			break;
			
		default:
			throw new IllegalArgumentException("Invalid section");
		}
	}
	
	/**
	 * Takes all possible changes in a sound object and determines the best
	 * modification method to use
	 * @param section
	 * @param index
	 * @param playType
	 * @param isPlaying
	 * @param volume
	 * @throws IllegalArgumentException if invalid section
	 * @throws ArrayIndexOutOfBoundsException if invalid index
	 */
	public void modifySound(Sections section, int index, SoundModel.PlayType playType, boolean isPlaying, double volume)
			throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
		
		SoundscapeModel ss;
		
		switch(section){
		
		case CONSOLE1:
			ss = modifySound(this.console1.activeSoundscape, index, playType, isPlaying, volume);
			this.console1 = this.console1.replaceSoundscape(ss);
			this.notify(this.console1.activeSoundscapeIndex, this.console1, this.console1.activeSoundscape, this.console1Subscribers);
			this.notify(index, this.console1.activeSoundscape, this.console1.activeSoundscape.getSoundAtIndex(index), this.console1ActiveSsSubscribers);
			break;
			
		case CONSOLE2:
			ss = modifySound(this.console2.activeSoundscape, index, playType, isPlaying, volume);
			this.console2 = this.console2.replaceSoundscape(ss);
			this.notify(this.console2.activeSoundscapeIndex, this.console2, this.console2.activeSoundscape, this.console2Subscribers);
			this.notify(index, this.console2.activeSoundscape, this.console2.activeSoundscape.getSoundAtIndex(index), this.console2ActiveSsSubscribers);
			break;
			
		case EFFECTS:
			this.effects = modifySound(this.effects, index, playType, isPlaying, volume);
			this.notify(index, this.effects, this.effects.getSoundAtIndex(index), this.effectsSubscribers);
			break;
			
		case PREVIEW:
			this.preview = modifySound(this.preview, index, playType, isPlaying, volume);
			this.notify(index, this.preview, this.preview.getSoundAtIndex(index), this.previewSubscribers);
			break;
			
		default:
			throw new IllegalArgumentException("Invalid section");
		}
	}
	
	/**
	 * Private access - modifies sound, but chooses the appropriate method depending on what's changed
	 * @param ss
	 * @param index
	 * @param playType
	 * @param isPlaying
	 * @param volume
	 * @return
	 */
	private SoundscapeModel modifySound(SoundscapeModel ss, int index, SoundModel.PlayType playType, boolean isPlaying, double volume) {
		SoundModel sound = ss.getSoundAtIndex(index);
		int changes = 0;
		boolean p = false;
		boolean iP = false;
		boolean v = false;
		
		if (sound.currentPlayType != playType) {
			p = true;
			changes++;
		}
		if (sound.isPlaying != isPlaying){
			iP = true;
			changes++;
		}
		if (sound.volume != volume){
			v = true;
			changes++;
		}
		
		if (changes > 1){
			sound = sound.setAll(isPlaying, playType, volume);
		} else if (p){
			sound = sound.setPlayType(playType);
		} else if (iP){
			sound = sound.setPlay(isPlaying);
		} else if (v){
			sound = sound.setVolume(volume);
		}
		
		return ss.replaceSound(index, sound);
	}
	
	/**
	 * Initiates a fade. Must be either fade in or fade out
	 * @param section
	 * @param fade
	 * @param durationMs Duration of the fade in milliseconds
	 * @throws IllegalArgumentException if fade passed as neither FADEIN nor FADEOUT or invalid section
	 */
	public void fadeSoundscape(Sections section, SoundscapeModel.PlayState fade, int durationMs) throws IllegalArgumentException {
		SoundscapeModel ss;
		
		switch(section){
		
		case CONSOLE1:
			ss = this.fadeSoundscape(this.console1.activeSoundscape, fade, durationMs);
			this.console1 = this.console1.replaceSoundscape(ss);
			this.notify(this.console1.activeSoundscapeIndex, this.console1, this.console1.activeSoundscape, this.console1Subscribers);
			this.notify(this.console1.activeSoundscape, -1, this.console1ActiveSsSubscribers);
			break;
			
		case CONSOLE2:
			ss = this.fadeSoundscape(this.console2.activeSoundscape, fade, durationMs);
			this.console2 = this.console2.replaceSoundscape(ss);
			this.notify(this.console2.activeSoundscapeIndex, this.console2, this.console2.activeSoundscape, this.console2Subscribers);
			this.notify(this.console2.activeSoundscape, -1, this.console2ActiveSsSubscribers);
			break;
			
		case EFFECTS:
			this.effects = this.fadeSoundscape(this.effects, fade, durationMs);
			this.notify(this.effects, -1, this.effectsSubscribers);
			break;
			
		case PREVIEW:
			this.preview = this.fadeSoundscape(this.preview, fade, durationMs);
			this.notify(this.preview, -1, this.previewSubscribers);
			break;
			
		default:
			throw new IllegalArgumentException("Invalid section");
		}
	}
	
	/**
	 * Sets the fade state for a soundscape
	 * @param ss
	 * @param fade
	 * @param duration
	 * @return
	 */
	private SoundscapeModel fadeSoundscape(SoundscapeModel ss, PlayState fade, int duration){
		if (fade != PlayState.FADEIN && fade != PlayState.FADEOUT) {
			throw new IllegalArgumentException("Invalid fade type");
		}
		
		return ss.setFadeState(fade, duration);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
