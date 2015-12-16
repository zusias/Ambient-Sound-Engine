/*
 * SoundObject.java
 *
 * Created on March 3, 2006, 8:32 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package ase_source;

import java.util.Random;

/**
 * Completed Add extra fmod variables for functions here
 * 
 * @author Lance and Kidwell
 */
public class SoundObject {
	public static final int LOOP=0;
	public static final int SINGLEPLAY=1;
	public static final int RANDOMPLAY=2;
	
	private final int X = 0;
	private final int Y = 1;
	private final int Z = 2;

	public static final String[] comboBoxOptions ={"Copyright - DO NOT USE ", 
													"CC0 No Rights Reserved ", 
													"Attribution", 
													"Attribution Share Alike", 
													"Attribution No Derivs", 
													"Attribution NonCommercial", 
													"Attribution NonCommercial Share Alike", 
													"Attribution NonCommerical No Derivs "};

	private int ID;
	private String name;
	private String url;
	private int volume;
	private int fileSizeInBytes;
	private boolean playbackEnabled;
	private Random generator = new Random();

	private float[] sound_pos = new float[3];

	int playbackType; // loop, single play, random play
	int minRepeatDelay; // minimum amount of time between plays in random play
						// mode (in seconds)
	int maxRepeatDelay; // maximum amount of time between plays in random play
						// mode (in seconds)
	int minTimesRepeated; // minimum number of times the sound will be
							// repeated when in random play mode
	int maxTimesRepeated; // minimum number of times the sound will be
							// repeated when in random play mode
	float volumeVariance; // percentage volume can vary;
	
	private boolean invalid;

	// Additional variables for FMOD effects would be located here

	/**
	 * Creates a new instance of a Sound object, takes in the size, ID,
	 * filepath, repeat delay is set to not allow for the sound to play. sound
	 * is set in no position by default.
	 */
	public SoundObject(int dbID, String dbName, String path, int size) {
		ID = dbID;
		name = dbName;
		url = path;

		fileSizeInBytes = size;
		sound_pos[X] = 0;
		sound_pos[Y] = 0;
		sound_pos[Z] = 0;

		playbackEnabled = false;
		playbackType = LOOP; // default is loop;

		minRepeatDelay = 99999; // safe guard - will not play
		maxRepeatDelay = 99999; // safe guard - will not play
		minTimesRepeated = 1;
		maxTimesRepeated = 1;

		volume = 0; // default is muted
		volumeVariance = 0; // 0 = no variance, 1.0 = max variance
	}

	/**
	 * Constructor for a sound object, takes in two extra variables, volume and
	 * playback mode.
	 */
	public SoundObject(int dbID, String dbName, String path, int dbVolume,
			int playbackMode, int size) {
		ID = dbID;
		name = dbName;
		url = path;

		fileSizeInBytes = size;
		sound_pos[X] = 0;
		sound_pos[Y] = 0;
		sound_pos[Z] = 0;

		playbackEnabled = true; // update when field is added to database
		playbackType = playbackMode;

		minRepeatDelay = 99999; // safe guard - will not play
		maxRepeatDelay = 99999; // safe guard - will not play

		volume = dbVolume;
		volumeVariance = 0; // 0 = no variance, 1.0 = max variance
	}

	public SoundObject(int dbID, String dbName, String path, int dbVolume,
			int playbackMode, int size, int minNumRepeats, int maxNumRepeats,
			int minRptDelay, int maxRptDelay) {
		ID = dbID;
		name = dbName;
		url = path;

		fileSizeInBytes = size;
		sound_pos[X] = 0;
		sound_pos[Y] = 0;
		sound_pos[Z] = 0;

		playbackEnabled = true; // update when field is added to database
		playbackType = playbackMode;

		minRepeatDelay = minRptDelay; // safe guard - will not play
		maxRepeatDelay = maxRptDelay; // safe guard - will not play

		minTimesRepeated = minNumRepeats;
		maxTimesRepeated = maxNumRepeats;

		volume = dbVolume;
		volumeVariance = 0; // 0 = no variance, 1.0 = max variance
	}

	/** Classic method to set a variable, volume in this instance */
	public void changeVolume(int newVolume) {
		volume = newVolume;
	}

	/** sets the variables for repeat delay and volume variance */
	public void setAutoWipe(int min, int max, float var) {
		minRepeatDelay = min;
		maxRepeatDelay = max;
		volumeVariance = var;
	}

	public void setMinRepeats(int newInfo) {
		this.minTimesRepeated = newInfo;
	}

	public void setMaxRepeats(int newInfo) {
		this.maxTimesRepeated = newInfo;
	}

	public void setMinRepeatDelay(int newInfo) {
		this.minRepeatDelay = newInfo;
	}

	public void setMaxRepeatDelay(int newInfo) {
		this.maxRepeatDelay = newInfo;
	}

	/** sets the integer for playback, loop, wipe, autowipe */
	public void setPlaybackType(int pbType) {
		playbackType = pbType;
	}

	/** Method to set the variable for position of the sound */
	public void change3Dpos(float x, float y, float z) {
		sound_pos[X] = x;
		sound_pos[Y] = y;
		sound_pos[Z] = z;
	}

	/** Method to set the playback mode to true or false */
	public void setPlaybackEnabled(boolean status) {
		playbackEnabled = status;
	}

	/** Method that returns the float array for the sound's position */
	public float[] get3Dpos() {
		return sound_pos;
	}

	/**
	 * Method to return the playback type
	 * 
	 * @return int, 0 for loop, 1 for manual wipe, 2 for auto wipe
	 */
	public int getPlaybackType() {
		return playbackType;
	}

	/** Method to return the volume integer */
	public int getVolume() {
		return volume;
	}

	/** returns the name of the sound object */
	public String getName() {
		return name;
	}

	/** returns the ID of the sound object */
	public int getSoundID() {
		return ID;
	}

	/** returns the path of the sound object */
	public String getSoundPath() {
		return url;
	}

	public int getLoopCount() {
		if (maxTimesRepeated <= minTimesRepeated)
			return maxTimesRepeated;
		return (generator.nextInt(maxTimesRepeated - minTimesRepeated + 1) + minTimesRepeated);
	}

	public int getMinNumLoops() {
		return this.minTimesRepeated;
	}

	public int getMaxNumLoops() {
		return this.maxTimesRepeated;
	}

	public int getRepeatTime() {
		if (minRepeatDelay >= maxRepeatDelay) {
			return maxRepeatDelay;
		}
		return (generator.nextInt(maxRepeatDelay - minRepeatDelay + 1) + minRepeatDelay);
	}

	/** returns the minimum repeat delay in seconds */
	public int getMinRepeatDelay() {
		return minRepeatDelay;
	}

	/** returns the maximum repeat delay in seconds */
	public int getMaxRepeatDelay() {
		return maxRepeatDelay;
	}

	/** returns the volume variance in the sound object */
	public float getVolumeVariance() {
		return volumeVariance;
	}

	/** returns the filesize stated in the object declaration */
	public int getFileSizeInBytes() {
		return fileSizeInBytes;
	}

	/** returns the boolean for if playback is enabled or not */
	public boolean getPlaybackEnabled() {
		return playbackEnabled;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isInvalid() {
		return invalid;
	}
	
	/**
	 * 
	 * @param invalid true for a sound being invalid
	 */
	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}
}
