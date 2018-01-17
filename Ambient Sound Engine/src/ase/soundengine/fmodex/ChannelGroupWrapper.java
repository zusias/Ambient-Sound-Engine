package ase.soundengine.fmodex;

import java.nio.FloatBuffer;
//Java imports
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


//Native FmodEx Imports
import org.jouvieje.FmodEx.ChannelGroup;
import org.jouvieje.FmodEx.Misc.BufferUtils;

import ase.operations.ISubscriber;
import ase.soundengine.SoundEngineException;

/**
 * Wrapper class that wraps ChannelGroup and contains a
 * HashMap to its associated sounds (channels).<br/>
 * Also contains vectors for subscribers to the soundscape
 * represented by this channel group.
 * Package scope as only FmodExEngine should access
 * @author Kevin C. Gall
 *
 */
class ChannelGroupWrapper {
	public final ChannelGroup channelGroup = new ChannelGroup();
	public final Map<String, PlaybackObject> playbackObjects = new HashMap<>();
	public final Vector<ISubscriber<Boolean>> fadeEndSubscribers = new Vector<>();
	public final Vector<ISubscriber<String>> soundEndSubscribers = new Vector<>();
	public final Map<String, RandomPlayRunner> activeRandomPlayers = new HashMap<>();
	
	private boolean isPlaying = false;
	private boolean isFading = false;
	
	private boolean fadeInterrupt = false;
	private float interruptEndVolume = -1.0f;
	private int interruptDuration = -1;
	
	public boolean isPlaying() {
		return isPlaying;
	}
	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}
	
	public boolean isFading() {
		return isFading;
	}
	
	public void setFading(boolean isFading) {
		this.isFading = isFading;
	}
	
	public void setInterrupt(double endVolume, int durationMs) {
		fadeInterrupt = true;
		interruptEndVolume = (float) endVolume;
		interruptDuration = durationMs;
	}
	
	public void resetInterrupt() {
		fadeInterrupt = false;
		interruptEndVolume = -1.0f;
		interruptDuration = -1;
	}
	
	public float getInterruptVolume() {
		return interruptEndVolume;
	}
	
	public int getInterruptDuration() {
		return interruptDuration;
	}
	
	public boolean getInterrupted() {
		return fadeInterrupt;
	}
}
