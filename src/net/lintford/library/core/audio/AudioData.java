package net.lintford.library.core.audio;

import org.lwjgl.openal.AL10;

/** A wrapper for OpenAL audio data buffers. */
public abstract class AudioData {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int DATA_NOT_LOADED = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected int mBufferID;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isLoaded() {
		return mBufferID != DATA_NOT_LOADED;

	}

	public int bufferID() {
		return mBufferID;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public AudioData() {
		mBufferID = DATA_NOT_LOADED;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public abstract boolean loadAudioFromFile(final String pFilename);

	/** Unloads the OpenAL data stored in the buffer associated with this object. */
	public void unloadAudioData() {
		if (this.mBufferID == AudioData.DATA_NOT_LOADED)
			return;

		AL10.alDeleteBuffers(mBufferID);
		
		this.mBufferID = AudioData.DATA_NOT_LOADED;

	}

}
