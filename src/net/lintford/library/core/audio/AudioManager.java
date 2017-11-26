package net.lintford.library.core.audio;

import static org.lwjgl.openal.ALC10.ALC_DEFAULT_DEVICE_SPECIFIER;
import static org.lwjgl.openal.ALC10.ALC_FREQUENCY;
import static org.lwjgl.openal.ALC10.ALC_REFRESH;
import static org.lwjgl.openal.ALC10.ALC_SYNC;
import static org.lwjgl.openal.ALC10.ALC_TRUE;
import static org.lwjgl.openal.ALC10.alcCloseDevice;
import static org.lwjgl.openal.ALC10.alcCreateContext;
import static org.lwjgl.openal.ALC10.alcDestroyContext;
import static org.lwjgl.openal.ALC10.alcGetInteger;
import static org.lwjgl.openal.ALC10.alcGetString;
import static org.lwjgl.openal.ALC10.alcMakeContextCurrent;
import static org.lwjgl.openal.ALC10.alcOpenDevice;
import static org.lwjgl.openal.ALC11.ALC_ALL_DEVICES_SPECIFIER;
import static org.lwjgl.openal.ALC11.ALC_MONO_SOURCES;
import static org.lwjgl.openal.ALC11.ALC_STEREO_SOURCES;
import static org.lwjgl.openal.EXTThreadLocalContext.alcSetThreadContext;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALUtil;

import net.lintford.library.core.debug.DebugManager;

public class AudioManager {

	// --------------------------------------
	// Constants
	// --------------------------------------

	/** Returns a text identifier to represent this class. */
	public static final String TAG = AudioManager.class.getSimpleName();

	/** Defines a maximum number of 'fire-and'forget' audio sources created by the AudioManager. */
	private static final int AUDIO_FAF_SOURCE_POOL_SIZE = 8;

	// --------------------------------------
	// Variables
	// --------------------------------------

	/** A pool of {@link AudioSource}s, for fire and forget sounds */
	private List<AudioSource> mFaFSourcePool;

	/** A pool of {@link AudioSource}s created for other objects (and can be reused). */
	private List<AudioSource> mAudioSources;

	private Map<String, AudioData> mBuffers;

	private long mContext;
	private long mDevice;
	private int mMaxSourceCount;
	private boolean mOpenALInitialized;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/** Returns the maxiumum numbers of sources supported by the OpenAL context. */
	public int maxSources() {
		return mMaxSourceCount;
	}

	/** Returns true if OpenAL has be initialized (device and context created). */
	public boolean isInitialized() {
		return mOpenALInitialized;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public AudioManager() {
		mBuffers = new HashMap<>();

		mAudioSources = new ArrayList<>();
		mFaFSourcePool = new ArrayList<>(AUDIO_FAF_SOURCE_POOL_SIZE);

		mContext = NULL;
		mDevice = NULL;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadALContent() {
		if (mOpenALInitialized) {
			DebugManager.DEBUG_MANAGER.logger().i(TAG, "AudioManager already initialized.");
			return;

		}

		mDevice = alcOpenDevice((ByteBuffer) null);
		if (mDevice == NULL)
			throw new IllegalStateException("Failed to open the default device.");

		ALCCapabilities deviceCaps = ALC.createCapabilities(mDevice);

		DebugManager.DEBUG_MANAGER.logger().i(TAG, "OpenALC10: " + deviceCaps.OpenALC10);
		DebugManager.DEBUG_MANAGER.logger().i(TAG, "OpenALC11: " + deviceCaps.OpenALC11);
		DebugManager.DEBUG_MANAGER.logger().i(TAG, "caps.ALC_EXT_EFX = " + deviceCaps.ALC_EXT_EFX);

		// Check the caps of the sound devie
		if (deviceCaps.OpenALC11) {
			List<String> devices = ALUtil.getStringList(NULL, ALC_ALL_DEVICES_SPECIFIER);
			if (devices == null) {
				// checkALCError(NULL);

			} else {
				for (int i = 0; i < devices.size(); i++) {
					DebugManager.DEBUG_MANAGER.logger().i(TAG, i + ": " + devices.get(i));

				}

			}

		}

		// Chose which sound device to use
		String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
		DebugManager.DEBUG_MANAGER.logger().i(TAG, "Default device: " + defaultDeviceName);
		// Assert true: defaultDeviceName != null

		mContext = alcCreateContext(mDevice, (IntBuffer) null);
		alcSetThreadContext(mContext);
		AL.createCapabilities(deviceCaps);

		DebugManager.DEBUG_MANAGER.logger().i(TAG, "ALC_FREQUENCY: " + alcGetInteger(mDevice, ALC_FREQUENCY) + "Hz");
		DebugManager.DEBUG_MANAGER.logger().i(TAG, "ALC_REFRESH: " + alcGetInteger(mDevice, ALC_REFRESH) + "Hz");
		DebugManager.DEBUG_MANAGER.logger().i(TAG, "ALC_SYNC: " + (alcGetInteger(mDevice, ALC_SYNC) == ALC_TRUE));
		mMaxSourceCount = alcGetInteger(mDevice, ALC_MONO_SOURCES);
		DebugManager.DEBUG_MANAGER.logger().i(TAG, "ALC_MONO_SOURCES: " + mMaxSourceCount);
		DebugManager.DEBUG_MANAGER.logger().i(TAG, "ALC_STEREO_SOURCES: " + alcGetInteger(mDevice, ALC_STEREO_SOURCES));

		if (mMaxSourceCount == 0) {
			DebugManager.DEBUG_MANAGER.logger().e(TAG, "AudioManager not initialised correctly. Unable to assign AudioSources!");

		}

		// Setup some initial listener data
		setListenerData(0, 0, 0);

		// Pre-allocate some Audio sources which can be used to 'fire-and-forget' some sound effects
		for (int i = 0; i < AUDIO_FAF_SOURCE_POOL_SIZE; i++) {
			// Create a new audio source, add it to the pool and assign it to this controller.
			AudioSource lAudioSource = new AudioSource();
			lAudioSource.assign(hashCode());

			mFaFSourcePool.add(lAudioSource);

		}

		mOpenALInitialized = true;

	}

	public void unloadALContent() {
		// Remove all the sound buffers
		for (AudioData lAudioData : mBuffers.values()) {
			lAudioData.unloadAudioData();

		}

		mBuffers.clear();

		// Dipose of the Fire-and-Forget AudioSources.
		for(AudioSource lAudioSource : mFaFSourcePool) {
			lAudioSource.unassign(hashCode());
			lAudioSource.dispose();
			
		}
		
		// Dispose of the assignable AudioSources.
		for(AudioSource lAudioSource : mAudioSources) {
			lAudioSource.dispose();
			
		}

		alcMakeContextCurrent(NULL);
		alcDestroyContext(mContext);
		alcCloseDevice(mDevice);

		mOpenALInitialized = false;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	/** Returns the {@link AudioData} with the given name. */
	public AudioData getSound(String pName) {
		if (pName == null || pName.length() == 0) {
			return null;

		}

		// First, check to see if a sound resource with the given name already exists and if so, return it.
		if (mBuffers.containsKey(pName)) {
			return mBuffers.get(pName);

		}

		return null;

	}

	/** Returns an OpenAL {@link AudioSource} object which can be used to play an OpenAL AudioBuffer. */
	public AudioSource getAudioSource(final int pOwnerHash) {
		final int POOL_COUNT = mAudioSources.size();

		// First check if there are any audio sources free in the pool
		for (int i = 0; i < POOL_COUNT; i++) {
			if (mAudioSources.get(i).isFree()) {
				AudioSource lAudioSource = mAudioSources.get(i);
				if (lAudioSource.assign(pOwnerHash)) {
					return lAudioSource;

				}

			}

		}

		// If there is still space left in the pool, then add a new object
		if (AUDIO_FAF_SOURCE_POOL_SIZE + POOL_COUNT < mMaxSourceCount) {
			AudioSource lNewSource = new AudioSource();
			lNewSource.assign(pOwnerHash);
			mAudioSources.add(lNewSource);

			return lNewSource;
		}

		// otherwise, return null
		return null;

	}

	// --------------------------------------
	// Helper Methods
	// --------------------------------------

	/** Loads an WAV file extension and returns a new {@link AudioData} wrapper object. */
	public AudioData loadWavSound(String pName, String pFilename) {
		if (pName == null || pName.length() == 0) {
			System.err.println("Cannot load an audio file, null/no filename provided!");
			return null;

		}

		// First, check to see if a sound resource with the given name already exists and if so, return it.
		if (mBuffers.containsKey(pName)) {
			return mBuffers.get(pName);

		}

		AudioData lNewWavData = new WaveAudioData();
		if (lNewWavData.loadAudioFromFile(pFilename)) {
			mBuffers.put(pName, lNewWavData);

		}

		return lNewWavData;

	}

	/** Loads an OGG file extension and returns a new {@link AudioData} wrapper object. */
	public AudioData loadOggSound(String pName, String pFilename) {
		if (pName == null || pName.length() == 0) {
			System.err.println("Cannot load an audio file, null/no filename provided!");
			return null;

		}

		// First, check to see if a sound resource with the given name already exists and if so, return it.
		if (mBuffers.containsKey(pName)) {
			return mBuffers.get(pName);

		}

		OGGAudioData lNewOggData = new OGGAudioData();
		if (lNewOggData.loadAudioFromFile(pFilename)) {

		}

		return lNewOggData;

	}

	/** Plays the given {@link AudioData}. */
	public void play(AudioData pData) {
		if (pData == null || !pData.isLoaded())
			return;

		play(pData, 1f, 1f);

	}

	/** Plays the given {@link AudioData} at the specified volume and pitch. */
	public void play(AudioData pData, float pGain, float pPitch) {
		if (pData == null || !pData.isLoaded())
			return;

		AudioSource lAS = getFAFAudioSource();

		if (lAS != null) {
			lAS.play(pData.bufferID(), pGain, pPitch);
		}

	}

	/** Returns the first non-playing {@link AudioSource} in the FAF_POOL. Returns null if no {@link AudioSource}s are available. */
	private AudioSource getFAFAudioSource() {
		for (int i = 0; i < AUDIO_FAF_SOURCE_POOL_SIZE; i++) {
			if (!mFaFSourcePool.get(i).isPlaying()) {
				AudioSource lAudioSource = mFaFSourcePool.get(i);
				return lAudioSource;

			}

		}

		return null; // increasePoolSize(32);

	}

	public void setListenerData(float pX, float pY, float pZ) {
		AL10.alListener3f(AL10.AL_POSITION, pX, pY, pZ);
		AL10.alListener3f(AL10.AL_VELOCITY, 0, 0, 0);

	}

}
