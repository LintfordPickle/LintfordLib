package net.ld.library.core.openal;

import static org.lwjgl.openal.ALC10.alcOpenDevice;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.ByteBuffer;

import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;

public class SoundManager {

	private long mDevice = -1;
	
	public boolean isInitialised(){
		return mDevice != -1;
	}
	
	public SoundManager(){
		
	}
	
	public void initialise(){
		mDevice = alcOpenDevice((ByteBuffer)null);
		if ( mDevice == NULL )
			throw new IllegalStateException("Failed to open the default device.");
		
		ALCCapabilities deviceCaps = ALC.createCapabilities(mDevice);

		
		
	}
	
}
