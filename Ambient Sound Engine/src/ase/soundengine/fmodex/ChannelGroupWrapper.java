package ase.soundengine.fmodex;

//Java imports
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


//Native FmodEx Imports
import org.jouvieje.FmodEx.ChannelGroup;

import ase.operations.ISubscriber;

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
}
