package ase.soundengine.fmodex;

//Java imports
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.System.exit;

import ase.operations.ISubscriber;
import ase.operations.SoundModel;
import ase.operations.SoundModel.PlayType;
import ase.operations.SoundscapeModel;
import ase.operations.SoundscapeModel.PlayState;
import ase.soundengine.SoundEngine;
import ase.soundengine.SoundEngineException;
import ase.operations.OperationsManager;
import ase.operations.OperationsManager.Sections;
import ase.operations.RandomPlaySettings;
import ase.operations.Log;
import static ase.operations.OperationsManager.Sections.*;
import static ase.operations.SoundscapeModel.PlayState.*;
import static ase.operations.SoundModel.PlayType.*;
import static ase.operations.Log.LogLevel.*;

//FmodEx imports
import org.jouvieje.FmodEx.System;
import org.jouvieje.FmodEx.Defines.INIT_MODES;
import org.jouvieje.FmodEx.Misc.BufferUtils;
import org.jouvieje.FmodEx.FmodEx;
import org.jouvieje.FmodEx.Init;
import org.jouvieje.FmodEx.Enumerations.FMOD_RESULT;
import org.jouvieje.FmodEx.Exceptions.InitException;
import org.jouvieje.FmodEx.ChannelGroup;
import org.jouvieje.FmodEx.Channel;
import org.jouvieje.FmodEx.Sound;
import org.jouvieje.FmodEx.Callbacks.FMOD_CHANNEL_CALLBACK;
import org.jouvieje.FmodEx.Enumerations.FMOD_CHANNEL_CALLBACKTYPE;
import static org.jouvieje.FmodEx.Defines.FMOD_INITFLAGS.FMOD_INIT_NORMAL;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_HARDWARE;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_LOOP_NORMAL;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_LOOP_OFF;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_SOFTWARE;
import static org.jouvieje.FmodEx.Enumerations.FMOD_CHANNELINDEX.FMOD_CHANNEL_FREE;
import static org.jouvieje.FmodEx.Defines.VERSIONS.FMOD_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_JAR_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_LIBRARY_VERSION;
import static org.jouvieje.FmodEx.Enumerations.FMOD_CHANNEL_CALLBACKTYPE.FMOD_CHANNEL_CALLBACKTYPE_END;
import static org.jouvieje.FmodEx.Enumerations.FMOD_RESULT.FMOD_OK;


//Test imports
import ase.operations.TestDataProvider;

/**
 * <p>Implementation of the SoundEngine abstract class. This implementation
 * uses the FmodExEngine to support sound card interaction.
 * <br>
 * Documentation:
 * <br>
 * <a href='http://www.fmod.org/documentation/#content/generated/lowlevel_api.html'>FmodEx Docs</a><br>
 * The above links to the most recent version of the software, which I believe outdates
 * the library being used in this project.</p>
 * <p>The java wrapper being used to interact with the native library is found at:<br>
 * <a href='http://jerome.jouvie.free.fr/nativefmodex/javadoc/index.html'>NativeFmodEx Javadoc</a><br>
 * Javadoc is a stub: no specific explanations were written with the javadoc. All information
 * about the functionality of the library must be matched with the documentation of the native library.</p>
 * 
 * @author Kevin C. Gall
 *
 */
public class FmodExEngine extends SoundEngine {
	//static variables
	private static int driverCount = 0;
	private static boolean startupSuccess = true;
	private static String initFailMessage = null;
	public static final Log logger = OperationsManager.opsMgr.logger;
	public static final Random random = new Random();
	
	//private final static int SOUND_BANK_CAPACITY = 1000; Not used yet
	private final static int STREAM_THRESHOLD_BYTES = 500000;
	
	//private final variables
	private final System system = new System();
	private final Map<Integer, ChannelGroupWrapper> channelGroups = new HashMap<>();
	private final ExecutorService threadPool = Executors.newCachedThreadPool();
	
	//the update thread and control variable (boolean)
	private boolean updateInterrupt = false;
	//create new thread on object construction with runnable
	private final Runnable updateRunner = () -> {
		try {
			while(!updateInterrupt) {
				system.update();
				
				Thread.sleep(500);
			}
		} catch (InterruptedException e) {
			logger.log(DEV, "Update thread interrupted");
		} finally {
			internalShutdown();
		}
	};
	
	
	//public constants
	public final int driverId;
	public final String driverName;
	
	//initial load of FmodEx library
	static {
		try {
			logger.log(DEV, "Loading FmodEx Library");
			Init.loadLibraries(INIT_MODES.INIT_FMOD_EX);
		} catch (InitException e){
			initFailMessage = e.getMessage();
			startupSuccess = false;
		}
		
		//Checking NativeFmodEx version against version of package
		if (NATIVEFMODEX_LIBRARY_VERSION != NATIVEFMODEX_JAR_VERSION) {
			initFailMessage = "Error! NativeFmodEx library version ("
					+ Integer.toString(NATIVEFMODEX_LIBRARY_VERSION)
					+ ") is different than jar version ("
					+ Integer.toString(NATIVEFMODEX_JAR_VERSION);
			
			startupSuccess = false;
		}
	}
	
	/**
	 * Takes the 0-based driver index and instantiates
	 * the Engine with the specified driver.
	 * 
	 * If the driver does not exist, throws error
	 * @param driverId
	 * @throws SoundEngineException If the specified driver does not exist
	 */
	public FmodExEngine(int driverIndex) throws SoundEngineException {
		throw new SoundEngineException("Constructor not implemented");
	}
	
	/**
	 * Initializes the SoundEngine with the next available driver.<br>
	 * Driver availability is determined statically based on the
	 * amount of times this constructor has been invoked, and is only
	 * meant as a convenience so that the user does not have to query
	 * the available drivers on the system manually.
	 * <br><br>
	 * If mixed with calls to {@link FmodExEngine#FmodExEngine(int)}, no checks
	 * will be made to ensure you are not trying to initialize a sound engine
	 * for a driver that is already initialized.
	 * 
	 * @throws SoundEngineException if there are no more drivers to initialize
	 */
	public FmodExEngine() throws SoundEngineException {
		if (!startupSuccess){
			throw new SoundEngineException(initFailMessage);
		}
		
		//initialize system object
		fmodErrCheck(FmodEx.System_Create(system));
		
		//discover number of available drivers and compare with initialized sound cards
		ByteBuffer buffer = BufferUtils.newByteBuffer(256);
		fmodErrCheck(system.getNumDrivers(buffer.asIntBuffer()));
		int numDrivers = buffer.getInt(0);
		
		if (driverCount >= numDrivers){
			throw new SoundEngineException("Driver already initialized");
		}
		
		//set system driver with 0-based driver id
		this.driverId = driverCount;
		fmodErrCheck(system.setDriver(driverCount));
		ByteBuffer nameBuffer = BufferUtils.newByteBuffer(256);
		fmodErrCheck(system.getDriverName(driverCount, nameBuffer, 256));
		
		this.driverName = BufferUtils.toString(nameBuffer);
		
		//init the system
		fmodErrCheck(system.init(32, FMOD_INIT_NORMAL, null));
		
		//Instantiate the update thread
		threadPool.submit(updateRunner);
		
		driverCount++;
	}
	
	static void fmodErrCheck(FMOD_RESULT result) {
		if (result != FMOD_OK) {
			logger.log(PROD, "Error with Sound Engine");
			logger.log(DEV, "FMOD Error! "
									+ result.asInt()
									+ " "
									+ FmodEx.FMOD_ErrorString(result));
			
			exit(-1);
		}
	}
	
	private void publishFinishedSound(int id, String name) {
		for (ISubscriber<String> subscriber : channelGroups.get(id).soundEndSubscribers) {
			subscriber.notifySubscriber(name);
		}
	}
	
	void publishFinishedFade(int id, Boolean isPlaying) {
		for (ISubscriber<Boolean> subscriber : channelGroups.get(id).fadeEndSubscribers) {
			subscriber.notifySubscriber(isPlaying);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * <br><b>NOTE 7/8/17:</b> This is a prototype implementation. Does not include
	 * appropriate logic for various PlayStates of Soundscape
	 */
	@Override
	public String[] loadSoundscape(SoundscapeModel ssModel, Sections section) throws SoundEngineException {
		int ssId = ssModel.runtimeId;
		
		logger.log(DEV, "Loading Soundscape " + ssId + " " + ssModel.name);
		
		if (channelGroups.get(ssId) != null) {
			throw new SoundEngineException("Soundscape with this ID already loaded. ID: " + ssId);
		}
		
		//Allocate and initialize new channel group
		ChannelGroupWrapper chGrp = new ChannelGroupWrapper();
		
		//Set wrapper's play state
		if (ssModel.playState != STOPPED && ssModel.playState != FADEOUT) {
			chGrp.setPlaying(true);
		}
		
		//add to all maps
		channelGroups.put(ssId, chGrp);
		
		fmodErrCheck(system.createChannelGroup(ssModel.name, chGrp.channelGroup));
		
		if (ssModel.playState != FADEIN) {
			logger.log(DEBUG, "Setting master volume for channel group");
			chGrp.channelGroup.setVolume((float)ssModel.masterVolume);
		} else {
			logger.log(DEBUG, "Initiating fade from 0.0 to " + ssModel.masterVolume);
			fadeSoundscape(ssModel.runtimeId, 0.0, ssModel.masterVolume, ssModel.fadeDuration);
		}
		
		String[] soundNames = new String[ssModel.getTotalSounds()];
		int count = 0;
		
		for (SoundModel sModel : ssModel){
			soundNames[count++] = loadSound(ssModel.runtimeId, sModel);
		}
		

		logger.log(DEBUG, "Soundscape fully loaded");
		return soundNames;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String loadSound(int id, SoundModel sModel) throws SoundEngineException {
		logger.log(DEV, "Loading sound " + sModel.name);
		
		Sound newSound = new Sound();
		ChannelGroupWrapper chGrp = this.channelGroups.get(id);
		
		
		if (sModel.sizeInBytes >= STREAM_THRESHOLD_BYTES) {
			logger.log(DEBUG, "Creating sound stream");
			fmodErrCheck(system.createStream(sModel.filePath.toString(), FMOD_SOFTWARE, null, newSound));
		} else {
			logger.log(DEBUG, "Creating sound without streaming");
			fmodErrCheck(system.createSound(sModel.filePath.toString(), FMOD_SOFTWARE,  null,  newSound));
		}
		
		//a unique name within this group if already taken
		String soundName = sModel.name;
		
		int copies = 0;
		while (chGrp.playbackObjects.get(soundName) != null) {
			copies++;
		}
		if (copies > 0){
			soundName += copies;
		}
		
		PlaybackObject playObj =
				new PlaybackObject(newSound, (float)sModel.volume, sModel.currentPlayType, soundName, sModel.randomSettings);
		chGrp.playbackObjects.put(soundName, playObj);
		
		if (sModel.isPlaying){
			playSound(id, soundName);
		}
		
		return sModel.name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearSound(int id, String symbol) throws SoundEngineException {
		logger.log(DEV, "Clearing sound " + symbol);
		
		ChannelGroupWrapper chGrp = channelGroups.get(id);
		if (chGrp == null){
			throw new SoundEngineException("Invalid soundscape ID");
		}
		
		//stop the sound
		stopSound(id, symbol);
		chGrp.playbackObjects.remove(symbol);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void playSound(int id, String symbol) throws SoundEngineException {
		logger.log(DEV, "Playing sound " + symbol);
		
		ChannelGroupWrapper chGrp = channelGroups.get(id);
		if (chGrp == null){
			throw new SoundEngineException("Invalid soundscape ID");
		}
		
		PlaybackObject playObj = chGrp.playbackObjects.get(symbol);
		if (playObj == null){
			throw new SoundEngineException("Invalid sound symbol for soundscape ID " + id);
		}
		
		//if play is random and no active random play thread, instantiate now
		//also unpause if paused without playing the sound prematurely
		PlayType playType = playObj.getPlayType();
		if (playType == RANDOM){
			RandomPlayRunner runner = chGrp.activeRandomPlayers.get(symbol);
			if (runner == null) {
				RandomPlayRunner randomPlayer = new RandomPlayRunner(this, id, playObj);
				
				chGrp.activeRandomPlayers.put(symbol, randomPlayer);
				threadPool.submit(randomPlayer);

				return;
			} else if (runner.isPaused()) {
				runner.setPaused(false);
				
				return;
			}
		}
		
		//get either existing or new channel
		boolean channelGroupPlaying = chGrp.isPlaying();
		Channel channel;
		if (playObj.hasChannel()) {
			
			channel = playObj.getChannel();
			
			logger.log(DEBUG,  "Unpausing sound " + symbol + ", not setting position if channel group is playing (" + channelGroupPlaying + ")");
			fmodErrCheck(channel.setPaused(!channelGroupPlaying));

			return;
		}
		
		channel = playObj.newChannel();
		
		logger.log(DEBUG, "Sound " + symbol + " paused from channelgroup: " + channelGroupPlaying);
		fmodErrCheck(system.playSound(FMOD_CHANNEL_FREE, playObj.sound, !channelGroupPlaying, channel));
		
		setChannelPlayType(id, playObj);
		
		logger.log(DEBUG,  "Setting sound " + symbol + " channel's group");
		fmodErrCheck(channel.setChannelGroup(chGrp.channelGroup));
		
		//channel callback
		fmodErrCheck(channel.setCallback(FMOD_CHANNEL_CALLBACKTYPE_END, new ChannelEndCallback(id, playObj), 0));
		
		//Set volume
		logger.log(DEBUG,  "Setting sound " + symbol + " volume");
		fmodErrCheck(channel.setVolume(playObj.getVolume()));
		
		fmodErrCheck(channel.setPosition(0, 1));
	}
	
	/**
	 * Package method for RandomPlayRunner to invoke which allows it
	 * to reset the random sound play
	 * @param playObj
	 * @param runner
	 */
	void randomSoundReset(PlaybackObject playObj, RandomPlayRunner runner) {
		this.threadPool.submit(runner);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stopSound(int id, String symbol) throws SoundEngineException {
		logger.log(DEV, "Stopping sound " + symbol);
		
		ChannelGroupWrapper chGrp = channelGroups.get(id);
		if (chGrp == null){
			throw new SoundEngineException("Invalid soundscape ID");
		}
		
		PlaybackObject playObj = chGrp.playbackObjects.get(symbol);
		if (playObj == null){
			throw new SoundEngineException("Invalid sound symbol for soundscape ID " + id);
		}

		if (playObj.hasChannel()) {
			logger.log(DEBUG, "Stopping and forgetting valid channel");
			fmodErrCheck(playObj.getChannel().stop());
			playObj.forgetChannel();
		}
		
		if (playObj.getPlayType() == RANDOM) {
			logger.log(DEBUG, "Stopping and forgetting random play runner");
			
			RandomPlayRunner runner = chGrp.activeRandomPlayers.get(symbol);
			if (runner == null) {
				logger.log(DEBUG, "No active random play runner");
				return;
			}
			
			runner.setStopped(true); //should stop thread
			chGrp.activeRandomPlayers.remove(symbol);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pauseSound(int id, String symbol) throws SoundEngineException {
		logger.log(DEV, "Pausing sound " + symbol);
		
		ChannelGroupWrapper chGrp = channelGroups.get(id);
		if (chGrp == null){
			throw new SoundEngineException("Invalid soundscape ID");
		}
		
		PlaybackObject playObj = chGrp.playbackObjects.get(symbol);
		if (playObj == null){
			throw new SoundEngineException("Invalid sound symbol for soundscape ID " + id);
		}
		
		if (playObj.hasChannel()) {
			fmodErrCheck(playObj.getChannel().setPaused(true));
		}
		
		if (playObj.getPlayType() == RANDOM) {
			RandomPlayRunner runner = chGrp.activeRandomPlayers.get(symbol);
			if (runner == null) {
				logger.log(DEBUG, "No active random play runner");
				return;
			}
			
			runner.setPaused(true);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSoundVolume(int id, String symbol, double newVolume) throws SoundEngineException {
		logger.log(DEV, "Setting volume for sound " + symbol);
		
		ChannelGroupWrapper chGrp = channelGroups.get(id);
		if (chGrp == null){
			throw new SoundEngineException("Invalid soundscape ID");
		}
		
		PlaybackObject playObj = chGrp.playbackObjects.get(symbol);
		if (playObj == null){
			throw new SoundEngineException("Invalid sound symbol for soundscape ID " + id);
		}

		playObj.setVolume((float) newVolume);
		if (playObj.hasChannel()) {
			fmodErrCheck(playObj.getChannel().setVolume(playObj.getVolume()));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSoundPlaytype(int id, String symbol, PlayType playType) throws SoundEngineException {
		logger.log(DEV, "Setting sound play mode " + playType + " for sound " + symbol);
		
		ChannelGroupWrapper chGrp = channelGroups.get(id);
		if (chGrp == null){
			throw new SoundEngineException("Invalid soundscape ID");
		}
		
		PlaybackObject playObj = chGrp.playbackObjects.get(symbol);
		if (playObj == null){
			throw new SoundEngineException("Invalid sound symbol for soundscape ID " + id);
		}
		
		PlayType oldPlayType = playObj.getPlayType();
		playObj.setPlayType(playType);
		
		if (oldPlayType != playType && oldPlayType == RANDOM) {
			RandomPlayRunner runner = chGrp.activeRandomPlayers.remove(symbol);
			runner.setStopped(true); //cancel random play
		}
		
		if (playObj.hasChannel()) {
			setChannelPlayType(id, playObj);
		}
	}
	
	/**
	 * Does not perform the same checks as public methods on data validity. Assumes a valid channel group id
	 * and a playback object with an existing channel.
	 * Sets up the channel however the play type requires
	 * @param chGrpId
	 * @param playObj
	 * @throws SoundEngineException
	 */
	private void setChannelPlayType(int chGrpId, PlaybackObject playObj) throws SoundEngineException {
		String symbol = playObj.getSoundName();
		PlayType playType = playObj.getPlayType();
		ChannelGroupWrapper channelGroup = channelGroups.get(chGrpId);
		Channel channel = playObj.getChannel();
		
		logger.log(DEBUG, "Setting play mode from SoundModel.PlayType: " + playType + " for sound " + symbol);
		
		switch (playType) {
			case SINGLE:
				fmodErrCheck(channel.setMode(FMOD_LOOP_OFF));
				
				break;
			case LOOP:
				fmodErrCheck(channel.setMode(FMOD_LOOP_NORMAL));
				fmodErrCheck(channel.setLoopCount(-1));
				break;
			case RANDOM:
				RandomPlaySettings settings = playObj.getRandomSettings();
				
				//range adds one because random.nextInt is [0, range)
				int range = settings.maxRepeats - settings.minRepeats + 1;
				int numberOfRepeats = settings.minRepeats;
				if (range > 0) {
					numberOfRepeats = random.nextInt(range) + settings.minRepeats;
				}
				
				logger.log(DEBUG, "Chosen number of repeats for Random Play for sound " + symbol + ": " + numberOfRepeats);
				
				fmodErrCheck(channel.setMode(FMOD_LOOP_NORMAL));
				fmodErrCheck(channel.setLoopCount(numberOfRepeats));
				
				//check to see if there is a random play thread. If not, create one, but do not submit it
				//This is here in case a sound is set to random while it is playing. We want the sound
				//to be able to carry on with random play after it has stopped like we would expect
				RandomPlayRunner runner = channelGroup.activeRandomPlayers.get(symbol);
				if (runner == null) {
					runner = new RandomPlayRunner(this, chGrpId, playObj);
					channelGroup.activeRandomPlayers.put(symbol, runner);
				}
				
				break;
			default:
				throw new SoundEngineException("Unrecognized play type");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSoundPlaytype(int id, String symbol, PlayType playType, RandomPlaySettings randomSettings) throws SoundEngineException {
		logger.log(DEV, "Setting sound play mode " + playType + " for sound " + symbol);
		
		ChannelGroupWrapper chGrp = channelGroups.get(id);
		if (chGrp == null){
			throw new SoundEngineException("Invalid soundscape ID");
		}
		
		PlaybackObject playObj = chGrp.playbackObjects.get(symbol);
		if (playObj == null){
			throw new SoundEngineException("Invalid sound symbol for soundscape ID " + id);
		}
		
		playObj.setRandomSettings(randomSettings);
		setSoundPlaytype(id, symbol, playType);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSoundscapeVolume(int id, double newVolume) throws SoundEngineException {
		logger.log(DEV, "Setting volume for soundscape " + id);
		
		ChannelGroupWrapper chGrp = channelGroups.get(id);
		if (chGrp == null){
			throw new SoundEngineException("Invalid soundscape ID");
		}
		
		fmodErrCheck(chGrp.channelGroup.setVolume((float) newVolume));
		//cancel fade, if any
		chGrp.setFading(false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void playSoundscape(int id) throws SoundEngineException {
		logger.log(DEV, "Playing soundscape " + id);
		
		ChannelGroupWrapper chGrp = channelGroups.get(id);
		if (chGrp == null){
			throw new SoundEngineException("Invalid soundscape ID");
		}
		
		if (chGrp.isPlaying()) {
			logger.log(DEBUG, "Soundscape " + id + " already playing");
			return;
		}
		
		chGrp.setPlaying(true);

		for (PlaybackObject playObj : chGrp.playbackObjects.values()) {
			/*
			 * Checking to see if playObj has a channel before playing because
			 * only sounds that are loaded in and ready to go will have a channel.
			 * If they don't have a channel, that means that they either are set
			 * to not play or were single play and ended playback. The user
			 * via the GUI must select to either Load the sound (if soundscape is
			 * not playing) or play the sound (if soundscape is already playing)
			 */
			if (playObj.hasChannel()) {
				playSound(id, playObj.getSoundName());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pauseSoundscape(int id) throws SoundEngineException {
		logger.log(DEV, "Pausing soundscape " + id);
		
		ChannelGroupWrapper chGrp = channelGroups.get(id);
		if (chGrp == null){
			throw new SoundEngineException("Invalid soundscape ID");
		}
		
		if (!chGrp.isPlaying()) {
			logger.log(DEBUG, "Soundscape " + id + " is not playing: cannot pause");
			return;
		}
		
		chGrp.setPlaying(false);
		
		for (PlaybackObject playObj : chGrp.playbackObjects.values()) {
			pauseSound(id, playObj.getSoundName());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stopSoundscape(int id) throws SoundEngineException {
		logger.log(DEV, "Stopping soundscape " + id);
		
		ChannelGroupWrapper chGrp = channelGroups.get(id);
		if (chGrp == null){
			throw new SoundEngineException("Invalid soundscape ID");
		}
		
		chGrp.channelGroup.stop();
		chGrp.setPlaying(false);
		chGrp.setFading(false);
		
		for (PlaybackObject playObj : chGrp.playbackObjects.values()) {
			//no need to stop these channels because they've already been
			//stopped by the channel group
			if (playObj.hasChannel()) {
				playObj.forgetChannel();
				//reset channel for sounds that are supposed to play
				//next time the soundscape starts up again.
				playSound(id, playObj.getSoundName());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearSoundscape(int id) throws SoundEngineException {
		logger.log(DEV, "Clearing soundscape " + id);
		
		ChannelGroupWrapper chGrp = channelGroups.get(id);
		if (chGrp == null){
			throw new SoundEngineException("Invalid soundscape ID");
		}
		
		//stop all playing channels
		fmodErrCheck(chGrp.channelGroup.stop());
		fmodErrCheck(chGrp.channelGroup.release());
		
		//forget the wrapper object, which includes all its loaded sounds
		channelGroups.remove(id);
	}

	/**
	 * {@inheritDoc}
	 * <h3>FmodExEngine</h3>
	 * <p>It appears the FmodEx library does not have an abstraction for fading
	 * sounds, and so fade must be implemented on outside of the library</p>
	 */
	@Override
	public void fadeSoundscape(int id, double startVolume, double endVolume, int ms) throws SoundEngineException {
		logger.log(DEV, "Attempting fade of soundscape " + id);
		
		ChannelGroupWrapper chGrp = channelGroups.get(id);
		if (chGrp == null){
			throw new SoundEngineException("Invalid soundscape ID");
		}
		
		chGrp.setFading(true);
		
		FadeRunner fader = new FadeRunner(this, chGrp, id, startVolume, endVolume, ms);
		threadPool.submit(fader);
	}
	
	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void subscribeToFinishedSounds(int id, ISubscriber<String> subscriber) throws SoundEngineException {
		ChannelGroupWrapper chGrp = channelGroups.get(id);
		if (chGrp == null){
			throw new SoundEngineException("Invalid soundscape ID");
		}
		
		chGrp.soundEndSubscribers.add(subscriber);
	}
	
	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void subscribeToFinishedFade(int id, ISubscriber<Boolean> subscriber) throws SoundEngineException {
		ChannelGroupWrapper chGrp = channelGroups.get(id);
		if (chGrp == null){
			throw new SoundEngineException("Invalid soundscape ID");
		}
		
		chGrp.fadeEndSubscribers.add(subscriber);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void shutdown(){
		updateInterrupt = true;
	}
	
	private void internalShutdown() {
		logger.log(DEV, "Shutting down FmodEx Engine: Driver " + driverName);
		fmodErrCheck(system.close());
		fmodErrCheck(system.release());
		this.threadPool.shutdown();
		logger.log(DEV, "FmodEx Engine shutdown complete: Driver " + driverName);
	}

	public static void main(String[] args) throws InterruptedException, SoundEngineException {
		logger.log(DEV, "starting main");
		FmodExEngine soundCard1, soundCard2, soundCard3, soundCard4, soundCard5, soundCard6;
		
		try {
			soundCard1 = new FmodExEngine();
			
			logger.log(DEV, "Initialized Driver 1; Name: " + soundCard1.driverName);
			
			soundCard2 = new FmodExEngine();
			logger.log(DEV, "Initialized Driver 2; Name: " + soundCard2.driverName);
			
			soundCard3 = new FmodExEngine();
			logger.log(DEV, "Initialized Driver 3; Name: " + soundCard3.driverName);
			
			soundCard4 = new FmodExEngine();
			logger.log(DEV, "Initialized Driver 4; Name: " + soundCard4.driverName);
			
			soundCard5 = new FmodExEngine();
			logger.log(DEV, "Initialized Driver 5; Name: " + soundCard5.driverName);
			
			soundCard6 = new FmodExEngine();
			logger.log(DEV, "Initialized Driver 6; Name: " + soundCard6.driverName);
		} catch (SoundEngineException e) {
			logger.log(PROD, "Error Initializing FmodExEngine!");
			logger.log(DEV, e.getMessage());
			return;
		}
		
		SoundscapeModel ss = TestDataProvider.testSoundscape(
				new String[] {".\\sounds\\walla, crowd in hall.mp3",".\\sounds\\ocean waves.mp3", ".\\sounds\\klaxon alarm aoogah.mp3", ".\\sounds\\bowling.wav"});
		
		try {
			soundCard1.loadSoundscape(ss, CONSOLE1);
			soundCard1.subscribeToFinishedFade(ss.runtimeId,
					(Boolean isPlaying) -> logger.log(PROD, "Listener for soundscape fade invoked with " + isPlaying));
		} catch (SoundEngineException seEx) {
			logger.log(DEV, "Error loading soundscape");
			return;
		}
		
		int count = 0;
		while (true) {
			Thread.sleep(500);
			
			if (count == 5) {
				soundCard1.fadeSoundscape(ss.runtimeId, 1.0, 0.0, 5000);
			}
			
			if (count == 20) {
				soundCard1.setSoundVolume(ss.runtimeId, "test1", 0.1);
				soundCard1.fadeSoundscape(ss.runtimeId, 0.0, 1.0, 3000);
			}
			
			if (count == 25) {
				soundCard1.setSoundPlaytype(ss.runtimeId, "test2", LOOP);
			}
			
			if (count == 35) {
				soundCard1.setSoundPlaytype(ss.runtimeId, "test2", RANDOM);
			}
			
			
			count++;
		}
	}
	
	//Callback classes
	
	private class ChannelEndCallback implements FMOD_CHANNEL_CALLBACK {
		private final PlaybackObject playbackObject;
		private final int soundscapeId;
		
		public ChannelEndCallback (int soundscapeId, PlaybackObject playbackObject) {
			this.soundscapeId = soundscapeId;
			this.playbackObject = playbackObject;
		}
		
		@Override
		public FMOD_RESULT FMOD_CHANNEL_CALLBACK (Channel channel, FMOD_CHANNEL_CALLBACKTYPE callbackType, int arg2, int arg3, int arg4) {
			
			if (callbackType == FMOD_CHANNEL_CALLBACKTYPE_END) {
				IntBuffer buffer = BufferUtils.newIntBuffer(256);
				FmodExEngine.fmodErrCheck(channel.getIndex(buffer));
				
				logger.log(DEBUG, "End Callback for channel " + buffer.get());

				playbackObject.forgetChannel();
				
				String name = playbackObject.getSoundName();
				
				RandomPlayRunner randomRunner =
						channelGroups.get(soundscapeId).activeRandomPlayers.get(playbackObject.getSoundName());
				
				//do not publish Stopped Sound if random. We want the gui to still register the sound as
				//"playing" because the randomRunner is still responsible for its sound output.
				//When the user "Stops" the sound with the GUI, then the user is again responsible for
				//its playback
				if (randomRunner != null) {
					logger.log(DEBUG, "Launching RandomPlayRunner again");
					randomSoundReset(playbackObject, randomRunner);
				} else {
					logger.log(DEBUG, "Publishing that the sound is finished");
					publishFinishedSound(soundscapeId, name);
				}
				
			}
			
			return FMOD_OK;
		};
	}
}
