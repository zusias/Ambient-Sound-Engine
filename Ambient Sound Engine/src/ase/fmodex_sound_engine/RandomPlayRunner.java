package ase.fmodex_sound_engine;

import ase.bridge.SoundEngineException;
import ase.operations.RandomPlaySettings;
import static ase.fmodex_sound_engine.FmodExEngine.logger;
import static ase.operations.Log.LogLevel.*;

import org.jouvieje.FmodEx.Channel;
import org.jouvieje.FmodEx.Callbacks.FMOD_CHANNEL_CALLBACK;
import org.jouvieje.FmodEx.Enumerations.FMOD_CHANNEL_CALLBACKTYPE;
import org.jouvieje.FmodEx.Enumerations.FMOD_RESULT;
import static org.jouvieje.FmodEx.Enumerations.FMOD_CHANNEL_CALLBACKTYPE.FMOD_CHANNEL_CALLBACKTYPE_END;
import static org.jouvieje.FmodEx.Enumerations.FMOD_RESULT.FMOD_OK;

public class RandomPlayRunner implements Runnable {
	private final FmodExEngine soundEngine;
	private final int channelGroupId;
	private final PlaybackObject playObj;
	
	private boolean soundPaused = false;
	private boolean soundStopped = false;
	
	public RandomPlayRunner(FmodExEngine soundEngine, int channelGroupId, PlaybackObject playObj) {
		this.soundEngine = soundEngine;
		this.channelGroupId = channelGroupId;
		this.playObj = playObj;
	}
	
	public synchronized void setPaused(boolean paused) {
		this.soundPaused = paused;
	}
	public synchronized boolean isPaused() {
		return soundPaused;
	}
	
	public synchronized void setStopped(boolean stopped) {
		this.soundStopped = stopped;
	}
	
	@Override
	public void run() {
		RandomPlaySettings settings = playObj.getRandomSettings();
		int range = settings.maxDelay - settings.minDelay + 1;
		int sleepDuration = settings.minDelay * 1000; 
		if (range > 0){
			sleepDuration =
					(FmodExEngine.random.nextInt(range) + settings.minDelay) * 1000;
		}
		
		logger.log(DEBUG, "Chosen sleep duration for RandomPlayRunner: " + sleepDuration);
		
		try {
			do {
				Thread.sleep(sleepDuration);
				synchronized (this) {
					if (!soundStopped && !isPaused()) {
						soundEngine.playSound(channelGroupId, playObj.getSoundName());
					}
					
					//pause behavior
					sleepDuration = 5000; //reset sleep duration to static 5 seconds
					if (soundStopped) {setPaused(false);} //ensure that a stopped thread trumps all
				}
			} while (isPaused());
		} catch (InterruptedException | SoundEngineException exception) {
			logger.log(DEV, "Interrupted random play runner: " + exception.getMessage());
			logger.log(DEBUG, exception.getStackTrace().toString());
		}
	}
}
