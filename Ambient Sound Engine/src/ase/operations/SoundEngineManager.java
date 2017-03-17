package ase.operations;

class SoundEngineManager {
	//Save a reference to the initialized SoundEngine
	static SoundEngine soundEngine = OperationsManager.instance.soundEngine;
	static Log logger = OperationsManager.instance.logger;
	
	//define anonymous classes for each console, as they will each consist of a single method
//	static ISubscriber<SoundscapeSetModel> console1Subscriber;
//	static ISubscriber<SoundscapeSetModel> console2Subscriber;
//	static ISubscriber<SoundscapeModel> effectsSubscriber =
//			new SoundscapeSubscriber(OperationsManager.Sections.EFFECTS);
//	static ISubscriber<SoundscapeModel> previewSubscriber =
//			new SoundscapeSubscriber(OperationsManager.Sections.PREVIEW);
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	private class SoundscapeSubscriber implements ISubscriber<SoundscapeModel> {
		private SoundscapeModel lastSs;
		private final OperationsManager.Sections section;
		
		public SoundscapeSubscriber(OperationsManager.Sections section) {
			this.section = section;
		}
		
		@Override
		public void notify(SoundscapeModel ss){
			if (ss == lastSs){
				return;
			}
			
			/* series of checks to discover what changed.
			 * The first must be ssid to determine if the entire soundscape has changed,
			 * but after that the order is less important
			 */
			try {
				if (lastSs.ssid != ss.ssid){
					
				}
				
				soundEngine.play(ss.ssid);
			} catch (SoundEngineException SeEx){
				logger.log(Log.LogLevel.DEV, "The sound engine has failed: " + SeEx.getMessage());
				logger.log(Log.LogLevel.DEBUG, SeEx.getStackTrace().toString());
			}
			
			lastSs = ss;
		}
	}
}