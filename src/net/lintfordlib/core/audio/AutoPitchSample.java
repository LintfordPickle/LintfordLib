package net.lintfordlib.core.audio;

import org.lwjgl.openal.AL10;

import net.lintfordlib.core.audio.data.AudioDataBase;
import net.lintfordlib.core.maths.InterpolationHelper;

public class AutoPitchSample {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private AudioSource audioSource;
	private AudioDataBase audioSample;

	private float rootPitch; // [0,1]
	private float minPitch;
	private float maxPitch;
	private float gainBoostDb;

	private boolean autopitchEnable;

	private final ParamFade fadeIn;
	private final ParamFade fadeOut;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public boolean isInitialized() {
		return audioSource != null && audioSource.sourceId() > 0;
	}

	public float gainBoostDb() {
		return gainBoostDb;
	}

	public void gainBoostDb(float newValue) {
		gainBoostDb = newValue;
	}

	public ParamFade fadeIn() {
		return fadeIn;
	}

	public ParamFade fadeOut() {
		return fadeOut;
	}

	// ---------------------------------------------
	// COnstructor
	// ---------------------------------------------

	public AutoPitchSample() {

		gainBoostDb = 0.f;

		rootPitch = 0.13f;
		minPitch = 0.63f;
		maxPitch = 2.00f;

		fadeIn = new ParamFade();
		fadeOut = new ParamFade();

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void enableAutopitch(float rootPitch, float minPitch, float maxPitch) {
		this.rootPitch = rootPitch;
		this.minPitch = minPitch;
		this.maxPitch = maxPitch;
		autopitchEnable = true;
	}

	public void disableAutopitch(float rootPitch, float minPitch, float maxPitch) {
		if (autopitchEnable) {
			AL10.alSourcef(audioSource.sourceId(), AL10.AL_PITCH, 1.f);
		}

		autopitchEnable = false;

	}

	public void loadSample(AudioSource source, AudioDataBase sample) {
		audioSource = source;
		audioSample = sample;

	}

	public void unload() {
		if (audioSource != null) {
			audioSource.dispose();
			audioSource.unassign();
			audioSource = null;
		}

		if (audioSample != null) {
			audioSample.unloadAudioData();
			audioSample = null;
		}
	}

	public void update(float param) {

		updateAutoPitch(param);
		updateGain(param);

	}

	public void play() {
		audioSource.play(audioSample.bufferID(), true);
	}

	public void stop() {
		audioSource.stop();
	}

	private void updateAutoPitch(float param) {
		if (!autopitchEnable) {
			AL10.alSourcef(audioSource.sourceId(), AL10.AL_PITCH, 1.f);
			return;
		}

		float pitchFactor = computePitch(param, rootPitch, 0.f, minPitch, 1.f, maxPitch);
		AL10.alSourcef(audioSource.sourceId(), AL10.AL_PITCH, pitchFactor);
	}

	private void updateGain(float param) {
		final var baseGain = (gainBoostDb == 0) ? 1.0f : (float) Math.pow(10.0f, gainBoostDb / 20.0f);

		final var fadeInGain = fadeIn.calculateGain(param);
		final var fadeOutGain = fadeOut.calculateGain(param);

		final var finalGain = baseGain * fadeInGain * fadeOutGain;

		audioSource.gain(finalGain);
		audioSource.setMaxGain(finalGain);
	}

	float computePitch(float rpmNorm, float rootPitch, float minPitchParam, float pitchAtMin, float pitchAtRoot, float pitchAtMax) {
		if (rpmNorm <= rootPitch) {
			float t = (rpmNorm - minPitchParam) / (rootPitch - minPitchParam);
			return InterpolationHelper.lerp(pitchAtMin, pitchAtRoot, t);
		} else {
			float t = (rpmNorm - rootPitch) / (1.0f - rootPitch);
			return InterpolationHelper.lerp(pitchAtRoot, pitchAtMax, t);
		}
	}
}
