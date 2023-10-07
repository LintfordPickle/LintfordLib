package net.lintfordlib.core.audio;

import java.util.ArrayList;
import java.util.List;

import net.lintfordlib.core.audio.data.AudioData;

public class AudioFireAndForgetManager {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private AudioManager mAudioManager;
	private int mNumberOfSources;
	private boolean mIsInUse;

	/** A pool of {@link AudioSource}s, for fire and forget sounds */
	private List<AudioSource> mAudioSourcePool;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isInUse() {
		return mIsInUse;
	}

	public int numberOfSources() {
		return mNumberOfSources;
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public AudioFireAndForgetManager(AudioManager audioManager) {
		mAudioManager = audioManager;
		mAudioSourcePool = new ArrayList<>();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void assign() {
		mIsInUse = true;
	}

	public void unassign() {
		mIsInUse = false;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void play(String audioDataName) {
		play(audioDataName, 0, 0, 0, 0);
	}

	public void play(String audioDataName, float positionX, float positionY, float velocityX, float velocityY) {
		final var lAudioDataBuffer = mAudioManager.getAudioDataBufferByName(audioDataName);
		play(lAudioDataBuffer, positionX, positionY, velocityX, velocityY);
	}

	/** Plays the given {@link AudioData}. */
	public void play(AudioData audioDataBuffer, float positionX, float positionY, float velocityX, float velocityY) {
		if (audioDataBuffer == null || !audioDataBuffer.isLoaded())
			return;

		play(audioDataBuffer, 100f, 1f, positionX, positionY, velocityX, velocityY);
	}

	/** Plays the given {@link AudioData} at the specified volume and pitch. */
	public void play(AudioData audioDataBuffer, float gain, float pitch, float positionX, float positionY, float velocityX, float velocityY) {
		if (audioDataBuffer == null || !audioDataBuffer.isLoaded())
			return;

		final var lAudioSource = getFreeAudioSource();
		if (lAudioSource != null) {
			lAudioSource.setPosition(positionX, positionY, 0f);
			lAudioSource.setVelocity(velocityX, velocityY, 0f);
			lAudioSource.setLooping(false);
			lAudioSource.play(audioDataBuffer.bufferID(), gain, pitch);
		}
	}

	/** Returns the first non-playing {@link AudioSource} in the AudioSourcePool. Returns null if no {@link AudioSource}s are available. */
	private AudioSource getFreeAudioSource() {
		for (int i = 0; i < mNumberOfSources; i++) {
			if (!mAudioSourcePool.get(i).isPlaying()) {
				return mAudioSourcePool.get(i);
			}
		}

		return null;
	}

	public void acquireAudioSources(int amountToAcquire) {
		int lActualNumberOfSourcesAvailable = mAudioSourcePool.size();
		for (int i = 0; i < amountToAcquire; i++) {
			final var lNewAudioSource = mAudioManager.getAudioSource(hashCode(), AudioManager.AUDIO_SOURCE_TYPE_SOUNDFX);
			if (lNewAudioSource == null) {
				break;
			}

			lActualNumberOfSourcesAvailable++;
			mAudioSourcePool.add(lNewAudioSource);
		}

		mNumberOfSources = lActualNumberOfSourcesAvailable;
	}

	public void releaseAudioSources() {
		final int lNumberOfSources = mAudioSourcePool.size();
		for (int i = 0; i < lNumberOfSources; i++) {
			mAudioSourcePool.get(i).unassign();
		}

		mAudioSourcePool.clear();
		mNumberOfSources = 0;
	}
}
