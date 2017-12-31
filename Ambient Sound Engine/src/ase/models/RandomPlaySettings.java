package ase.models;

/**
 * Model for settings relating to random play
 * of sounds
 * @author Kevin C. Gall
 *
 */
public class RandomPlaySettings {
	public final int minDelay;
	public final int maxDelay;
	public final int minRepeats;
	public final int maxRepeats;
	
	/**
	 * 
	 * @param minDelay In seconds
	 * @param maxDelay In seconds
	 * @param minRepeats
	 * @param maxRepeats
	 */
	public RandomPlaySettings(int minDelay, int maxDelay, int minRepeats, int maxRepeats) {
		if (minDelay < 0 || maxDelay < 0 || minRepeats < 0 || maxRepeats < 0){
			throw new IllegalArgumentException("All arguments must be positive integers or 0");
		}
		
		this.minDelay = minDelay;
		this.maxDelay = maxDelay;
		this.minRepeats = minRepeats;
		this.maxRepeats = maxRepeats;
	}
}