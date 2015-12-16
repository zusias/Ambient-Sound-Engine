package ase_source;

/**
 * 
 * TODO: Actually determine this stuff dynamically
 */
public class Arg {
	static public boolean software_initialization = false;
	static public boolean multiple_soundcard = false;

	/** Creates a new instance of Arg */
	public Arg() {
	}

	public static void handleArg(String[] args) {
		software_initialization = false;
		multiple_soundcard = false;
	}

}
