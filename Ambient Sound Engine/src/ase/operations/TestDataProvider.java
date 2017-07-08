package ase.operations;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


import static java.lang.System.exit;

import static ase.operations.Log.LogLevel.DEBUG;
import static ase.operations.Log.LogLevel.DEV;
import static ase.operations.Log.LogLevel.PROD;
import static ase.operations.SoundModel.PlayType.*;
import static ase.operations.SoundscapeModel.PlayState.*;

/**
 * Provider meant for test data only. Static methods which
 * construct soundscapes and sound objects
 * @author Kevin C. Gall
 *
 */
public class TestDataProvider {
	public static SoundscapeModel testSoundscape(String[] fileNames) {
		SoundModel[] sounds = new SoundModel[fileNames.length];
		int testCount = 0;
		
		for (String name : fileNames) {
			Path filePath = Paths.get(name);
			
			long size;
			try {
				size = OperationsManager.getFileSize(filePath);
			} catch (IOException e){
				OperationsManager.opsMgr.logger.log(DEV, "IO Error: " + e.getMessage());
				OperationsManager.opsMgr.logger.log(DEBUG, e.getStackTrace().toString());
				
				exit(-1);
				return null;
			}
			
			SoundModel newSound = new SoundModel(filePath, "test" + testCount, SINGLE, true, 1, size);
			sounds[testCount++] = newSound;
		}
		
		return new SoundscapeModel(
				1,
				1,
				1.0,
				sounds,
				"Test Soundscape",
				PLAYING,
				0);
	}
}
