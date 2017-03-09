package ase.operations;

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
	public final String name;
	public final PlayType currentPlayType;
	public final boolean isPlaying;
	public final double volume;
	
	/**
	 * 
	 * @param filePath
	 * @param name
	 * @param currentPlayType
	 * @param isPlaying
	 */
	public SoundModel(Path filePath, String name, PlayType currentPlayType, boolean isPlaying, double volume){
		this.filePath = filePath;
		this.name = name;
		this.currentPlayType = currentPlayType;
		this.isPlaying = isPlaying;
		this.volume = volume;
	}
	
	public SoundModel setPlay(boolean isPlaying) {
		return isPlaying == this.isPlaying ? this :
			this.setPlay(isPlaying, this.currentPlayType);
	}
	
	/**
	 * Set model as playing and optionally change play type.
	 * @param isPlaying
	 * @param newPlayType
	 * @return Returns new instance of SoundModel as this is an immutable data structure
	 */
	public SoundModel setPlay(boolean isPlaying, PlayType newPlayType){
		return (isPlaying == this.isPlaying && newPlayType == this.currentPlayType) ? this :
			new SoundModel(this.filePath, this.name, newPlayType, isPlaying, this.volume);
	}
	
	/**
	 * Set play type without changing play status
	 * @param newPlayType
	 * @return Returns new instance of SoundModel as this is an immutable data structure
	 */
	public SoundModel setPlayType(PlayType newPlayType){
		return newPlayType == this.currentPlayType ? this :
			new SoundModel(this.filePath, this.name, newPlayType, this.isPlaying, this.volume);
	}
	
	/**
	 * 
	 * @param newVolume
	 * @return
	 */
	public SoundModel setVolume(double newVolume){
		return newVolume == this.volume ? this :
			new SoundModel(this.filePath, this.name, this.currentPlayType, this.isPlaying, newVolume);
	}
	
	/**
	 * Sets all "changeable" fields on a sound model.
	 * <b>NOTE:</b> Whereas other set methods check to ensure that something is changing
	 * before creating a new object, this method simply creates a new object no matter
	 * what. Only use this method when you know something is changing
	 * @param isPlaying
	 * @param newPlayType
	 * @param newVolume
	 * @return
	 */
	public SoundModel setAll(boolean isPlaying, PlayType newPlayType, double newVolume){
		return new SoundModel(this.filePath, this.name, newPlayType, isPlaying, newVolume);
	}
}
