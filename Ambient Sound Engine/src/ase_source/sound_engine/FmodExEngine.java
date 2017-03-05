/*
 * FmodExEngine.java
 *
 * Created on March 4, 2006, 11:07 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package ase_source.sound_engine;

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

import ase_source.Arg;
import ase_source.SoundObject;
import ase_source.Soundscape;

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

	private Soundcard stage; // object for sound card - stage out
	private Soundcard preview; // object for sound card - preview out

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
