package ase.bridge;

//google
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

//ASE Operations
import ase.operations.Log;
import ase.operations.OperationsManager;
import ase.operations.SoundModel;
import ase.operations.SoundscapeModel;
import ase.operations.Log.LogLevel;
import ase.operations.OperationsManager.Sections;
import ase.operations.IIterableSubscriber;
import ase.operations.ISubscriber;

/**
 * Composite of different classes which implement various
 * Subscriber interfaces. This class is presented to the
 * SoundEngineManager, which keeps a separate one for each
 * possible active soundscape.
 * @author Kevin C. Gall
 *
 */
class SoundscapeSubscriberComposite {
	static Log logger = OperationsManager.opsMgr.logger;
	
	private SoundscapeModel currentSoundscape = null;
	private final SoundEngine soundEngine;
	//private final Vector<String> soundSymbols = new Vector<>();
	private final Sections section;
	private final BiMap<Integer, String> soundSymbols = HashBiMap.create();

	//subscribers
	public final OperationsManagerSubscriber opsSubscriber = new OperationsManagerSubscriber();
	public final ISubscriber<String> soundEngineStopSoundSubscriber;
	public final ISubscriber<Boolean> soundEngineStopFadeSubscriber;
	
	public SoundscapeSubscriberComposite(SoundEngine soundEngine, Sections section) {
		this.soundEngine = soundEngine;
		this.section = section;
		
		/*
		 * Initialize subscribers that must be instantiated after
		 * main object initialization
		 */
		
		this.soundEngineStopSoundSubscriber =
				(String symbol) -> {
					BiMap<String, Integer> indexMap = this.soundSymbols.inverse();
					
					int index = indexMap.get(symbol);
					
					SoundModel sound = currentSoundscape.getSoundAtIndex(index);
					OperationsManager.opsMgr.modifySound(section, index, sound.currentPlayType, false, sound.volume);
				};
				
		this.soundEngineStopFadeSubscriber =
				(Boolean isPlaying) -> {
					OperationsManager.opsMgr.setSoundscapeIsPlaying(section, isPlaying);
					
					try {
						soundEngine.setSoundscapeVolume(currentSoundscape.runtimeId, currentSoundscape.masterVolume);
					} catch (SoundEngineException seEx) {
						logger.log(LogLevel.DEV, "The sound engine has failed: " + seEx.getMessage());
						logger.log(LogLevel.DEBUG, seEx.getStackTrace().toString());
					}
				};
	}
	
	private void loadSymbols(String[] symbols){
		this.soundSymbols.clear();
		
		for (int i = 0; i < symbols.length; i++){
			this.soundSymbols.put(i, symbols[i]);
		}
	}
	

	//private class to implement IIterableSubscriber, which has 2 methods
	private class OperationsManagerSubscriber implements IIterableSubscriber<SoundscapeModel, SoundModel> {
		@Override
		public void notifySubscriber(SoundscapeModel ss, int removedIndex){
			if (ss == currentSoundscape || soundEngine == null){
				return;
			}
			
			/* series of checks to discover what changed.
			 * The first must be ssid to determine if the entire soundscape has changed,
			 * but after that the order is less important
			 */
			try {
				if (currentSoundscape == null || currentSoundscape.runtimeId != ss.runtimeId){
					//completely new soundscape
					if (currentSoundscape != null) soundEngine.clearSoundscape(currentSoundscape.runtimeId);
					String[] symbols = soundEngine.loadSoundscape(ss, section);
					loadSymbols(symbols);
					
					//subscribe to soundscape events
					soundEngine.subscribeToFinishedSounds(ss.runtimeId, soundEngineStopSoundSubscriber);
					soundEngine.subscribeToFinishedFade(ss.runtimeId, soundEngineStopFadeSubscriber);
					//returns because loadSoundscape should act according to appropriate
					//state on its own
					return;
				}
				
				if (currentSoundscape.playState != ss.playState){
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
				
				if (currentSoundscape.masterVolume != ss.masterVolume){
					soundEngine.setSoundscapeVolume(ss.runtimeId, ss.masterVolume);
				}
				
				if (removedIndex > -1){
					soundEngine.clearSound(ss.runtimeId, soundSymbols.get(removedIndex));
					soundSymbols.remove(removedIndex);
				}
			} catch (SoundEngineException seEx){
				logger.log(LogLevel.DEV, "The sound engine has failed: " + seEx.getMessage());
				logger.log(LogLevel.DEBUG, seEx.getStackTrace().toString());
			} finally {
				currentSoundscape = ss;
			}
		}
		
		@Override
		public void notifySubscriber(int index, SoundscapeModel ss, SoundModel sound){
			//could be null for non-existant preview soundcard
			if (soundEngine == null){
				return;
			}
			
			try {
				SoundModel lastSound = null;
				if (index < currentSoundscape.getTotalSounds()){
					lastSound = currentSoundscape.getSoundAtIndex(index);
				}
				
				if (lastSound == sound){
					return;
				}
				
				//new sound appended
				//Sound engine should handle all other state if loading
				if (lastSound == null) {
					soundSymbols.put(index, soundEngine.loadSound(ss.runtimeId, sound));
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
				
				if (lastSound.randomSettings != sound.randomSettings) {
					//TODO: Invoke SoundEngine set Random Settings method
				}

			} catch (SoundEngineException seEx) {
				logger.log(Log.LogLevel.DEV, "The sound engine has failed: " + seEx.getMessage());
				logger.log(Log.LogLevel.DEBUG, seEx.getStackTrace().toString());
			} finally {
				currentSoundscape = ss;
			}
		}
	}
}
