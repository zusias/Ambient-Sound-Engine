package ase.fmodex_sound_engine;

//Java
import java.nio.IntBuffer;

//ASE
import static ase.operations.OperationsManager.opsMgr;
import static ase.operations.Log.LogLevel.*;

//Native FMODEx
import org.jouvieje.FmodEx.Misc.BufferUtils;
import org.jouvieje.FmodEx.Channel;
import org.jouvieje.FmodEx.Callbacks.FMOD_CHANNEL_CALLBACK;
import org.jouvieje.FmodEx.Enumerations.FMOD_CHANNEL_CALLBACKTYPE;
import org.jouvieje.FmodEx.Enumerations.FMOD_RESULT;
import static org.jouvieje.FmodEx.Enumerations.FMOD_RESULT.FMOD_OK;
import static org.jouvieje.FmodEx.Enumerations.FMOD_CHANNEL_CALLBACKTYPE.FMOD_CHANNEL_CALLBACKTYPE_END;

public class ChannelEndCallback implements FMOD_CHANNEL_CALLBACK {

	@Override
	public FMOD_RESULT FMOD_CHANNEL_CALLBACK(Channel channel, FMOD_CHANNEL_CALLBACKTYPE callbackType, int arg2, int arg3,
			int arg4) {
		
		if (callbackType == FMOD_CHANNEL_CALLBACKTYPE_END) {
			IntBuffer buffer = BufferUtils.newIntBuffer(256);
			FmodExEngine.fmodErrCheck(channel.getIndex(buffer));
			
			opsMgr.logger.log(DEBUG, "End Callback for channel " + buffer.get());
			opsMgr.logger.log(DEBUG,
					"Arg2 " + arg2
					+ "Arg3 " + arg3
					+ "Arg4 " + arg4
					);
		}
		
		return FMOD_OK;
	}

}
