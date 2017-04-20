package net.ld.library.core.audio;

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

public class AudioManager {

	public static void init() {

		mDevice = alcOpenDevice((ByteBuffer) null);
		if (mDevice == NULL)
			throw new IllegalStateException("Failed to open the default device.");

		ALCCapabilities deviceCaps = ALC.createCapabilities(mDevice);

		System.out.println("OpenALC10: " + deviceCaps.OpenALC10);
		System.out.println("OpenALC11: " + deviceCaps.OpenALC11);
		System.out.println("caps.ALC_EXT_EFX = " + deviceCaps.ALC_EXT_EFX);

		// Check the caps of the sound devie
		if (deviceCaps.OpenALC11) {
			List<String> devices = ALUtil.getStringList(NULL, ALC_ALL_DEVICES_SPECIFIER);
			if (devices == null) {
				// checkALCError(NULL);

			} else {
				for (int i = 0; i < devices.size(); i++) {
					System.out.println(i + ": " + devices.get(i));

				}

			}

		}

		// Chose which sound device to use
		String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
		System.out.println("Default device: " + defaultDeviceName);
		// Assert true: defaultDeviceName != null

		mContext = alcCreateContext(mDevice, (IntBuffer) null);
		alcSetThreadContext(mContext);
		AL.createCapabilities(deviceCaps);

		mMaxSourceCount = alcGetInteger(mDevice, ALC_MONO_SOURCES);

		System.out.println("ALC_FREQUENCY: " + alcGetInteger(mDevice, ALC_FREQUENCY) + "Hz");
		System.out.println("ALC_REFRESH: " + alcGetInteger(mDevice, ALC_REFRESH) + "Hz");
		System.out.println("ALC_SYNC: " + (alcGetInteger(mDevice, ALC_SYNC) == ALC_TRUE));
		System.out.println("ALC_MONO_SOURCES: " + mMaxSourceCount);
		System.out.println("ALC_STEREO_SOURCES: " + alcGetInteger(mDevice, ALC_STEREO_SOURCES));

		// If there are no audio sources available, then we need to disable the sounds.
		if (mMaxSourceCount == 0) {
			// TODO (John): Disbale the sound manager

			System.err.println("AudioManager not initialised correctly. Unable to assign AudioSources!");

		}

	}

	/** Cleans up the OpenAL resources and removes the device and context. */
	public static void cleanUp() {
		// Remove all the sound buffers
		for (AudioData lAudioData : mBuffers.values()) {
			lAudioData.unloadAudioData();

		}

		mBuffers.clear();

		alcMakeContextCurrent(NULL);
		alcDestroyContext(mContext);
		alcCloseDevice(mDevice);

		mContext = NULL;
		mDevice = NULL;

	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String TAG = AudioManager.class.getSimpleName();

	private static Map<String, AudioData> mBuffers;

	private static long mContext;
	private static long mDevice;

	private static AudioManager mAudioManager;
	private static int mMaxSourceCount;

	/** Defines a maximum number of 'fire-and'forget' audio sources created by the AudioManager. */
	private static final int AUDIO_FAF_SOURCE_POOL_SIZE = 8;

	// --------------------------------------
	// Variables
	// --------------------------------------

	/** A pool of {@link AudioSource}s, for fire and forget sounds */
	private List<AudioSource> mFaFSourcePool;

	/** A pool of {@link AudioSource}s created for other objects (and can be reused). */
	private List<AudioSource> mAudioSources;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/** Returns an instance of the {@link AudioManager} singleton. */
	public static AudioManager audioManager() {
		if (mAudioManager == null) {
			mAudioManager = new AudioManager();
			AudioManager.init();
			mAudioManager.initialise();
		}

		return mAudioManager;
	}

	/** Returns the maximum numbers of sources supported by the OpenAL context. */
	public static int maxSources() {
		return mMaxSourceCount;
	}

	/** Returns true if an AL context has been assigned. false otherwise. */
	public boolean contextIDAssigned() {
		return mDevice != NULL;
	}

	/** Returns true if an AL device has been assigned. false otherwise. */
	public boolean deviceConnected() {
		return mDevice != NULL;
	}

	/** Returns true if AudioManager has been properly initialised, flase otherwise. */
	public boolean isInitialised() {
		return contextIDAssigned() && deviceConnected();
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	private AudioManager() {
		mBuffers = new HashMap<>();

		mAudioSources = new ArrayList<>();
		mFaFSourcePool = new ArrayList<>(AUDIO_FAF_SOURCE_POOL_SIZE);

		mContext = NULL;
		mDevice = NULL;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	void initialise() {
		// Setup some initial listener data
		setListenerData(0, 0, -2);

		for (int i = 0; i < AUDIO_FAF_SOURCE_POOL_SIZE; i++) {
			// Create a new audio source, add it to the pool and assign it to this controller.
			AudioSource lAudioSource = new AudioSource();
			lAudioSource.assign(hashCode());

			mFaFSourcePool.add(lAudioSource);

		}
	}

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

	/** Plays the given {@link AudioData}. */
	public void play(AudioData pData) {
		if (pData == null || !pData.isLoaded())
			return;

		play(pData, 1f, 1f);

	}

	public void play(AudioData pData, float pGain, float pPitch) {
		if (pData == null || !pData.isLoaded())
			return;

		AudioSource lAS = getFAFAudioSource();

		if (lAS != null) {
			System.out.println("FaF source ID: " + lAS.sourceID());
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

	public static void setListenerData(float pX, float pY, float pZ) {
		AL10.alListener3f(AL10.AL_POSITION, pX, pY, pZ);
		AL10.alListener3f(AL10.AL_VELOCITY, 0, 0, 0);

	}

}
