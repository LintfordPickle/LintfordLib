package net.lintford.library.core.audio;

import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;

import net.lintford.library.core.audio.AudioManager.AudioNubble;
import net.lintford.library.core.debug.Debug;

public class AudioSource {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int NO_OWNER = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private float mMaxSourceGain;
	private float mMinSourceGain;
	private float mSourceGain;
	private float mSourcePitch;

	private int mSourceID;
	private int mOwnerHash;
	private AudioNubble mAudioNubble;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float maxGain() {
		return mMaxSourceGain;
	}

	public float minGain() {
		return mMinSourceGain;
	}

	public float gain() {
		return mSourceGain;
	}

	public void gain(float pNewGainValue) {
		mSourceGain = pNewGainValue;
	}

	public float pitch() {
		return mSourcePitch;
	}

	public void pitch(float pNewPitchValue) {
		mSourcePitch = pNewPitchValue;
	}

	public int audioSourceType() {
		if (mAudioNubble == null) {
			return AudioManager.AUDIO_SOURCE_TYPE_SOUNDFX;

		}

		return mAudioNubble.audioType;

	}

	/** Returns the OpenAL source ID. */
	public int sourceID() {
		return mSourceID;
	}

	/** Returns true if this {@link AudioSource} is no in use and can be assigned. Returns false otherwise. */
	public boolean isFree() {
		return mOwnerHash == NO_OWNER;

	}

	/** Returns true if the sourceID has been assigned and is not equal to AL10-AL_INVALID (-1, 0xFFFFFFFF) */
	public boolean isValidSource() {
		return mSourceID != AL10.AL_INVALID;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	/** package access ctor */
	public AudioSource() {
		mSourceID = AL10.alGenSources();

		if (mSourceID == AL10.AL_INVALID) {
			Debug.debugManager().logger().e("AudioSource", "You have reached the limit of OpenAL Audio Sources");

		}

		mSourceGain = 1.f;
		mSourcePitch = 1.f;
		mOwnerHash = NO_OWNER;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	/** Applies a lock on this AudioSource and marks it as assigned. */
	public boolean assign(int pOwnerHash, AudioNubble pAudioNubble) {

		if (!isFree() || !isValidSource())
			return false;

		mAudioNubble = pAudioNubble;
		mOwnerHash = pOwnerHash;

		return true;

	}

	public boolean unassign() {
		mAudioNubble = null;
		mOwnerHash = NO_OWNER;
		return true;

	}

	/**
	 * Checks if the current lock held on this {@link AudioSource} matches the given hash value. It the hashes match, the lock is released.
	 * 
	 * @return true if this {@link AudioSource} is free after calling this method, false otherwise.
	 */
	public boolean unassign(int pOwnerHash) {
		if (isFree())
			return true;

		if (mOwnerHash != pOwnerHash) {
			// calling object doesn't own the lock
			return false;
		}

		mOwnerHash = NO_OWNER;
		return true;

	}

	/** Disposes of this AudioSource, and frees up the AudioSource in the AudioManager. */
	public void dispose() {
		if (!isFree()) {
			mOwnerHash = NO_OWNER;

		}

		if (isValidSource()) {
			AL10.alDeleteSources(mSourceID);

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	/** Instructs this {@link AudioSource} to being playing the audio buffer specified by the given buffer ID. */
	public void play(int pBufferID) {
		this.play(pBufferID, mSourceGain, 1f);

	}

	/** Instructs this {@link AudioSource} to being playing the audio buffer specified by the given buffer ID. Also specifies the volumn and the pitch of the sound. */
	public void play(int pBufferID, float pGain, float pPitch) {
		setGain(pGain);
		setPitch(pPitch);

		// associate the buffer with the source
		AL10.alSourcei(mSourceID, AL10.AL_BUFFER, pBufferID);

		// Play the source with the buffer
		AL10.alSourcei(mSourceID, AL11.AL_SEC_OFFSET, 0);
		AL10.alSourcePlay(mSourceID);

	}

	/** Pauses the {@link AudioSource}. */
	public void pause() {
		AL10.alSourcePause(mSourceID);
	}

	/** Resumes playing the {@link AudioSource} from a last position when paused. */
	public void continuePlaying() {
		AL10.alSourcePlay(mSourceID);
	}

	/** Stops the {@link AudioSource} playback and returns the position back to zero. */
	public void stop() {
		AL10.alSourceStop(mSourceID);
	}

	public void updateGain() {
		if (mOwnerHash == -1)
			return;

		AL10.alSourcef(mSourceID, AL10.AL_GAIN, mSourceGain * mAudioNubble.nubbleNormalized());

	}

	/** Sets the gain of this {@link AudioSource}. */
	public void setGain(float pNewGain) {
		mSourceGain = pNewGain;
		AL10.alSourcef(mSourceID, AL10.AL_GAIN, pNewGain * mAudioNubble.nubbleNormalized());
	}

	public void setMaxGain(float pNewMaxGain) {
		mMaxSourceGain = pNewMaxGain;
		AL10.alSourcef(mSourceID, AL10.AL_MAX_GAIN, mMaxSourceGain);
	}

	public void setMinGain(float pNewMinGain) {
		mMinSourceGain = pNewMinGain;
		AL10.alSourcef(mSourceID, AL10.AL_MIN_GAIN, mMinSourceGain);
	}

	/** Sets the pitch of this {@link AudioSource}. */
	public void setPitch(float pNewPitchValue) {
		mSourcePitch = pNewPitchValue;
		AL10.alSourcef(mSourceID, AL10.AL_PITCH, mSourcePitch);

	}

	/** Sets the 3d position of this {@link AudioSource}. n.b. the AudioBuffer must be loaded with a mono sound effect (and not a stereo sound effect). */
	public void setPosition(float pX, float pY, float pZ) {
		AL10.alSource3f(mSourceID, AL10.AL_POSITION, pX, pY, pZ);

	}

	/** Sets the velocity of this {@link AudioSource}. */
	public void setVelocity(float pX, float pY, float pZ) {
		AL10.alSource3f(mSourceID, AL10.AL_VELOCITY, pX, pY, pZ);

	}

	/** Sets whether or not this {@link AudioSource} should loop the sound effect once it has finished playing. */
	public void setLooping(boolean pNewLoopingValue) {
		AL10.alSourcei(mSourceID, AL10.AL_LOOPING, pNewLoopingValue ? AL10.AL_TRUE : AL10.AL_FALSE);

	}

	/** Returns true if this {@link AudioSource} is currently playing, otherwise returns false. */
	public boolean isPlaying() {
		return AL10.alGetSourcei(mSourceID, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
	}

	public float getCurrentPlaybackTime() {
		return AL10.alGetSourcef(sourceID(), AL11.AL_SEC_OFFSET);

	}

}
