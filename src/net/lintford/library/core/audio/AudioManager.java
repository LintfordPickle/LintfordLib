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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.audio.data.AudioData;
import net.lintford.library.core.audio.data.OGGAudioData;
import net.lintford.library.core.audio.data.WaveAudioData;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.storage.FileUtils;

public class AudioManager {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private AudioListener mAudioListener;

	public AudioListener listener() {
		return mAudioListener;
	}

	public class AudioMetaDataDefinition {
		public String filepath;
		public String soundname;
	}

	public class AudioMetaData {
		public AudioMetaDataDefinition[] AudioMetaDefinitions;

	}

	public static final String META_FILE_LOCATION = "/res/audio/meta.json";

	/** Defines a maximum number of 'fire-and'forget' audio sources created by the AudioManager. */
	private static final int AUDIO_FAF_SOURCE_POOL_SIZE = 8;

	// --------------------------------------
	// Variables
	// --------------------------------------

	/** A pool of {@link AudioSource}s, for fire and forget sounds */
	private List<AudioSource> mFaFSourcePool;

	/** A pool of {@link AudioSource}s created for other objects (and can be reused). */
	private List<AudioSource> mAudioSources;

	private Map<String, AudioData> mAudioDataBuffers;

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

	public AudioData getAudioDataBufferByName(String pName) {
		return mAudioDataBuffers.get(pName);

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public AudioManager() {
		mAudioDataBuffers = new HashMap<>();

		mAudioSources = new ArrayList<>();
		mFaFSourcePool = new ArrayList<>(AUDIO_FAF_SOURCE_POOL_SIZE);

		mAudioListener = new AudioListener();

		mContext = NULL;
		mDevice = NULL;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadALContent(ResourceManager pResourceManager) {
		if (mOpenALInitialized) {
			Debug.debugManager().logger().i(getClass().getSimpleName(), "AudioManager already initialized.");
			return;

		}

		mDevice = alcOpenDevice((ByteBuffer) null);
		if (mDevice == NULL)
			throw new IllegalStateException("Failed to open the default device.");

		ALCCapabilities deviceCaps = ALC.createCapabilities(mDevice);

		Debug.debugManager().logger().i(getClass().getSimpleName(), "OpenALC10: " + deviceCaps.OpenALC10);
		Debug.debugManager().logger().i(getClass().getSimpleName(), "OpenALC11: " + deviceCaps.OpenALC11);
		Debug.debugManager().logger().i(getClass().getSimpleName(), "caps.ALC_EXT_EFX = " + deviceCaps.ALC_EXT_EFX);

		// Check the caps of the sound devie
		if (deviceCaps.OpenALC11) {
			List<String> devices = ALUtil.getStringList(NULL, ALC_ALL_DEVICES_SPECIFIER);
			if (devices == null) {
				// checkALCError(NULL);

			} else {
				for (int i = 0; i < devices.size(); i++) {
					Debug.debugManager().logger().i(getClass().getSimpleName(), i + ": " + devices.get(i));

				}

			}

		}

		// Chose which sound device to use
		String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
		Debug.debugManager().logger().i(getClass().getSimpleName(), "Default device: " + defaultDeviceName);
		// Assert true: defaultDeviceName != null

		mContext = alcCreateContext(mDevice, (IntBuffer) null);
		alcSetThreadContext(mContext);
		AL.createCapabilities(deviceCaps);

		mMaxSourceCount = alcGetInteger(mDevice, ALC_MONO_SOURCES);

		Debug.debugManager().logger().i(getClass().getSimpleName(), "ALC_FREQUENCY: " + alcGetInteger(mDevice, ALC_FREQUENCY) + "Hz");
		Debug.debugManager().logger().i(getClass().getSimpleName(), "ALC_REFRESH: " + alcGetInteger(mDevice, ALC_REFRESH) + "Hz");
		Debug.debugManager().logger().i(getClass().getSimpleName(), "ALC_SYNC: " + (alcGetInteger(mDevice, ALC_SYNC) == ALC_TRUE));
		Debug.debugManager().logger().i(getClass().getSimpleName(), "ALC_MONO_SOURCES: " + mMaxSourceCount);
		Debug.debugManager().logger().i(getClass().getSimpleName(), "ALC_STEREO_SOURCES: " + alcGetInteger(mDevice, ALC_STEREO_SOURCES));

		if (mMaxSourceCount == 0) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "AudioManager not initialized correctly. Unable to assign AudioSources!");

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

		// Once all the OpenAL Capabailities have been discovered, move onto loading the audio data
		loadAudioFilesFromMetafile(META_FILE_LOCATION);

	}

	public void unloadALContent() {
		// Remove all the sound buffers
		for (AudioData lAudioData : mAudioDataBuffers.values()) {
			lAudioData.unloadAudioData();

		}

		mAudioDataBuffers.clear();

		// Dipose of the Fire-and-Forget AudioSources.
		for (AudioSource lAudioSource : mFaFSourcePool) {
			lAudioSource.unassign(hashCode());
			lAudioSource.dispose();

		}

		// Dispose of the assignable AudioSources.
		for (AudioSource lAudioSource : mAudioSources) {
			lAudioSource.dispose();

		}

		alcMakeContextCurrent(NULL);
		alcDestroyContext(mContext);
		alcCloseDevice(mDevice);

		mOpenALInitialized = false;

	}

	public void unloadAudioBuffer(String pAudioName) {
		final var lAudioDataBuffer = mAudioDataBuffers.get(pAudioName);

		if (lAudioDataBuffer == null) {
			return;

		}

		lAudioDataBuffer.unloadAudioData();

		mAudioDataBuffers.remove(pAudioName);

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
		if (mAudioDataBuffers.containsKey(pName)) {
			return mAudioDataBuffers.get(pName);

		}

		return null;

	}

	/** Returns an OpenAL {@link AudioSource} object which can be used to play an OpenAL AudioBuffer. */
	public AudioSource getAudioSource(final int pOwnerHash) {
		final int lNumberSourcesInPool = mAudioSources.size();

		// First check if there are any audio sources free in the pool
		for (int i = 0; i < lNumberSourcesInPool; i++) {
			if (mAudioSources.get(i).isFree()) {
				AudioSource lAudioSource = mAudioSources.get(i);
				if (lAudioSource.assign(pOwnerHash)) {
					return lAudioSource;

				}

			}

		}

		// If there is still space left in the pool, then add a new object
		if (AUDIO_FAF_SOURCE_POOL_SIZE + lNumberSourcesInPool < mMaxSourceCount) {
			AudioSource lNewSource = new AudioSource();
			lNewSource.assign(pOwnerHash);
			mAudioSources.add(lNewSource);

			return lNewSource;
		}

		// otherwise, return null
		return null;

	}

	// --------------------------------------
	// Loading Methods
	// --------------------------------------

	public void loadAudioFilesFromMetafile(String pMetaFileLocation) {
		Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("Loading audio from meta-file %s", pMetaFileLocation));

		final Gson GSON = new GsonBuilder().create();

		String lMetaFileContentsString = null;
		AudioMetaData lAudioMetaObject = null;

		lMetaFileContentsString = FileUtils.loadString(pMetaFileLocation);
		lAudioMetaObject = GSON.fromJson(lMetaFileContentsString, AudioMetaData.class);

		if (lAudioMetaObject == null || lAudioMetaObject.AudioMetaDefinitions == null || lAudioMetaObject.AudioMetaDefinitions.length == 0) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Couldn't load audio files from font meta file");
			return;

		}

		final int lNumberOfFontUnitDefinitions = lAudioMetaObject.AudioMetaDefinitions.length;
		for (int i = 0; i < lNumberOfFontUnitDefinitions; i++) {
			final var lAudioDataDefinition = lAudioMetaObject.AudioMetaDefinitions[i];

			final var lSoundName = lAudioDataDefinition.soundname;
			final var lFilepath = lAudioDataDefinition.filepath;

			loadAudioFile(lSoundName, lFilepath);

		}

	}

	public AudioData loadAudioFile(String pSoundName, String pFilepath) {
		if (!mOpenALInitialized) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Cannot load AudioData files until the AudioManager has been loaded");
			return null;

		}

		if (pSoundName == null || pSoundName.length() == 0) {
			return null;

		}

		if (mAudioDataBuffers.containsKey(pSoundName)) {
			return mAudioDataBuffers.get(pSoundName);

		}

		final var lSoundName = pSoundName;
		final var lSoundData = loadAudioFile(pFilepath);

		if (lSoundData != null) {
			Debug.debugManager().logger().i(getClass().getSimpleName(), "Loaded AudioData file '" + pFilepath + "' as " + lSoundName);
			mAudioDataBuffers.put(lSoundName, lSoundData);

		}

		return lSoundData;

	}

	private AudioData loadAudioFile(String pFilepath) {
		if (pFilepath == null || pFilepath.length() == 0) {
			return null;

		}

		InputStream lInputStream = null;
		if (pFilepath.charAt(0) == '/') {
			lInputStream = loadAudioDataFromResource(pFilepath);

		} else {
			lInputStream = loadAudioDataFromFile(pFilepath);

		}

		if (lInputStream == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Couldn't open the audio file: " + pFilepath);
			return null;

		}

		final var lFileExtension = FileUtils.getFileExtension(pFilepath);
		switch (lFileExtension) {
		case ".wav":
			final var lNewWavData = new WaveAudioData();
			lNewWavData.loadAudioFromInputStream(lInputStream);
			return lNewWavData;

		case ".ogg":
			final var lNewOggAudioData = new OGGAudioData();
			lNewOggAudioData.loadAudioFromInputStream(lInputStream);
			return lNewOggAudioData;

		default:
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Failed to recognize the audio file extension.");
			return null;
		}

	}

	private InputStream loadAudioDataFromResource(String pResourcename) {
		final var lInputStream = FileUtils.class.getResourceAsStream(pResourcename);
		if (lInputStream == null)
			return null;

		return new BufferedInputStream(lInputStream);

	}

	private InputStream loadAudioDataFromFile(String pFilename) {
		final var lNewFile = new File(pFilename);

		if (!lNewFile.exists()) {
			return null;

		}

		try {
			return new BufferedInputStream(new FileInputStream(lNewFile));
		} catch (FileNotFoundException e) {

		}

		return null;
	}

	// --------------------------------------
	// Helper Methods
	// --------------------------------------

	public AudioSource play(String pAudioDataName) {
		return play(getAudioDataBufferByName(pAudioDataName));

	}

	/** Plays the given {@link AudioData}. */
	public AudioSource play(AudioData pAudioDataBuffer) {
		if (pAudioDataBuffer == null || !pAudioDataBuffer.isLoaded())
			return null;

		return play(pAudioDataBuffer, 1f, 1f);

	}

	/** Plays the given {@link AudioData} at the specified volume and pitch. */
	public AudioSource play(AudioData pAudioDataBuffer, float pGain, float pPitch) {
		if (pAudioDataBuffer == null || !pAudioDataBuffer.isLoaded())
			return null;

		final var lAudioSource = getFAFAudioSource();
		if (lAudioSource != null) {
			lAudioSource.play(pAudioDataBuffer.bufferID(), pGain, pPitch);
			return lAudioSource;
		}

		return null;

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
