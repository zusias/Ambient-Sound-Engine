package ase.soundengine;

import com.google.common.eventbus.Subscribe;

//ASE imports
import ase.operations.Log;
import ase.operations.OperationsManager.Sections;
import ase.operations.events.ChangedSoundscapeEvent;

import static ase.operations.OperationsManager.opsMgr;

/**
 * This class is a bridge between the SoundEngine interface and the OperationsManager's
 * observer-subscriber model. This class is intended to allow easy swapping of implemented
 * Sound Engines depending on the needs of the developers
 * @author Kevin C. Gall
 *
 */
public class SoundEngineManager {
	static Log logger = opsMgr.logger;
	
	private final SoundscapeEventHandler console1Subscriber;
	private final SoundscapeEventHandler console2Subscriber;
	private final SoundscapeEventHandler effectsSubscriber;
	private final SoundscapeEventHandler previewSubscriber;
	
	/**
	 * Takes 2 instances of Sound Engine: one for each sound card you support.
	 * Creates the subscribers and adds them to the OperationsManager instance.
	 * They add themselves to the SoundEngine when they load soundscapes
	 * @param stage
	 * @param preview
	 */
	public SoundEngineManager(SoundEngine stage, SoundEngine preview){
		this.console1Subscriber = new SoundscapeEventHandler(stage, Sections.CONSOLE1);
		this.console2Subscriber = new SoundscapeEventHandler(stage, Sections.CONSOLE2);
		this.effectsSubscriber = new SoundscapeEventHandler(stage, Sections.EFFECTS);
		this.previewSubscriber = new SoundscapeEventHandler(preview, Sections.PREVIEW);
		
		opsMgr.eventBus.register(this);
	}
	
	@Subscribe public void soundscapeChangeListener(ChangedSoundscapeEvent evt) {
		SoundscapeEventHandler handler = null;
		
		switch(evt.section) {
			case CONSOLE1:
				handler = console1Subscriber;
				break;
			case CONSOLE2:
				handler = console2Subscriber;
				break;
			case EFFECTS:
				handler = effectsSubscriber;
				break;
			case PREVIEW:
				handler = previewSubscriber;
				break;
		}
		
		if (evt.sound == null) {
			handler.handleSoundscapeChange(evt.soundscape, evt.soundIndex);
		} else {
			handler.handleSoundChange(evt.soundscape, evt.sound, evt.soundIndex);
		}
	}
}