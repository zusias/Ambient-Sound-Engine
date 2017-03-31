package ase.sound_engine;

//Java lang/library imports
import java.nio.ByteBuffer;

//ASE imports
import ase.bridge.SoundEngine;
import ase.bridge.SoundEngineException;
import ase.operations.SoundModel;
import ase.operations.SoundModel.PlayType;
import ase.operations.SoundscapeModel;

//FmodEx imports
import org.jouvieje.FmodEx.System;
import org.jouvieje.FmodEx.Defines.INIT_MODES;
import org.jouvieje.FmodEx.Misc.BufferUtils;
import org.jouvieje.FmodEx.FmodEx;
import org.jouvieje.FmodEx.Init;
import org.jouvieje.FmodEx.Enumerations.FMOD_RESULT;
import org.jouvieje.FmodEx.Exceptions.InitException;
import static org.jouvieje.FmodEx.Defines.FMOD_INITFLAGS.FMOD_INIT_NORMAL;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_HARDWARE;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_LOOP_NORMAL;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_LOOP_OFF;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_SOFTWARE;
import static org.jouvieje.FmodEx.Defines.VERSIONS.FMOD_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_JAR_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_LIBRARY_VERSION;

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
	
	//private variables
	private final System system;
	
	//initial load of FmodEx library
	static {
		try {
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
		FmodEx.System_Create(system);
		
		//discover number of available drivers and compare with initialized sound cards
		ByteBuffer buffer = BufferUtils.newByteBuffer(256);
		system.getNumDrivers(buffer.asIntBuffer());
		int numDrivers = buffer.getInt(0);
		
		if (driverCount >= numDrivers){
			throw new SoundEngineException("Driver already initialized");
		}
		
		driverCount++;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] loadSoundscape(SoundscapeModel ssModel) throws SoundEngineException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String loadSound(SoundModel sModel, int id) throws SoundEngineException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearSound(int id, String symbol) throws SoundEngineException {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void playSound(int id, String symbol) throws SoundEngineException {
		// TODO Auto-generated method stub

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

}
