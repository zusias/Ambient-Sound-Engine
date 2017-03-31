package ase.bridge;

import ase.operations.ISubscriber;
import ase.operations.Log;
import ase.operations.OperationsManager;
import ase.operations.OperationsManager.Sections;
import ase.operations.SoundModel;
import ase.operations.SoundscapeModel;
import ase.operations.Log.LogLevel;
import java.util.Vector;

/**
 * This class is a bridge between the SoundEngine interface and the OperationsManager's
 * observer-subscriber model. This class is intended to allow easy swapping of implemented
 * Sound Engines depending on the needs of the developers
 * @author Kevin C. Gall
 *
 */
public class SoundEngineManager {
	static Log logger = OperationsManager.opsMgr.logger;
	
	private final SoundscapeSubscriber console1Subscriber;
	private final SoundscapeSubscriber console2Subscriber;
	private final SoundscapeSubscriber effectsSubscriber;
	private final SoundscapeSubscriber previewSubscriber;
	
	/**
	 * Takes 2 instances of Sound Engine: one for each sound card you support.
	 * Creates the subscribers and adds them to the OperationsManager instance
	 * @param stage
	 * @param preview
	 */
	public SoundEngineManager(SoundEngine stage, SoundEngine preview){
		this.console1Subscriber = new SoundscapeSubscriber(stage);
		OperationsManager.opsMgr.subscribeToActiveSoundscape(Sections.CONSOLE1, this.console1Subscriber);

		this.console2Subscriber = new SoundscapeSubscriber(stage);
		OperationsManager.opsMgr.subscribeToActiveSoundscape(Sections.CONSOLE2, this.console2Subscriber);

		this.effectsSubscriber = new SoundscapeSubscriber(stage);
		OperationsManager.opsMgr.subscribeToEffects(this.effectsSubscriber);

		this.previewSubscriber = new SoundscapeSubscriber(preview);
		OperationsManager.opsMgr.subscribeToPreview(this.previewSubscriber);	
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	private class SoundscapeSubscriber implements ISubscriber<SoundscapeModel, SoundModel> {
		private SoundscapeModel lastSs = null;
		private final SoundEngine soundEngine;
		private final Vector<String> soundSymbols = new Vector<>();
		
		public SoundscapeSubscriber(SoundEngine soundEngine) {
			this.soundEngine = soundEngine;
		}
		
		@Override
		public void notify(SoundscapeModel ss, int removedIndex){
			if (ss == lastSs || soundEngine == null){
				return;
			}
			
			/* series of checks to discover what changed.
			 * The first must be ssid to determine if the entire soundscape has changed,
			 * but after that the order is less important
			 */
			try {
				if (lastSs == null || lastSs.runtimeId != ss.runtimeId){
					//completely new soundscape
					if (lastSs != null) soundEngine.clearSoundscape(lastSs.runtimeId);
					String[] symbols = soundEngine.loadSoundscape(ss);
					loadSymbols(symbols);
					//returns because loadSoundscape should act according to appropriate
					//state on its own
					return;
				}
				
				if (lastSs.playState != ss.playState){
					switch(ss.playState){
					case PLAYING:
						soundEngine.playSoundscape(ss.runtimeId);
						break;
					case STOPPED:
						soundEngine.stopSoundscape(ss.runtimeId);
						break;
					case FADEIN:
						soundEngine.fadeSoundscape(ss.runtimeId, 0.0, ss.masterVolume, ss.fadeDuration);
						break;
					case FADEOUT:
						soundEngine.fadeSoundscape(ss.runtimeId, ss.masterVolume, 0.0, ss.fadeDuration);
						break;
					default:
						throw new IllegalStateException("Soundscape passed with illegal playState");
					}
				}
				
				if (lastSs.masterVolume != ss.masterVolume){
					soundEngine.setSoundscapeVolume(ss.runtimeId, ss.masterVolume);
				}
				
				if (removedIndex > -1){
					soundEngine.clearSound(ss.runtimeId, soundSymbols.get(removedIndex));
					soundSymbols.removeElementAt(removedIndex);
				}
			} catch (SoundEngineException seEx){
				logger.log(LogLevel.DEV, "The sound engine has failed: " + seEx.getMessage());
				logger.log(LogLevel.DEBUG, seEx.getStackTrace().toString());
			} finally {
				lastSs = ss;
			}
		}
		
		@Override
		public void notify(int index, SoundscapeModel ss, SoundModel sound){
			//could be null for non-existant preview soundcard
			if (soundEngine == null){
				return;
			}
			
			try {
				SoundModel lastSound = null;
				if (index < lastSs.getTotalSounds()){
					lastSound = lastSs.getSoundAtIndex(index);
				}
				
				if (lastSound == sound){
					return;
				}
				
				//new sound appended
				//Sound engine should handle all other state if loading
				if (lastSound == null) {
					soundEngine.loadSound(sound, ss.runtimeId);
					return;
				}
				
				String symbol = soundSymbols.get(index);
				
				if (lastSound.currentPlayType != sound.currentPlayType) {
					soundEngine.setSoundPlaytype(ss.runtimeId, symbol, sound.currentPlayType);
				}
				
				if (lastSound.volume != sound.volume){
					soundEngine.setSoundVolume(ss.runtimeId, symbol, sound.volume);
				}
				
				if (lastSound.isPlaying != sound.isPlaying) {
					if (sound.isPlaying){
						soundEngine.playSound(ss.runtimeId, symbol);
					} else {
						soundEngine.stopSound(ss.runtimeId, symbol);
					}
				}

			} catch (SoundEngineException seEx) {
				logger.log(Log.LogLevel.DEV, "The sound engine has failed: " + seEx.getMessage());
				logger.log(Log.LogLevel.DEBUG, seEx.getStackTrace().toString());
			} finally {
				lastSs = ss;
			}
		}
		
		private void loadSymbols(String[] symbols){
			this.soundSymbols.clear();
			for (String s : symbols){
				this.soundSymbols.add(s);
			}
		}
	}
}