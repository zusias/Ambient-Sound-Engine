package ase.fmodex_sound_engine;

//Java imports
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;
import java.io.IOException;
import java.util.TreeMap;
import java.util.Map;

import static java.lang.System.exit;

//ASE imports
import ase.bridge.SoundEngine;
import ase.bridge.SoundEngineException;
import ase.operations.SoundModel;
import ase.operations.SoundModel.PlayType;
import ase.operations.SoundscapeModel;
import ase.operations.OperationsManager.Sections;
import static ase.operations.OperationsManager.Sections.*;

//ASE Logger
import static ase.operations.OperationsManager.opsMgr;
import static ase.operations.Log.LogLevel.DEBUG;
import static ase.operations.Log.LogLevel.DEV;
import static ase.operations.Log.LogLevel.PROD;

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
	private final static int SOUND_BANK_CAPACITY = 1000;
	private final static int STREAM_THRESHOLD_BYTES = 500000;
	
	//private variables
	private final System system;
	
	//TODO:
	//need a set of ChannelGroups. Each channelGroup will have a collection of
	//channels, each representing a Sound.
	//The "ChannelGroup"s correspond to Soundscapes, and the "Channel"s correspond
	//to individual sounds
	private Map<Integer, ChannelGroupWrapper> channelGroups = new TreeMap<>();
	
	//public constants
	public final int driverId;
	public final String driverName;
	
	//initial load of FmodEx library
	static {
		try {
			opsMgr.logger.log(DEV, "Loading FmodEx Library");
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
		system = new System();
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
		
		driverCount++;
	}
	
	static void fmodErrCheck(FMOD_RESULT result) {
		if (result != FMOD_RESULT.FMOD_OK) {
			opsMgr.logger.log(PROD, "Error with Sound Engine");
			opsMgr.logger.log(DEV, "FMOD Error! "
									+ result.asInt()
									+ " "
									+ FmodEx.FMOD_ErrorString(result));
			
			exit(-1);
		}
	}
	
	private void shutdown(){
		opsMgr.logger.log(DEV, "Shutting down FmodEx Engine");
		fmodErrCheck(system.close());
		fmodErrCheck(system.release());
		opsMgr.logger.log(DEV, "FmodEx Engine shutdown complete");
	}
	
	/**
	 * {@inheritDoc}
	 * <br><b>NOTE 7/8/17:</b> This is a prototype implementation. Does not fulfull abstract spec,
	 * and invokes currently incomplete loadSound method
	 */
	@Override
	public String[] loadSoundscape(SoundscapeModel ssModel, Sections section) throws SoundEngineException {
		opsMgr.logger.log(DEV, "Loading Soundscape " + ssModel.runtimeId + " " + ssModel.name);
		
		//Allocate and initialize new channel group
		ChannelGroupWrapper chGrp = new ChannelGroupWrapper(section);
		channelGroups.put(ssModel.runtimeId, chGrp);
		fmodErrCheck(system.createChannelGroup(ssModel.name, chGrp.channelGroup));
		
		String[] soundNames = new String[ssModel.getTotalSounds()];
		int count = 0;
		
		for (SoundModel sModel : ssModel){
			soundNames[count++] = loadSound(ssModel.runtimeId, sModel);
		}
		
		opsMgr.logger.log(DEBUG, "Setting master volume for channel group");
		chGrp.channelGroup.setVolume((float)ssModel.masterVolume);

		opsMgr.logger.log(DEBUG, "Soundscape fully loaded");
		return soundNames;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>TODO: <b>7/11/17</b> You've run into the issue when trying
	 * to add a callback to a newly constructed channel:<br/>
	 * The callback needs to inform the OperationsManager that the sound has
	 * stopped playing, but currently it has no way of updating the right
	 * sound.
	 * <br/>You're looking at guava's bimap so that the SoundEngineManager
	 * can track symbols vs. indices. You should also consider changing the
	 * OperationsManager interface to allow for a more stable way of accessing
	 * sounds other than index.<br/>
	 * You will also need to update ChannelGroupWrapper to accomodate things</p> 
	 */
	@Override
	public String loadSound(int id, SoundModel sModel) throws SoundEngineException {
		opsMgr.logger.log(DEV, "Loading sound " + sModel.name);
		
		Channel soundChannel = new Channel();
		Sound newSound = new Sound();
		
		
		if (sModel.sizeInBytes >= STREAM_THRESHOLD_BYTES) {
			opsMgr.logger.log(DEBUG, "Creating sound stream");
			fmodErrCheck(system.createStream(sModel.filePath.toString(), FMOD_SOFTWARE, null, newSound));
		} else {
			opsMgr.logger.log(DEBUG, "Creating sound without streaming");
			fmodErrCheck(system.createSound(sModel.filePath.toString(), FMOD_SOFTWARE,  null,  newSound));
		}
		
		opsMgr.logger.log(DEBUG, "Setting system to play sound");
		fmodErrCheck(system.playSound(FMOD_CHANNEL_FREE, newSound, !sModel.isPlaying, soundChannel));
		
		//channel callback
		FMOD_CHANNEL_CALLBACK endCallback = new ChannelEndCallback();
		fmodErrCheck(soundChannel.setCallback(FMOD_CHANNEL_CALLBACKTYPE_END, endCallback, 1));
		
		opsMgr.logger.log(DEBUG,  "Setting sound volume");
		fmodErrCheck(soundChannel.setVolume((float)sModel.volume));
		
		ChannelGroupWrapper chGrp = this.channelGroups.get(id);
		
		opsMgr.logger.log(DEBUG,  "Setting sound channel's group");
		fmodErrCheck(soundChannel.setChannelGroup(chGrp.channelGroup));
		
		//add channel to map of channels, ensuring
		//a unique name within this group if already taken
		String soundName = sModel.name;
		
		int copies = 0;
		while (chGrp.channels.get(soundName) != null) {
			copies++;
		}
		if (copies > 0){
			soundName += copies;
		}
		
		chGrp.channels.put(soundName, soundChannel);
		
		return sModel.name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearSound(int id, String symbol) throws SoundEngineException {
		opsMgr.logger.log(DEV, "Clearing sound " + symbol);
		
		ChannelGroupWrapper chGrp = channelGroups.get(id);
		
		Channel channel = chGrp.channels.get(symbol);
		
		//stop the sound
		//TODO: Ensure no random play threads can start up again
		channel.stop();
		chGrp.channels.remove(symbol);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void playSound(int id, String symbol) throws SoundEngineException {
		opsMgr.logger.log(DEV, "Playing sound " + symbol);
		
		ChannelGroupWrapper chGrp = channelGroups.get(id);
		Channel channel = chGrp.channels.get(symbol);
		
		ByteBuffer buffer = BufferUtils.newByteBuffer(256);
		fmodErrCheck(channel.setPosition(0, 1));
		fmodErrCheck(channel.setPaused(false));
		fmodErrCheck(channel.getPaused(buffer));
		opsMgr.logger.log(DEBUG, "Is paused: " + buffer.getInt(0));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stopSound(int id, String symbol) throws SoundEngineException {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pauseSound(int id, String symbol) throws SoundEngineException {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSoundVolume(int id, String symbol, double newVolume) throws SoundEngineException {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSoundPlaytype(int id, String symbol, PlayType playType) throws SoundEngineException {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSoundscapeVolume(int id, double newVolume) throws SoundEngineException {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void playSoundscape(int id) throws SoundEngineException {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pauseSoundscape(int id) throws SoundEngineException {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stopSoundscape(int id) throws SoundEngineException {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearSoundscape(int id) throws SoundEngineException {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fadeSoundscape(int id, double startVolume, double endVolume, int ms) throws SoundEngineException {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) throws InterruptedException, SoundEngineException{
		opsMgr.logger.log(DEV, "starting main");
		FmodExEngine soundCard1, soundCard2, soundCard3, soundCard4, soundCard5, soundCard6;
		
		try {
			soundCard1 = new FmodExEngine();
			
			opsMgr.logger.log(DEV, "Initialized Driver 1; Name: " + soundCard1.driverName);
			
			soundCard2 = new FmodExEngine();
			opsMgr.logger.log(DEV, "Initialized Driver 2; Name: " + soundCard2.driverName);
			
			soundCard3 = new FmodExEngine();
			opsMgr.logger.log(DEV, "Initialized Driver 3; Name: " + soundCard3.driverName);
			
			soundCard4 = new FmodExEngine();
			opsMgr.logger.log(DEV, "Initialized Driver 4; Name: " + soundCard4.driverName);
			
			soundCard5 = new FmodExEngine();
			opsMgr.logger.log(DEV, "Initialized Driver 5; Name: " + soundCard5.driverName);
			
			soundCard6 = new FmodExEngine();
			opsMgr.logger.log(DEV, "Initialized Driver 6; Name: " + soundCard6.driverName);
		} catch (SoundEngineException e) {
			opsMgr.logger.log(PROD, "Error Initializing FmodExEngine!");
			opsMgr.logger.log(DEV, e.getMessage());
			return;
		}
		
		SoundscapeModel ss = TestDataProvider.testSoundscape(
				new String[] {".\\sounds\\walla, crowd in hall.mp3",".\\sounds\\ocean waves.mp3", ".\\sounds\\klaxon alarm aoogah.mp3"});
		
		try {
			soundCard1.loadSoundscape(ss, CONSOLE1);
		} catch (SoundEngineException seEx) {
			opsMgr.logger.log(DEV, "Error loading soundscape");
			return;
		}
		
		int count = 0;
		while (true) {
			Thread.sleep(500);
			soundCard1.system.update();
			
//			if (count == 10){
//				soundCard1.clearSound(ss.runtimeId, "test1");
//			}
			if (count == 5) {
				soundCard1.playSound(ss.runtimeId, "test2");
			}
			
			count++;
		}
	}
}
