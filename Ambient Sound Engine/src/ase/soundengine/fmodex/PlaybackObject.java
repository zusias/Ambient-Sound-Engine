package ase.soundengine.fmodex;

//ASE imports
import ase.operations.SoundModel.PlayType;
import ase.operations.RandomPlaySettings;

//Native FmodEx imports
import org.jouvieje.FmodEx.Channel;
import org.jouvieje.FmodEx.Sound;

/**
 * FmodExEngine version of the Sound Model. Contains different
 * references including the channel and the sound engine's
 * unique name for this sound. Theoretically, the same
 * sound (SoundModel) could have dozens of PlaybackObjects.
 * @author Kevin C. Gall
 *
 */
public class PlaybackObject {
	public final Sound sound;
	
	private Channel channel;
	private float volume;
	private PlayType playType;
	private String soundName;
	private RandomPlaySettings randomSettings;
	
	public PlaybackObject (Sound sound, float volume, PlayType playType, String name, RandomPlaySettings randomSettings) {
		this.sound = sound;
		
		this.volume = volume;
		this.playType = playType;
		this.soundName = name;
		this.randomSettings = randomSettings;
	}
	
	public boolean hasChannel() {
		return channel != null;
	}
	public void forgetChannel() {
		this.channel = null;
	}
	public Channel getChannel() {
		return channel;
	}
	public Channel newChannel() {
		this.channel = new Channel();
		
		return this.channel;
	}
	
	public float getVolume() {
		return volume;
	}
	public void setVolume(Float volume) {
		this.volume = volume;
	}
	
	public PlayType getPlayType() {
		return playType;
	}
	public void setPlayType(PlayType playType) {
		this.playType = playType;
	}
	
	public String getSoundName() {
		return soundName;
	}
	public void setSoundName(String soundName) {
		this.soundName = soundName;
	}
	
	public RandomPlaySettings getRandomSettings() {
		return randomSettings;
	}
	public void setRandomSettings(RandomPlaySettings randomSettings) {
		this.randomSettings = randomSettings;
	}
}
