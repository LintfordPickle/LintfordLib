package net.lintford.library.core.audio.data;

import java.io.InputStream;

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

	protected String mName;
	protected int mBufferID;

	protected float mDurationInSeconds;
	protected int mSize;
	protected int mFrequency;
	protected int mChannels;
	protected int mBitsPerSample;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float durationInSeconds() {
		return mDurationInSeconds;
	}

	public boolean isLoaded() {
		return mBufferID != DATA_NOT_LOADED;

	}

	public int bufferID() {
		return mBufferID;
	}

	public String name() {
		return mName;
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

	public abstract boolean loadAudioFromInputStream(final String pAudioName, final InputStream pInputStream);

	/** Unloads the OpenAL data stored in the buffer associated with this object. */
	public void unloadAudioData() {
		if (this.mBufferID == AudioData.DATA_NOT_LOADED)
			return;

		AL10.alDeleteBuffers(mBufferID);

		this.mBufferID = AudioData.DATA_NOT_LOADED;
		this.mName = "";

	}

}
