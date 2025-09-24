package net.lintfordlib.core.audio.data;

import java.io.InputStream;

import org.lwjgl.openal.AL10;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.core.debug.Debug;

public class WaveAudioData extends AudioDataBase {

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean loadAudioFromInputStream(String name, InputStream inputStream) {
		if (isLoaded())
			return false;

		mName = name;
		mBufferID = AL10.alGenBuffers();

		final var lWaveAudioData = WaveData.create(inputStream);

		if (lWaveAudioData == null)
			return false;

		AL10.alBufferData(mBufferID, lWaveAudioData.format, lWaveAudioData.data, lWaveAudioData.samplerate);

		mSize = AL10.alGetBufferi(mBufferID, AL10.AL_SIZE);
		mBitsPerSample = lWaveAudioData.sizeInBits;
		mFrequency = lWaveAudioData.samplerate;
		mChannels = lWaveAudioData.channels;

		final var lLengthInSamples = mSize * 8 / (1f * mBitsPerSample);
		mDurationInSeconds = (float) lLengthInSamples / (float) mFrequency;

		if (ConstantsApp.getBooleanValueDef("DEBUG_AUDIO_ENABLED", false)) {
			Debug.debugManager().logger().i(getClass().getSimpleName(), " ------ ");
			Debug.debugManager().logger().i(getClass().getSimpleName(), "AudioEntity Name: " + name);
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

}
