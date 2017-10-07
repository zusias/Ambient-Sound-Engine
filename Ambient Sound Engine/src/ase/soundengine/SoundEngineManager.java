package ase.soundengine;

//ASE imports
import ase.operations.Log;
import ase.operations.OperationsManager;
import ase.operations.OperationsManager.Sections;

/**
 * This class is a bridge between the SoundEngine interface and the OperationsManager's
 * observer-subscriber model. This class is intended to allow easy swapping of implemented
 * Sound Engines depending on the needs of the developers
 * @author Kevin C. Gall
 *
 */
public class SoundEngineManager {
	static Log logger = OperationsManager.opsMgr.logger;
	
	private final SoundscapeSubscriberComposite console1Subscriber;
	private final SoundscapeSubscriberComposite console2Subscriber;
	private final SoundscapeSubscriberComposite effectsSubscriber;
	private final SoundscapeSubscriberComposite previewSubscriber;
	
	/**
	 * Takes 2 instances of Sound Engine: one for each sound card you support.
	 * Creates the subscribers and adds them to the OperationsManager instance.
	 * They add themselves to the SoundEngine when they load soundscapes
	 * @param stage
	 * @param preview
	 */
	public SoundEngineManager(SoundEngine stage, SoundEngine preview){
		this.console1Subscriber = new SoundscapeSubscriberComposite(stage, Sections.CONSOLE1);
		OperationsManager.opsMgr.subscribeToActiveSoundscape(Sections.CONSOLE1, this.console1Subscriber.opsSubscriber);

		this.console2Subscriber = new SoundscapeSubscriberComposite(stage, Sections.CONSOLE2);
		OperationsManager.opsMgr.subscribeToActiveSoundscape(Sections.CONSOLE2, this.console2Subscriber.opsSubscriber);

		this.effectsSubscriber = new SoundscapeSubscriberComposite(stage, Sections.EFFECTS);
		OperationsManager.opsMgr.subscribeToEffects(this.effectsSubscriber.opsSubscriber);

		this.previewSubscriber = new SoundscapeSubscriberComposite(preview, Sections.PREVIEW);
		OperationsManager.opsMgr.subscribeToPreview(this.previewSubscriber.opsSubscriber);
	}
}