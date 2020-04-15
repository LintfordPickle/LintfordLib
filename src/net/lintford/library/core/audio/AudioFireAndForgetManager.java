package net.lintford.library.core.audio;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.audio.data.AudioData;

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

	public AudioFireAndForgetManager(AudioManager pAudioManager) {
		mAudioManager = pAudioManager;
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

	public void play(String pAudioDataName) {
		play(pAudioDataName, 0, 0, 0, 0);

	}

	public void play(String pAudioDataName, float pWorldX, float pWorldY, float pVelX, float pVelY) {
		final var lAudioDataBuffer = mAudioManager.getAudioDataBufferByName(pAudioDataName);
		play(lAudioDataBuffer, pWorldX, pWorldY, pVelX, pVelY);

	}

	/** Plays the given {@link AudioData}. */
	public void play(AudioData pAudioDataBuffer, float pWorldX, float pWorldY, float pVelX, float pVelY) {
		if (pAudioDataBuffer == null || !pAudioDataBuffer.isLoaded())
			return;

		play(pAudioDataBuffer, 100f, 1f, pWorldX, pWorldY, pVelX, pVelY);

	}

	/** Plays the given {@link AudioData} at the specified volume and pitch. */
	public void play(AudioData pAudioDataBuffer, float pGain, float pPitch, float pWorldX, float pWorldY, float pVelX, float pVelY) {
		if (pAudioDataBuffer == null || !pAudioDataBuffer.isLoaded())
			return;

		final var lAudioSource = getFreeAudioSource();
		if (lAudioSource != null) {
			lAudioSource.setPosition(pWorldX, pWorldY, 0f);
			lAudioSource.setVelocity(pVelX, pVelY, 0f);
			lAudioSource.play(pAudioDataBuffer.bufferID(), pGain, pPitch);

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

	public void acquireAudioSources(int pAmt) {
		int lActualNumberOfSourcesAvailable = mAudioSourcePool.size();
		for (int i = 0; i < pAmt; i++) {
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
