package ase.operations;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import ase.models.RandomPlaySettings;
import ase.models.SoundModel;
import ase.models.SoundscapeModel;
import ase.models.SoundModel.PlayType;

import static java.lang.System.exit;
import static ase.models.SoundModel.PlayType.*;
import static ase.models.SoundscapeModel.PlayState.*;
import static ase.operations.Log.LogLevel.DEBUG;
import static ase.operations.Log.LogLevel.DEV;
import static ase.operations.Log.LogLevel.PROD;

/**
 * Provider meant for test data only. Static methods which
 * construct soundscapes and sound objects
 * @author Kevin C. Gall
 *
 */
public class TestDataProvider {
	private static int count = 0;
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
				OperationsManager.opsMgr.logger.log(DEBUG, e.getStackTrace());
				
				exit(-1);
				return null;
			}
			PlayType playType;
			if (testCount % 3 == 0) {
				playType = SINGLE;
			} else if (testCount % 3 == 1) {
				playType = LOOP;
			} else {
				playType = RANDOM;
			}
			
			RandomPlaySettings randomSettings = new RandomPlaySettings(0, 10, 0, 5);
			
			SoundModel newSound = new SoundModel(filePath, "test" + testCount, playType, true, 1, size, randomSettings);
			sounds[testCount++] = newSound;
		}
		
		return new SoundscapeModel(
				1,
				count++,
				1.0,
				sounds,
				"Test Soundscape",
				PLAYING,
				0);
	}
}
