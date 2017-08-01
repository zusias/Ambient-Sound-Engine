package ase.operations;

//java imports
import java.nio.file.Path;

/**
 * Immutable data model for sounds
 * @author Kevin
 *
 */
public class SoundModel {
	public static enum PlayType {
		LOOP, SINGLE, RANDOM
	}
	
	public final Path filePath;
	public final long sizeInBytes;
	public final String name;
	public final PlayType currentPlayType;
	public final boolean isPlaying;
	public final double volume;
	public final RandomPlaySettings randomSettings;
	
	/**
	 * 
	 * @param filePath
	 * @param name
	 * @param currentPlayType
	 * @param isPlaying
	 * @param volume
	 * @param sizeInBytes
	 * @param randomSettings
	 */
	public SoundModel(
			Path filePath,
			String name,
			PlayType currentPlayType,
			boolean isPlaying,
			double volume,
			long sizeInBytes,
			RandomPlaySettings randomSettings) {
		double normalizedVolume = volume;
		if (volume < 0.0) normalizedVolume = 0.0;
		if (volume > 1.0) normalizedVolume = 1.0;
		
		this.filePath = filePath;
		this.sizeInBytes = sizeInBytes;
		this.name = name;
		this.currentPlayType = currentPlayType;
		this.isPlaying = isPlaying;
		this.volume = normalizedVolume;
		this.randomSettings = randomSettings;
	}
	
	SoundModel setPlay(boolean isPlaying) {
		return isPlaying == this.isPlaying ? this :
			this.setPlay(isPlaying, this.currentPlayType);
	}
	
	/**
	 * Set model as playing and optionally change play type.
	 * @param isPlaying
	 * @param newPlayType
	 * @return Returns new instance of SoundModel as this is an immutable data structure
	 */
	SoundModel setPlay(boolean isPlaying, PlayType newPlayType){
		return (isPlaying == this.isPlaying && newPlayType == this.currentPlayType) ? this :
			new SoundModel(this.filePath, this.name, newPlayType, isPlaying, this.volume, this.sizeInBytes, this.randomSettings);
	}
	
	/**
	 * Set play type without changing play status
	 * @param newPlayType
	 * @return Returns new instance of SoundModel as this is an immutable data structure
	 */
	SoundModel setPlayType(PlayType newPlayType){
		return newPlayType == this.currentPlayType ? this :
			new SoundModel(this.filePath, this.name, newPlayType, this.isPlaying, this.volume, this.sizeInBytes, this.randomSettings);
	}
	
	/**
	 * 
	 * @param newVolume number between 0.0 and 1.0 inclusive. If outside those bounds, will round to nearest
	 * @return
	 */
	SoundModel setVolume(double newVolume){
		return newVolume == this.volume ? this :
			new SoundModel(this.filePath, this.name, this.currentPlayType, this.isPlaying, newVolume, this.sizeInBytes, this.randomSettings);
	}
	
	/**
	 * @param randomSettings A new random settings object
	 */
	SoundModel setRandomPlaySettings(RandomPlaySettings randomSettings) {
		return randomSettings == this.randomSettings ? this :
			new SoundModel(this.filePath, this.name, this.currentPlayType, this.isPlaying, this.volume, this.sizeInBytes, randomSettings);
	}
	
	/**
	 * Sets all "changeable" fields on a sound model.
	 * <b>NOTE:</b> Whereas other set methods check to ensure that something is changing
	 * before creating a new object, this method simply creates a new object no matter
	 * what. Only use this method when you know something is changing
	 * @param isPlaying
	 * @param newPlayType
	 * @param newVolume
	 * @param randomSettings
	 * @return
	 */
	SoundModel setAll(boolean isPlaying, PlayType newPlayType, double newVolume, RandomPlaySettings randomSettings){
		return new SoundModel(this.filePath, this.name, newPlayType, isPlaying, newVolume, this.sizeInBytes, randomSettings);
	}
}
