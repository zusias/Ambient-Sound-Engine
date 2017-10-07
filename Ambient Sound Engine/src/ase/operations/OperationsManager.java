package ase.operations;


//Java
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Files;

//Utilities
import java.util.LinkedList;

import com.google.common.eventbus.EventBus;

import ase.operations.SoundscapeModel.PlayState;
import ase.operations.events.ChangedSoundscapeEvent;
import ase.operations.events.ChangedSoundscapeSetEvent;

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
 * NOTE: Consider altering Subject / Observer in favor of an event bus.
 * Perhaps v5.1 or v6.0
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
	
	//utility app objects
	public final Log logger;
	public final EventBus eventBus = new EventBus();
	
	//Data models
	private SoundscapeSetModel console1;
	private SoundscapeSetModel console2;
	private SoundscapeModel effects;
	
	//This data model is not rendered in the UI except by a preview button
	private SoundscapeModel preview;
	
	//Subscriber sets
	private final LinkedList<IIterableSubscriber<SoundscapeModel, SoundModel>> console1ActiveSsSubscribers = new LinkedList<>();
	private final LinkedList<IIterableSubscriber<SoundscapeModel, SoundModel>> console2ActiveSsSubscribers = new LinkedList<>();
	private final LinkedList<IIterableSubscriber<SoundscapeModel, SoundModel>> effectsSubscribers = new LinkedList<>();
	private final LinkedList<IIterableSubscriber<SoundscapeModel, SoundModel>> previewSubscribers = new LinkedList<>();
	
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
		
		eventBus.register(this);
		
		logger.log(DEBUG, "Ending Ops Constructor");
	}
	
	/**
	 * Publish a change in a console. If removedIndex > -1, that indicates a
	 * soundscape has been removed from the set
	 * @param section The section of the console to publish
	 * @param removedIndex If a soundscape has been removed, its index. Otherwise -1
	 */
	private void publishConsole(Sections section, int removedIndex){
		SoundscapeSetModel console = getSsSetModelFromSection(section);
		eventBus.post(new ChangedSoundscapeSetEvent(section, console, null, removedIndex));
	}
	
	/**
	 * Publish a change in a console, specifying a new soundscape model
	 * @param section The section of the console to publish
	 * @param ss The soundscape that changed
	 * @param index The index of the soundscape that changed
	 */
	private void publishConsole(Sections section, SoundscapeModel ss, int index) {
		SoundscapeSetModel console = getSsSetModelFromSection(section);
		eventBus.post(new ChangedSoundscapeSetEvent(section, console, ss, index));
	}
	
	/**
	 * Publish a change in the Active Soundscape of a console
	 * @param section The section of the console to publish
	 */
	private void publishConsoleActiveSs(Sections section) {
		SoundscapeSetModel console = getSsSetModelFromSection(section);
		eventBus.post(new ChangedSoundscapeSetEvent(section, console, console.activeSoundscape, console.activeSoundscapeIndex));
	}
	
	private SoundscapeSetModel getSsSetModelFromSection(Sections section) {
		switch(section) {
			case CONSOLE1:
				return this.console1;
			case CONSOLE2:
				return this.console2;
			default:
				throw new IllegalArgumentException("Not a valid section");
		}
	}
	
	/**
	 * Notify subscribers of a particular soundscape of a change in the model
	 * @param ss
	 * @param subscribers
	 */
	private void publishSoundscape(Sections section, int removedIndex){
		SoundscapeModel ss = getActiveSoundscapeFromSection(section);
		eventBus.post(new ChangedSoundscapeEvent(section, ss, null, removedIndex));
	}
	
	private void publishSoundscape(Sections section, SoundModel sound, int index){
		SoundscapeModel ss = getActiveSoundscapeFromSection(section);
		eventBus.post(new ChangedSoundscapeEvent(section, ss, sound, index));
	}
	
	private SoundscapeModel getActiveSoundscapeFromSection(Sections section) {
		switch(section) {
			case CONSOLE1:
				return this.console1.activeSoundscape;
			case CONSOLE2:
				return this.console2.activeSoundscape;
			case EFFECTS:
				return this.effects;
			case PREVIEW: 
				return this.preview;
			default:
				throw new IllegalArgumentException("Unknown section");
		}
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
		if (console == Sections.CONSOLE1){
			this.console1 = this.console1.setActiveSoundscape(this.console1.getSoundscapeBySsid(ssid));
			this.publishConsole(Sections.CONSOLE1, -1);
			this.publishSoundscape(Sections.CONSOLE1, -1);
		} else if (console == Sections.CONSOLE2){
			this.console2 = this.console2.setActiveSoundscape(this.console2.getSoundscapeBySsid(ssid));
			this.publishConsole(Sections.CONSOLE2, -1);
			this.publishSoundscape(Sections.CONSOLE2, -1);
		} else{
			throw new IllegalArgumentException("Invalid section passed");
		}
	}
	
	/* All below methods operate on the active soundscape. In order for a soundscape
	 * to be modified by any observers, it must be active
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
			publishConsoleActiveSs(Sections.CONSOLE1);
			publishSoundscape(Sections.CONSOLE1, -1);
			break;
			
		case CONSOLE2:
			this.console2 = setSoundscapeVolume(this.console2, volume);
			publishConsoleActiveSs(Sections.CONSOLE2);
			publishSoundscape(Sections.CONSOLE2, -1);
			break;
			
		case EFFECTS:
			this.effects = this.effects.setMasterVolume(volume);
			publishSoundscape(Sections.EFFECTS, -1);
			break;
			
		case PREVIEW:
			this.preview = this.preview.setMasterVolume(volume);
			publishSoundscape(Sections.PREVIEW, -1);
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
			publishConsole(Sections.CONSOLE1, ss, this.console1.getTotalSoundscapes() - 1);
			publishSoundscape(Sections.CONSOLE1, -1);
			break;
			
		case CONSOLE2:
			this.console2 = this.console2.addSoundscape(ss).setActiveSoundscape(ss);
			publishConsole(Sections.CONSOLE2, ss, this.console2.getTotalSoundscapes() - 1);
			publishSoundscape(Sections.CONSOLE2, -1);
			break;
			
		case EFFECTS:
			this.effects = ss;
			publishSoundscape(Sections.EFFECTS, -1);
			break;
			
		case PREVIEW:
			this.preview = ss;
			publishSoundscape(Sections.PREVIEW, -1);
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
				.setIsPlaying(this.console1.activeSoundscape.playState == SoundscapeModel.PlayState.STOPPED);
			this.console1 = this.console1.replaceSoundscape(ss);
			this.publishConsoleActiveSs(Sections.CONSOLE1);
			publishSoundscape(Sections.CONSOLE1, -1);
			break;
			
		case CONSOLE2:
			ss = this.console2.activeSoundscape
				.setIsPlaying(this.console2.activeSoundscape.playState == SoundscapeModel.PlayState.STOPPED);
			this.console2 = this.console2.replaceSoundscape(ss);
			this.publishConsoleActiveSs(Sections.CONSOLE2);
			publishSoundscape(Sections.CONSOLE2, -1);
			break;
			
		case EFFECTS:
			this.effects = this.effects
				.setIsPlaying(this.effects.playState == SoundscapeModel.PlayState.STOPPED);
			publishSoundscape(Sections.EFFECTS, -1);
			break;
			
		case PREVIEW:
			this.preview = this.preview
				.setIsPlaying(this.preview.playState == SoundscapeModel.PlayState.STOPPED);
			publishSoundscape(Sections.PREVIEW, -1);
			break;
			
		default:
			throw new IllegalArgumentException("Invalid section");
		}
	}
	
	public void setSoundscapeIsPlaying(Sections section, boolean isPlaying) {
		SoundscapeModel ss;
		
		switch(section){
		
		case CONSOLE1:
			ss = this.console1.activeSoundscape
				.setIsPlaying(isPlaying);
			this.console1 = this.console1.replaceSoundscape(ss);
			this.publishConsoleActiveSs(Sections.CONSOLE1);
			publishSoundscape(Sections.CONSOLE1, -1);
			break;
			
		case CONSOLE2:
			ss = this.console2.activeSoundscape
				.setIsPlaying(isPlaying);
			this.console2 = this.console2.replaceSoundscape(ss);
			this.publishConsoleActiveSs(Sections.CONSOLE2);
			publishSoundscape(Sections.CONSOLE2, -1);
			break;
			
		case EFFECTS:
			this.effects = this.effects
				.setIsPlaying(isPlaying);
			publishSoundscape(Sections.EFFECTS, -1);
			break;
			
		case PREVIEW:
			this.preview = this.preview
				.setIsPlaying(isPlaying);
			publishSoundscape(Sections.PREVIEW, -1);
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
			this.publishConsoleActiveSs(Sections.CONSOLE1);
			this.publishSoundscape(Sections.CONSOLE1, sound, this.console1.activeSoundscape.getSoundIndex(sound));
			break;
			
		case CONSOLE2:
			ss = this.console2.activeSoundscape.addSound(sound);
			this.console2 = this.console2.replaceSoundscape(ss);
			this.publishConsoleActiveSs(Sections.CONSOLE2);
			this.publishSoundscape(Sections.CONSOLE2, sound, this.console2.activeSoundscape.getSoundIndex(sound));
			break;
			
		case EFFECTS:
			this.effects = this.effects.addSound(sound);
			this.publishSoundscape(Sections.EFFECTS, sound, this.effects.getSoundIndex(sound));
			break;
			
		case PREVIEW:
			this.preview = this.preview.addSound(sound);
			this.publishSoundscape(Sections.PREVIEW, sound, this.preview.getSoundIndex(sound));
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
			this.publishConsoleActiveSs(Sections.CONSOLE1);
			this.publishSoundscape(Sections.CONSOLE1, index);
			break;
			
		case CONSOLE2:
			ss = this.console2.activeSoundscape.removeSound(index);
			this.console2 = this.console2.replaceSoundscape(ss);
			this.publishConsoleActiveSs(Sections.CONSOLE2);
			this.publishSoundscape(Sections.CONSOLE2, index);
			break;
			
		case EFFECTS:
			this.effects = this.effects.removeSound(index);
			this.publishSoundscape(Sections.EFFECTS, index);
			break;
			
		case PREVIEW:
			this.preview = this.preview.removeSound(index);
			this.publishSoundscape(Sections.PREVIEW, index);
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
			this.publishConsoleActiveSs(Sections.CONSOLE1);
			this.publishSoundscape(Sections.CONSOLE1, this.console1.activeSoundscape.getSoundAtIndex(index), index);
			break;
			
		case CONSOLE2:
			ss = modifySound(this.console2.activeSoundscape, index, playType, isPlaying, volume);
			this.console2 = this.console2.replaceSoundscape(ss);
			this.publishConsoleActiveSs(Sections.CONSOLE2);
			this.publishSoundscape(Sections.CONSOLE2, this.console2.activeSoundscape.getSoundAtIndex(index), index);
			break;
			
		case EFFECTS:
			this.effects = modifySound(this.effects, index, playType, isPlaying, volume);
			this.publishSoundscape(Sections.EFFECTS, this.effects.getSoundAtIndex(index), index);
			break;
			
		case PREVIEW:
			this.preview = modifySound(this.preview, index, playType, isPlaying, volume);
			this.publishSoundscape(Sections.PREVIEW, this.preview.getSoundAtIndex(index), index);
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
			sound = sound.setAll(isPlaying, playType, volume, sound.randomSettings);
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
	 * Modify the random play settings for a sound
	 * @param section
	 * @param index of the sound in the soundscape
	 * @param minDelay
	 * @param maxDelay
	 * @param minRepeats
	 * @param maxRepeats
	 */
	public void modifySoundRandomPlaySettings(
			Sections section,
			int index,
			int minDelay,
			int maxDelay,
			int minRepeats,
			int maxRepeats) {
		RandomPlaySettings newSettings = new RandomPlaySettings(minDelay, maxDelay, minRepeats, maxRepeats);
		SoundscapeModel ss;
		
		switch(section){
		
		case CONSOLE1:
			ss = modifySoundRandomPlaySettings(this.console1.activeSoundscape, index, newSettings);
			this.console1 = this.console1.replaceSoundscape(ss);
			this.publishConsoleActiveSs(Sections.CONSOLE1);
			this.publishSoundscape(Sections.CONSOLE1, this.console1.activeSoundscape.getSoundAtIndex(index), index);
			break;
			
		case CONSOLE2:
			ss = modifySoundRandomPlaySettings(this.console2.activeSoundscape, index, newSettings);
			this.console2 = this.console2.replaceSoundscape(ss);
			this.publishConsoleActiveSs(Sections.CONSOLE2);
			this.publishSoundscape(Sections.CONSOLE2, this.console2.activeSoundscape.getSoundAtIndex(index), index);
			break;
			
		case EFFECTS:
			this.effects = modifySoundRandomPlaySettings(this.effects, index, newSettings);
			this.publishSoundscape(Sections.EFFECTS, this.effects.getSoundAtIndex(index), index);
			break;
			
		case PREVIEW:
			this.preview = modifySoundRandomPlaySettings(this.preview, index, newSettings);
			this.publishSoundscape(Sections.PREVIEW, this.preview.getSoundAtIndex(index), index);
			break;
			
		default:
			throw new IllegalArgumentException("Invalid section");
		}
	}
	
	/**
	 * Private helper method for modifySoundRandomPlaySettings
	 * @param ss
	 * @param index
	 * @param randomSettings
	 * @return
	 */
	private SoundscapeModel modifySoundRandomPlaySettings(SoundscapeModel ss, int index, RandomPlaySettings randomSettings) {
		SoundModel sound = ss.getSoundAtIndex(index);
		sound = sound.setRandomPlaySettings(randomSettings);
		
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
			this.publishConsoleActiveSs(Sections.CONSOLE1);
			this.publishSoundscape(Sections.CONSOLE1, -1);
			break;
			
		case CONSOLE2:
			ss = this.fadeSoundscape(this.console2.activeSoundscape, fade, durationMs);
			this.console2 = this.console2.replaceSoundscape(ss);
			this.publishConsoleActiveSs(Sections.CONSOLE2);
			this.publishSoundscape(Sections.CONSOLE2, -1);
			break;
			
		case EFFECTS:
			this.effects = this.fadeSoundscape(this.effects, fade, durationMs);
			this.publishSoundscape(Sections.EFFECTS, -1);
			break;
			
		case PREVIEW:
			this.preview = this.fadeSoundscape(this.preview, fade, durationMs);
			this.publishSoundscape(Sections.PREVIEW, -1);
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
