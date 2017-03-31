/*
 * FmodExEngine.java
 *
 * Created on March 4, 2006, 11:07 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package ase_source_bak;

import static java.lang.System.exit;
import static java.lang.System.out;
import static org.jouvieje.FmodEx.Defines.FMOD_INITFLAGS.FMOD_INIT_NORMAL;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_HARDWARE;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_LOOP_NORMAL;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_LOOP_OFF;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_SOFTWARE;
import static org.jouvieje.FmodEx.Defines.VERSIONS.FMOD_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_JAR_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_LIBRARY_VERSION;
import static org.jouvieje.FmodEx.Enumerations.FMOD_CHANNELINDEX.FMOD_CHANNEL_FREE;
import static org.jouvieje.FmodEx.Enumerations.FMOD_CHANNELINDEX.FMOD_CHANNEL_REUSE;

import java.io.File;
import java.nio.ByteBuffer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.jouvieje.FmodEx.Channel;
import org.jouvieje.FmodEx.ChannelGroup;
import org.jouvieje.FmodEx.FmodEx;
import org.jouvieje.FmodEx.Init;
import org.jouvieje.FmodEx.Sound;
import org.jouvieje.FmodEx.System;
import org.jouvieje.FmodEx.Defines.INIT_MODES;
import org.jouvieje.FmodEx.Enumerations.FMOD_RESULT;
import org.jouvieje.FmodEx.Exceptions.InitException;
import org.jouvieje.FmodEx.Misc.BufferUtils;

import ase_source_bak.Soundcard.Console;
import ase_source_bak.Soundcard.PlaybackObject;
import ase_source_bak.Soundcard.RandomPlayThread;
import ase_source_bak.Soundcard.SoundData;

/**
 * 2017 Code Reorganization:
 * Moved into its own package.
 * As part of reorganizing the ASE, the Soundcard class was extracted from this
 * .java file and pulled into its own instead of existing as a nested class
 * within this file.
 * 
 * TODO: Clean up unused imports
 * 
 * @author Lance
 */

/*******************************************************************************
 * FmodExEngine <br>
 * This class is responsible for all sound creation in the application. 
 * Creating an instance of this class initializes one or two sound cards 
 * for playback and creates data structures to hold sound files in memory.<br>
 * 
 * Javadoc for FMODEx found at http://jerome.jouvie.free.fr/nativefmodex/javadoc/index.html
 * Documentation for FMOD Ex (C, C++, C#, Javascript) http://www.fmod.org/documentation/#content/generated/lowlevel_api.html
 * 		NOTE: above FMOD Ex docs likely point to the most recent version, which is not
 * 				necessarily what the Java NativeFmodeEx.jar implements an interface to
 ******************************************************************************/
public class FmodExEngine {
	static final int STAGEBUFFERSIZE = 500; // number of sounds that can be in
											// RAM
	// at once for sound card - stage out
	static final int PREVIEWBUFFERSIZE = 250; // number of sounds that can be
												// in RAM
	// at once for sound card- preview out
	static boolean previewSoundCardInstalled = false; // system flag

	public Soundcard stage; // object for sound card - stage out
	public Soundcard preview; // object for sound card - preview out

	/**
	 * this constructor is called when only one sound card is installed in
	 * system
	 */
	public FmodExEngine(int cardOne) {
		initializeEngine();
		stage = new Soundcard(cardOne, STAGEBUFFERSIZE);
		preview = null;
		previewSoundCardInstalled = false;
	}

	/** 
	 * this constructor is called when two sound cards are installed in system, never tested 
	 * */
	public FmodExEngine(int cardOne, int cardTwo) {
		System system = new System();
		ByteBuffer buffer = BufferUtils.newByteBuffer(256);
		int numDrivers;
		initializeEngine();
		FmodEx.System_Create(system);
		system.getNumDrivers(buffer.asIntBuffer());
		numDrivers = buffer.getInt(0);
		stage = new Soundcard(cardOne, STAGEBUFFERSIZE);
		if (numDrivers > 1) {
			preview = new Soundcard(cardTwo, PREVIEWBUFFERSIZE);
			previewSoundCardInstalled = true;
		} else {
			preview = null;
			previewSoundCardInstalled = false;
		}
	}

	/** 
	 * initializes sound engine 
	 */
	private static void initializeEngine() {

		try {
			Init.loadLibraries(INIT_MODES.INIT_FMOD_EX);
		} catch (InitException e) {
			out.printf("NativeFmodEx error! %s\n", e.getMessage());
			exit(1);
		}

		/*
		 * Checking NativeFmodEx version
		 */
		if (NATIVEFMODEX_LIBRARY_VERSION != NATIVEFMODEX_JAR_VERSION) {
			out
					.printf(
							"Error!  NativeFmodEx library version (%08x) is different to jar version (%08x)\n",
							NATIVEFMODEX_LIBRARY_VERSION,
							NATIVEFMODEX_JAR_VERSION);
			exit(0);
		}
	}
	
	/**
	 * Getter for primary soundcard - stage
	 * @param 
	 */
	public Soundcard getStage(){
		return this.stage;
	}
	
	/**
	 * Getter for preview soundcard - preview
	 * @param 
	 */
	public Soundcard getPreview(){
		return this.preview;
	}

	/* Checks calls to NativeFmodEx for errors */
	static void ERRCHECK(FMOD_RESULT result) {
		if (result != FMOD_RESULT.FMOD_OK) {
			out.printf("FMOD error! (%d) %s\n", result.asInt(), FmodEx
					.FMOD_ErrorString(result));
			// exit(1);
		}
	}

	public void Shutdown() {
		FMOD_RESULT result;
		out.println("*** Shutting down FmodEx Engine ***");
		/***********************************************************************
		 * Causes error if bank isn't used before shutdown - disabled for(int
		 * count=0;count<STAGEBUFFERSIZE;count++){
		 * result=stage.soundFile[count].sound.release(); ERRCHECK(result); }
		 */
		result = stage.system.close();
		ERRCHECK(result);
		result = stage.system.release();
		ERRCHECK(result);

		if (previewSoundCardInstalled) {
			/*******************************************************************
			 * Causes error if bank isn't used before shutdown - disabled
			 * for(int count=0;count<PREVIEWBUFFERSIZE;count++){
			 * 
			 * result=preview.soundFile[count].sound.release();
			 * ERRCHECK(result); }
			 */

			result = preview.system.close();
			ERRCHECK(result);
			result = preview.system.release();
			ERRCHECK(result);

		}
		out.println("*** FmodEx Engine Shutdown Complete ***");

	}
}

/**
 * Soundcard class <br>
 * This class is the heart of the sound engine. Sound files are loaded into a
 * central bank in this class. A basic memory manager swap sound files out of
 * memory as needed.
 * 
 */
class Soundcard {
	static final int LOOPPLAY = 0;
	static final int PLAYONCE = 1;
	static final int RANDOMPLAY = 2;

	static final int STREAMTHRESHOLD = 250000;

	private static final int LOADLIMIT = 25;
	public float cacheHit = 0, cacheMiss = 0; // tracks sound bank cache hits
	public float cachePercentage = 0; // tracks sound bank cache misses
	int bankSize = 0; // holds number of sounds in bank

	public System system = new System(); // System object - one required for
											// each sound card
	int BANKCAPACITY; // capacity of sound bank - defined in instansiating
						// object
	SoundData[] soundFile; // bank of sound files
	Console consoleOne; // holds info for primary soundscape panel
	Console consoleTwo; // holds info for secondary soundscape panel
	Console phantom; // this console used for quick previews & tests - not
						// seen = phantom!

	int soundcardID; // system driver id for sound card
	static FMOD_RESULT result; // used to hold results of NativeFmod calls
	ByteBuffer buffer;

	/* constructor */
	public Soundcard(int driverID, int bufferLimit) {
		BANKCAPACITY = bufferLimit;
		soundFile = new SoundData[BANKCAPACITY];
		for (int count = 0; count < BANKCAPACITY; count++) { // initialize
																// sound bank
			soundFile[count] = new SoundData();
		}

		// set up this soundcard with NativeFmodEx
		soundcardID = driverID;
		int version;
		buffer = BufferUtils.newByteBuffer(256);

		result = FmodEx.System_Create(system);
		FmodExEngine.ERRCHECK(result);

		result = system.setDriver(soundcardID);
		FmodExEngine.ERRCHECK(result);

		result = system.getVersion(buffer.asIntBuffer());
		FmodExEngine.ERRCHECK(result);
		version = buffer.getInt(0);

		if (version < FMOD_VERSION) {
			out
					.printf(
							"Error!  You are using an old version of FMOD %08x.  This program requires %08x\n",
							version, FMOD_VERSION);
			exit(0);
		}
		result = system.init(32, FMOD_INIT_NORMAL, null);
		FmodExEngine.ERRCHECK(result);

		consoleOne = new Console(); // create primary console instance
		consoleTwo = new Console(); // create secondary console instance
		phantom = new Console(); // create phantom console for quick tests;
	}

	/***************************************************************************
	 * This method maintains the sound bank. When a given sound is requested,
	 * the bank is searched. If the sound is already in * the Sound object is
	 * returned. If it is not found, it is * either added to the bank, or the
	 * least-used sound not * currently being used by any process is swapped out
	 */
	public Sound getSoundFromBank(SoundObject source) {

		String path;
		int location = bankSize;
		int maxIdle;
		int count;

		for (count = 0; count < bankSize; count++) { // scan bank for request
														// sound

			if (soundFile[count].soundID == source.getSoundID()) { // if found
																	// in cache

				// file was found in cache
				soundFile[count].cyclesIdle = 0; // sound no longer idle
													// entry - reset
				cacheHit++; // record cache as hit for performance counters
				cachePercentage = cacheHit / (cacheHit + cacheMiss) * 100;
				return soundFile[count].sound;
			}
		}
		if (bankSize == BANKCAPACITY) { // is bank full? -- if so ...
			maxIdle = 0; // this stores the value of the most idle sound
			location = 0; // this stores the location of the most idle sound

			for (count = 0; count < BANKCAPACITY; count++) { // cycle through
																// all sounds in
																// memory

				if (soundFile[count].playbackLocks == 0) { // if sound is not
															// in use by any
															// process
					if (soundFile[count].cyclesIdle > maxIdle) { // if its
																	// idle time
																	// is higher
																	// than the
																	// highest
																	// so far
						location = count; // mark location of sound in array
						maxIdle = soundFile[count].cyclesIdle; // update
																// maxIdle
																// amount to new
																// cycles idle
																// value
					}
					soundFile[count].cyclesIdle++; // increase idle count of
													// each non-playing sound
				}
			} // we have identified the oldest item in the cache - free it and
				// reduce bank size to reflect it
			bankSize--;
			soundFile[location].sound.release();
		}

		/* catch miss occurred - add sound to bank */

		path = source.getSoundPath();
		soundFile[location].soundID = source.getSoundID();
		soundFile[location].cyclesIdle = 0;
		bankSize++;

		if (source.getFileSizeInBytes() < STREAMTHRESHOLD) {
			/* load sound into RAM */
			if (Arg.software_initialization == true) {
				result = system.createSound(path, FMOD_SOFTWARE, null,
						soundFile[location].sound);
			} else {
				result = system.createSound(path, FMOD_HARDWARE, null,
						soundFile[location].sound);
			}
			FmodExEngine.ERRCHECK(result);
		} else {
			if (Arg.software_initialization == true) {
				result = system.createStream(path, FMOD_SOFTWARE, null,
						soundFile[location].sound);
			} else {
				result = system.createStream(path, FMOD_HARDWARE, null,
						soundFile[location].sound);
			}
			FmodExEngine.ERRCHECK(result);
		}

		/* update performance counters */
		cacheMiss++;
		cachePercentage = cacheHit / (cacheHit + cacheMiss) * 100;

		return soundFile[location].sound;

	}

	/***************************************************************************
	 * This method adds locks to sounds. Locks prevent a sound from being
	 * accidentally swapped from the sound bank when other * processes may be in
	 * need of it.
	 */
	public boolean addLock(int ID) {
		for (int count = 0; count < bankSize; count++) { // scan bank for
															// given sound
			if (ID == soundFile[count].soundID) { // if found
				soundFile[count].playbackLocks++; // add lock to sound
				return true; // success
			}
		}
		return false; // sound not in bank - failed to add lock
	}

	/***************************************************************************
	 * This method removes locks from sounds. Locks prevent a sound from being
	 * accidentally swapped from the sound bank when other processes * maybe in
	 * need of it.
	 */
	public boolean releaseLock(int ID) {
		SoundData current;
		for (int count = 0; count < bankSize; count++) { // scan through bank
			current = soundFile[count];
			if (ID == current.soundID) { // if sound is found
				if (current.playbackLocks == 0) // and no process is using it
					return false; // no locks to release
				else {
					current.playbackLocks--; // release a lock
					return true; // success
				}
			}
		}
		return false; // sound not found - failure
	}

	public int getLoadLimit() {
		return LOADLIMIT;
	}

	/**
	 * Console Class <br>
	 * The role of the Console class is to hold information about the
	 * soundscapes that are loaded for immediate use. This class controls
	 * playback options and make changes to sound such as volume and effects.
	 * This class is a nested class of Soundcard
	 */
	class Console {

		private int[] soundsInPlay = new int[LOADLIMIT];
		private PlaybackObject[] playbackobject = new PlaybackObject[LOADLIMIT];
		private ChannelGroup consoleMaster = new ChannelGroup();
		private boolean isPlaying = false;
//		private int soundscapeID;

		private int playbackObjectsActive = 0; // start with no channels active
												// in console

//		private ByteBuffer buffer = BufferUtils.newByteBuffer(256);

		// constructor
		public Console() {

			result = system.createChannelGroup("Console Master Channel",
					consoleMaster);
			FmodExEngine.ERRCHECK(result);
		}

		public void preload(Soundscape source) {
			int size;

			size = source.getSoundscapeSoundsCount();
			stopPlayback();
			playbackObjectsActive = 0; // old panel is void
//			soundscapeID = source.getSoundscapeID();
			for (int position = 0; position < size; position++) {
				SoundObject sound = source.getSound(position);
				preload(sound);	
			}
		}

		public void preload(SoundObject source) {
			int playbackType;
			File f = new File(source.getSoundPath());
			Sound newSound = new Sound();

//			PlaybackObject playback;
			if(f.isFile()){
				if (source.getFileSizeInBytes() > STREAMTHRESHOLD) {
					result = system.createStream(source.getSoundPath(), FMOD_SOFTWARE, null, newSound);
				} else {
					newSound = getSoundFromBank(source);
				}
	
				playbackType = source.getPlaybackType();
				playbackobject[playbackObjectsActive] = new PlaybackObject(
						playbackObjectsActive, source, newSound, consoleMaster, playbackType);
				soundsInPlay[playbackObjectsActive] = source.getSoundID();
			} else {
				//Invalid file, add some nulls for placeholder and skip to the next position
				source.setInvalid(true);
				playbackobject[playbackObjectsActive] = null;
				soundsInPlay[playbackObjectsActive] = source.getSoundID();
			}
			playbackObjectsActive++;
		}

		public void startPlayback(int playbackID) {
			out.println("playbackID=" + playbackID + " playbackObjectsActive="
					+ playbackObjectsActive);
			playbackobject[playbackID].play();
			if (playbackobject[playbackID].playbackMode != 2)
				addLock(soundsInPlay[playbackID]);

		}

		public void startPlayback() {
			out.println("Playback chosen");
			if (isPlaying == false) {
				isPlaying = true;
				consoleMaster.overrideMute(true);
				for (int count = 0; count < playbackObjectsActive; count++) {
					if (playbackobject[count] != null && 
					playbackobject[count].sourceSoundObject.getPlaybackEnabled() == true)
						startPlayback(count);
				}
				consoleMaster.overrideMute(false);
			}
		}
		
		public PlaybackObject getPlaybackObject(int id) {
			return playbackobject[id];
		}

		public void stopPlayback(int playbackID) {
			if(playbackobject[playbackID] != null){
				playbackobject[playbackID].stop();
			}
			releaseLock(soundsInPlay[playbackID]);
		}

		public void stopPlayback() {
			if (isPlaying == true) {
				isPlaying = false;
				consoleMaster.overrideMute(true);
				for (int count = 0; count < playbackObjectsActive; count++)
					stopPlayback(count);

				consoleMaster.overrideMute(false); // was consoleMaster.stop();
			}
		}

		public void deletePlaybackObject(int playbackID) {
			playbackobject[playbackID].stop();
			if (playbackObjectsActive > 1) {
				playbackobject[playbackID] = playbackobject[playbackObjectsActive - 1];
				playbackObjectsActive--;
			} else
				playbackObjectsActive = 0;
		}

		public boolean isPlaying() {
			return isPlaying;
		}

		public boolean isPlaying(int row) {
			return playbackobject[row].isActive();
		}

		public void changeVolume(int playbackID, int volume) {
			playbackobject[playbackID].changeVolume(volume / 100.0f);
		}

		public void changeVolume(int volume) {
			consoleMaster.setVolume(volume / 100.0f);
		}

		public void changePlaybackMode(int playbackID, int mode) {
			playbackobject[playbackID].changeMode(mode);
		}

		public boolean testSoundFile(String testFile) {
			boolean status = false;
//			Channel channel = new Channel();
			Sound sound = new Sound();
			result = system.createSound(testFile, FMOD_HARDWARE, null, sound);

			String message = "";
			if (result != FMOD_RESULT.FMOD_OK) {
				message = "Sound file is defective. Not installing at this time.";
				status = false;
			} else {
				message = "Sound file is good. Saving file to system now.";
				status = true;
			}
			JFrame frame = new JFrame();
			JOptionPane.showMessageDialog(frame, message);
			return status;
		}

		public Channel previewSoundFile(String testFile) {
			Channel channel = new Channel();
			Sound sound = new Sound();
			result = system.createSound(testFile, FMOD_HARDWARE, null, sound);

//			String message = "";
			if (result == FMOD_RESULT.FMOD_OK) {
				system.playSound(FMOD_CHANNEL_FREE, sound, false, channel);
			}
			return channel;
		}

		public Channel previewSoundFile(String testFile, Channel channel) {
			Sound sound = new Sound();
			result = system.createSound(testFile, FMOD_HARDWARE, null, sound);

//			String message = "";
			if (result == FMOD_RESULT.FMOD_OK) {
				system.playSound(FMOD_CHANNEL_REUSE, sound, false, channel);
			}
			return channel;
		}

		public void stopSounds(Channel channel) {
			channel.stop();
		}

		public void purgeConsole() {
			stopPlayback();
			for (int count = 0; count < playbackObjectsActive; count++) {
				deletePlaybackObject(0);
			}
		}

	}

	/**
	 * This really needs to be cleaned up.
	 * <p>This thread sits around for sounds with random playback enabled,
	 * it will play the sound once and then pause it, it then sleeps for a 
	 * random interval determined by the sound's settings, then it repeats.
	 */
	class RandomPlayThread extends Thread {
		static final int WIPELIMIT = 15;
//		private int wipeID = -1;
//		private int minRepeatCycle = 99999;
//		private int repeatCycleWidth = 99999;
//		private int frequency = 99999;
//		private float volumeVarianceFactor;
		public PlaybackObject playbackSource;
		private boolean enabled;
		private Channel channel;

		public RandomPlayThread(PlaybackObject incomingSource) {
			this.playbackSource = incomingSource;
			this.channel = playbackSource.channel;
		}

		public void run() {
			enabled = true;
			while (true) {
				try {
					int sleepTime = this.playbackSource.sourceSoundObject
							.getRepeatTime() * 1000;
					Thread.sleep(sleepTime);
					if (enabled) {
						playbackSource.channel.setMode(FMOD_LOOP_NORMAL);
						int loopCount = this.playbackSource.sourceSoundObject
								.getLoopCount();
						playbackSource.channel.setPaused(false);
						Thread.sleep(this.playbackSource.soundLength
								* loopCount - 50);
						playbackSource.channel.setPaused(true);
						playbackSource.channel.setPosition(0, 1);
					}
				} catch (InterruptedException exception) {
				}
			}
		}

		public void stopWipe() {
			enabled = false;
			channel.setPaused(true);
		}

		public boolean getStatus() {
			return enabled;
		}
	}

	/**
	 * A sound to be played by the system
	 * @author CKidwell
	 *
	 */
	class PlaybackObject {

		int playbackID;
		Channel channel = new Channel();
		ChannelGroup channelGroup = new ChannelGroup();
		public RandomPlayThread randomPlayThread;

		float volumeVarianceFactor = 0; //unused as of 10/19/16
		public SoundObject sourceSoundObject;
		public Sound sound;
		public int soundLength = -1;

		boolean isPlaying = false;

		int playbackMode;
		float volume;

		// commented out because I may not need to use these anymore
		/*
		 * FMOD_CHANNEL_CALLBACK endCallback = new FMOD_CHANNEL_CALLBACK(){
		 * public FMOD_RESULT FMOD_CHANNEL_CALLBACK(Channel tempChannel,
		 * FMOD_CHANNEL_CALLBACKTYPE type, int data1, int data2, int data3){
		 * Channel tempChannel2 = new Channel(); Sound tempSound = new Sound();
		 * result = tempChannel.getCurrentSound(tempSound);
		 * FmodExEngine.ERRCHECK(result); result =
		 * system.playSound(FMOD_CHANNEL_FREE, tempSound, true, tempChannel2);
		 * FmodExEngine.ERRCHECK(result); result =
		 * tempChannel2.setLoopCount(sourceSoundObject.getLoopCount());
		 * FmodExEngine.ERRCHECK(result); //result =
		 * tempChannel.getChannelGroup(channelGroup);
		 * //FmodExEngine.ERRCHECK(result); //result =
		 * tempChannel2.setChannelGroup(channelGroup);
		 * //FmodExEngine.ERRCHECK(result); result =
		 * channel.setCallback(FMOD_CHANNEL_CALLBACKTYPE_END, endCallback, 0);
		 * FmodExEngine.ERRCHECK(result); randomPlayThread = new
		 * RandomPlayThread(channel, sourceSoundObject); return
		 * FMOD_RESULT.FMOD_OK; } };
		 * 
		 * /*todo: figure out a way to remove a callback from a channel without
		 * doing this.
		 * 
		 * 
		 * FMOD_CHANNEL_CALLBACK nullCallback = new FMOD_CHANNEL_CALLBACK(){
		 * public FMOD_RESULT FMOD_CHANNEL_CALLBACK(Channel tempChannel,
		 * FMOD_CHANNEL_CALLBACKTYPE type, int data1, int data2, int data3){
		 * return FMOD_RESULT.FMOD_OK; } };
		 */

		public PlaybackObject(int ID, SoundObject source, Sound newSound,
				ChannelGroup channelgroup, int mode) {
			sourceSoundObject = source;
			playbackMode = mode;
			sound = newSound;
			volume = source.getVolume() / 100.0f;
			this.channelGroup = channelgroup;
			ByteBuffer buffer = BufferUtils.newByteBuffer(256);

			result = system.playSound(FMOD_CHANNEL_FREE, sound, true, channel);
			FmodExEngine.ERRCHECK(result);

			sound.getLength(buffer.asIntBuffer(), 1);
			// 1 is the code for FMOD to return the sound length in miliseconds,
			// see the fmod documentation
			this.soundLength = buffer.getInt(0);

			if (playbackMode == LOOPPLAY) {
				result = channel.setMode(FMOD_LOOP_NORMAL);
				FmodExEngine.ERRCHECK(result);
			}

			if (playbackMode == RANDOMPLAY) {
				result = channel.setMode(FMOD_LOOP_NORMAL);
				FmodExEngine.ERRCHECK(result);

				randomPlayThread = new RandomPlayThread(this);

			}

			result = channel.setVolume(volume);
			FmodExEngine.ERRCHECK(result);

			result = channel.setChannelGroup(channelgroup);
			FmodExEngine.ERRCHECK(result);
			/*
			 * autowipe=new AutoWipe(source.getMinRepeatDelayMs(),
			 * source.getMaxRepeatDelayMs(), source.getVolumeVariance(), sound,
			 * channelgroup);
			 */
		}

		public void setCallback(int trigger) {
			if (trigger == 0) {
				// channel.setCallback(FMOD_CHANNEL_CALLBACKTYPE_END,
				// nullCallback, 0);
			} else if (trigger == 1) {

			} else if (trigger == 2) {

			}
		}

		/**
		 * Plays the object in it's chosen method
		 */
		public void play() {
			if (isPlaying == false) {
				isPlaying = true;
				out.println("Playback mode=" + playbackMode);
				switch (playbackMode) {
				case LOOPPLAY:
					channel.setPaused(false);
					break;
				case PLAYONCE:
					result = system.playSound(FMOD_CHANNEL_FREE, sound, true,
							channel);
					channel.setVolume(volume);
					channel.setChannelGroup(this.channelGroup);
					FmodExEngine.ERRCHECK(result);
					channel.setPaused(false);
					break;
				case RANDOMPLAY:
					randomPlayThread = new RandomPlayThread(this);
					randomPlayThread.start();
					FmodExEngine.ERRCHECK(result);
					break;
				default:
					out.println("Error 30!");
					break;
				}
			}
		}

		public void stop() {
			if (isPlaying == true) {
				isPlaying = false;

				switch (playbackMode) {
				case LOOPPLAY:
					channel.setPaused(true);
					result = channel.setPosition(0, 1);
					FmodExEngine.ERRCHECK(result);
					break;

				case PLAYONCE:
					result = channel.setPaused(true); // may have to change
														// this
					FmodExEngine.ERRCHECK(result);
					result = channel.setPosition(0, 1);
					FmodExEngine.ERRCHECK(result);
					break;

				case RANDOMPLAY:
					// randomPlayThread.stop();
					randomPlayThread.stopWipe();
					channel.setPaused(true);
					break;

				default:
					out.println("Error 10!");
					break;
				}
			}
		}

		public void changeMode(int mode) {
			playbackMode = mode;
			if (playbackMode == LOOPPLAY) {
				result = channel.setMode(FMOD_LOOP_NORMAL);
				FmodExEngine.ERRCHECK(result);
				if (result != FMOD_RESULT.FMOD_OK) {
					result = system.playSound(FMOD_CHANNEL_FREE, sound, true,
							channel);
					channel.setVolume(volume);
					channel.setChannelGroup(this.channelGroup);
				}
				randomPlayThread.stopWipe();
			} else if (playbackMode == PLAYONCE) {
				result = channel.setMode(FMOD_LOOP_OFF);
				if (isPlaying) {
					channel.setPaused(true);
					channel.setPaused(false);
				}
				FmodExEngine.ERRCHECK(result);
			} else {
				result = channel.setMode(FMOD_LOOP_NORMAL);
				FmodExEngine.ERRCHECK(result);
				if (result != FMOD_RESULT.FMOD_OK) {
					result = system.playSound(FMOD_CHANNEL_FREE, sound, true,
							channel);
					channel.setVolume(volume);
					channel.setChannelGroup(this.channelGroup);
				}
				randomPlayThread = new RandomPlayThread(this);
			}
		}

		public void changeVolume(float volume) {
			switch (playbackMode) {
			case LOOPPLAY:
			case PLAYONCE: 
				channel.setVolume(volume);
				break;
			case RANDOMPLAY:
				channel.setVolume(volume);
				break;
			default:
				out.println("Error 20!");
				break;
			}
		}

		public boolean isActive() {

			FMOD_RESULT result = null;
			ByteBuffer buffer = BufferUtils.newByteBuffer(256);
			switch (playbackMode) {
			case LOOPPLAY:
				return isPlaying;
			case PLAYONCE:
				result = channel.isPlaying(buffer);
				break;
			case RANDOMPLAY:
				return randomPlayThread.getStatus();

			default:
				out.println("Error 667");
			}

			FmodExEngine.ERRCHECK(result);

			int bufferContents = (int) buffer.get();

			isPlaying = (bufferContents != 1) ? true : false;

			return isPlaying;
		}

	}

	class SoundData {
		int playbackLocks = 0;
		Sound sound = new Sound();
		int soundID = -1;
		int cyclesIdle = 0;
	}

}