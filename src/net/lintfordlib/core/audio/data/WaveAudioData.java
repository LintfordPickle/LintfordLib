package net.lintfordlib.core.audio.data;

import java.io.InputStream;

import org.lwjgl.openal.AL10;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.core.debug.Debug;

// FIXME: Fix memory allocations (https://github.com/LWJGL/lwjgl3-wiki/wiki/1.3.-Memory-FAQ) - do not use BufferUtils (cannot freely free resources)!
public class WaveAudioData extends AudioData {

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean loadAudioFromInputStream(String pName, InputStream pInputStream) {
		if (isLoaded())
			return false;

		mName = pName;
		mBufferID = AL10.alGenBuffers();

		final var lWaveAudioData = WaveData.create(pInputStream);

		if (lWaveAudioData == null) {
			return false;

		}

		AL10.alBufferData(mBufferID, lWaveAudioData.format, lWaveAudioData.data, lWaveAudioData.samplerate);

		mSize = AL10.alGetBufferi(mBufferID, AL10.AL_SIZE);
		mBitsPerSample = lWaveAudioData.sizeInBits;
		mFrequency = lWaveAudioData.samplerate;
		mChannels = lWaveAudioData.channels;

		final var lLengthInSamples = mSize * 8 / (1f * mBitsPerSample);
		mDurationInSeconds = (float) lLengthInSamples / (float) mFrequency;

		if (ConstantsApp.getBooleanValueDef("DEBUG_AUDIO_ENABLED", false)) {
			Debug.debugManager().logger().i(getClass().getSimpleName(), " ------ ");
			Debug.debugManager().logger().i(getClass().getSimpleName(), "AudioEntity Name: " + pName);
			Debug.debugManager().logger().i(getClass().getSimpleName(), "Size: " + mSize);
			Debug.debugManager().logger().i(getClass().getSimpleName(), "Frequency: " + mFrequency);
			Debug.debugManager().logger().i(getClass().getSimpleName(), "Channels: " + mChannels);
			Debug.debugManager().logger().i(getClass().getSimpleName(), "mBitsPerSample: " + mBitsPerSample);
			Debug.debugManager().logger().i(getClass().getSimpleName(), "Duration (Seconds): " + mDurationInSeconds);
			Debug.debugManager().logger().i(getClass().getSimpleName(), " ------ ");

		}

		lWaveAudioData.dispose();

		return true;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

}
