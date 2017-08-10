package ase.fmodex_sound_engine;

//Package imports
import static ase.fmodex_sound_engine.FmodExEngine.fmodErrCheck;
import static ase.fmodex_sound_engine.FmodExEngine.logger;
import static ase.operations.Log.LogLevel.*;

//Native FmodEx Imports
import org.jouvieje.FmodEx.ChannelGroup;

import ase.bridge.SoundEngineException;

/**
 * Runnable that fades a soundscape by setting the volume in
 * increments every 50ms
 * @author Kevin C. Gall
 *
 */
public class FadeRunner implements Runnable {
	private static float INTERVAL = 50.0f; //time in ms between each volume decrement
	
	private final FmodExEngine soundEngine;
	private final ChannelGroupWrapper channelGroupWrapper;
	private final float startVolume;
	private final float endVolume;
	private final int duration;
	private final int soundscapeId;
	
	private final float volumeDelta;
	private float currentVolume;
	
	/**
	 * 
	 * @param soundEngine
	 * @param channelGroup
	 * @param soundscapeId
	 * @param startVolume
	 * @param endVolume
	 * @param durationMs
	 */
	public FadeRunner (
			FmodExEngine soundEngine,
			ChannelGroupWrapper channelGroup,
			int soundscapeId,
			double startVolume,
			double endVolume,
			int durationMs) {
		
		this.soundEngine = soundEngine;
		this.channelGroupWrapper = channelGroup;
		this.soundscapeId = soundscapeId;
		this.startVolume = (float) startVolume;
		this.endVolume = (float) endVolume;
		this.duration = durationMs;
		
		
		//Ensure the delta is not 0 (with imprecision of floats) by setting a default.
		//The default may go too fast, but it is an edge case that the delta is
		//so extremely small that this implementation will do.
		
		float tempVolumeDelta = (this.endVolume - this.startVolume) / (duration / INTERVAL);
		
		if (tempVolumeDelta == 0.0f) {
			if (this.endVolume > this.startVolume) {
				tempVolumeDelta = 0.001f;
			} else if (this.endVolume < this.startVolume) {
				tempVolumeDelta = -0.001f;
			}
		}
		this.volumeDelta = tempVolumeDelta;
		
		logger.log(DEBUG, "Volume delta: " + tempVolumeDelta);
	}

	@Override
	public void run() {
		logger.log(DEV, "Beginning fade for soundscape " + soundscapeId);
		
		ChannelGroup channelGroup = channelGroupWrapper.channelGroup;
		
		//set start volume before anything else
		fmodErrCheck(channelGroup.setVolume((float) startVolume));
		currentVolume = startVolume;
		
		//begin playback for soundscape if not already playing
		if (!channelGroupWrapper.isPlaying()) {
			try {
				soundEngine.playSoundscape(soundscapeId);
			} catch (SoundEngineException seEx) {
				logger.log(DEV, "Play soundscape failed!");
				logger.log(DEBUG, seEx.getStackTrace());
			}
		}
		
		try {
			while (channelGroupWrapper.isFading() && currentVolume != endVolume) {
				//logger.log(DEBUG, "Thread sleeping for fader, soundscape ID: " + soundscapeId);
				Thread.sleep(50l);
				
				this.currentVolume = executeFadeStep();
			}
			
			logger.log(DEV, "Fade runner finishing for soundscape " + soundscapeId);
			channelGroupWrapper.setFading(false);
			
			boolean isPlaying = endVolume != 0;
			
			if (!isPlaying) {
				try {
					soundEngine.stopSoundscape(soundscapeId);
				} catch(SoundEngineException seEx) {
					logger.log(DEV, "Stop soundscape failed");
					logger.log(DEBUG, seEx.getStackTrace());
				}
			}
			
			soundEngine.publishFinishedFade(soundscapeId, isPlaying);
		} catch (InterruptedException interruptedEx) {
			logger.log(DEV, "Fade runner interrupted!");
			logger.log(DEBUG, interruptedEx.getStackTrace());
		}

	}
	
	/**
	 * One increment of the volume. Ensures that if the current volume
	 * goes past the target end, volume is reset to the end volume target
	 * @return New current volume
	 */
	private float executeFadeStep() {
		float volumeSet = currentVolume + volumeDelta;
		
		if ((volumeDelta < 0.0f && volumeSet < endVolume)
				|| (volumeDelta > 0.0f && volumeSet > endVolume)) {
			volumeSet = endVolume;
		}
		
		fmodErrCheck(channelGroupWrapper.channelGroup.setVolume(volumeSet));
		
		return volumeSet;
	}

}
