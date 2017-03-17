package ase.sound_engine;

import ase.operations.SoundEngine;
import ase.operations.SoundEngineException;
import ase.operations.SoundModel;
import ase.operations.SoundscapeModel;

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
 * @author Kevin C. Gall
 *
 */
public class FmodExEngine extends SoundEngine {

	public FmodExEngine() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void loadSoundscape(SoundscapeModel ssModel) throws SoundEngineException {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void modifySoundscape(int ssid, SoundscapeModel ssModel) throws SoundEngineException {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int loadSound(SoundModel sModel, int ssid) throws SoundEngineException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void modifySound(int ssid, int soundIndex, SoundModel sModel) throws SoundEngineException {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void modifyMasterVolume(int ssid, double newVolume) throws SoundEngineException {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void play(int ssid) throws SoundEngineException {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pause(int ssid) throws SoundEngineException {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop(int ssid) throws SoundEngineException {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearSoundscape(int ssid) throws SoundEngineException {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fade(int ssid, double startVolume, double endVolume, int ms) throws SoundEngineException {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
