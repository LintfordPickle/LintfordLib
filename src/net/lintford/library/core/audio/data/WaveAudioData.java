package net.lintford.library.core.audio.data;

import java.io.InputStream;

import org.lwjgl.openal.AL10;

// FIXME: Fix memory allocations (https://github.com/LWJGL/lwjgl3-wiki/wiki/1.3.-Memory-FAQ) - do not use BufferUtils (cannot freely free resources)!
public class WaveAudioData extends AudioData {

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean loadAudioFromInputStream(InputStream pInputStream) {
		if (isLoaded())
			return false;

		mBufferID = AL10.alGenBuffers();

		final var lWaveAudioData = WaveData.create(pInputStream);

		if (lWaveAudioData == null) {
			return false;

		}

		AL10.alBufferData(mBufferID, lWaveAudioData.format, lWaveAudioData.data, lWaveAudioData.samplerate);
		lWaveAudioData.dispose();

		return true;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

}
