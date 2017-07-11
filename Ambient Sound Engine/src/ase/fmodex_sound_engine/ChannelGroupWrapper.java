package ase.fmodex_sound_engine;

//Java imports
import java.util.HashMap;
import java.util.Map;

//ASE imports
import ase.operations.OperationsManager.Sections;

//Native FmodEx Imports
import org.jouvieje.FmodEx.ChannelGroup;
import org.jouvieje.FmodEx.Channel;

/**
 * Simple wrapper class that wraps ChannelGroup and contains a
 * HashMap to its associated sounds (channels)
 * Package scope as only FmodExEngine should access
 * @author Kevin C. Gall
 *
 */
class ChannelGroupWrapper {
	public final ChannelGroup channelGroup = new ChannelGroup();
	public final Sections section;
	public final Map<String, Channel> channels = new HashMap<>();
	
	public ChannelGroupWrapper (Sections section) {
		this.section = section;
	}
}
