package ase.models;

import java.nio.file.Path;
import java.nio.file.Paths;

import ase.models.SoundModel.PlayType;
import ase.models.SoundscapeModel.PlayState;

/**
 * Factory for models. This class defines defaults for models as necessary.
 * @author Kevin C. Gall
 *
 */
public class ModelFactory {
	
	//New Soundscape defaults 
	private int runtimeId = 0;
	private SoundscapeModel defaultSs = new SoundscapeModel(-1, -1, 1.0, null,
			"New Soundscape", SoundscapeModel.PlayState.STOPPED, 0);
	
	//New Sound Defaults
	private RandomPlaySettings defaultRandomSettings = new RandomPlaySettings(0, 10, 0, 5);
	
	public SoundscapeModel getNewSoundscape() {
		return defaultSs.copy(runtimeId++);
	}
	
	public SoundscapeModel copySoundscape(SoundscapeModel ss) {
		return ss.copy(runtimeId++).setSsid(-1);
	}
	
	/**
	 * Instantiates a Soundscape with provided data. Intended to instantiate a soundscape previously saved
	 * in the database, so with a valid ssid. Allocates a unique runtimeId to the soundscape
	 * @param ssid
	 * @param masterVolume
	 * @param sounds
	 * @param name
	 * @return
	 */
	public SoundscapeModel getSoundscape(int ssid, double masterVolume, SoundModel[] sounds, String name) {
		return defaultSs.copy(runtimeId++)
			.setSsid(ssid)
			.setMasterVolume(masterVolume)
			.rename(name)
			.addAllSounds(sounds);
	}
	
	/**
	 * Get a SoundModel from a sound file loaded into the database. Since this sound is not
	 * associated with a particular Soundscape, and therefore it has not had volume or play
	 * type customized, it is initialized with defaults.
	 * @param path
	 * @param name
	 * @param sizeInBytes
	 * @return
	 */
	public SoundModel getSoundWithDefaults(int soundId, String path, String name, long sizeInBytes) {
		Path soundPath = Paths.get(path);
		
		return new SoundModel(soundId, soundPath, name, PlayType.LOOP, true, 1.0, sizeInBytes, defaultRandomSettings);
	}
}
