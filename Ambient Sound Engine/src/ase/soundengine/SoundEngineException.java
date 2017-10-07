package ase.soundengine;

/**
 * This exception class is meant to be extended by the implementing sound engine
 * and thrown when the sound engine encounters errors.
 * By using a generic super class, we can abstract away any details
 * of the sound engine implementation
 * @author KevinCGall
 *
 */
public class SoundEngineException extends Exception {

	public SoundEngineException() {
		// TODO Auto-generated constructor stub
	}

	public SoundEngineException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public SoundEngineException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public SoundEngineException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public SoundEngineException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
