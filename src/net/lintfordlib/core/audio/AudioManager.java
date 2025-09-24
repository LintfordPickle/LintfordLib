package net.lintfordlib.core.audio;

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

import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.audio.data.AudioDataBase;
import net.lintfordlib.core.audio.data.OGGAudioData;
import net.lintfordlib.core.audio.data.WaveAudioData;
import net.lintfordlib.core.audio.music.MusicManager;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.core.storage.FileUtils;
import net.lintfordlib.options.AudioConfig;
import net.lintfordlib.options.AudioSettings;

public class AudioManager {

	public class AudioNubble {
		private boolean enabled;
		private float nubbleNormalised;
		public int audioType;

		public boolean isEnabled() {
			return enabled;
		}

		void nubbleNormalized(float newNubbleNormalized) {
			nubbleNormalised = MathHelper.clamp(newNubbleNormalized, 0f, 1f);
		}

		float nubbleNormalized() {
			if (!enabled)
				return 0.f;

			return nubbleNormalised;
		}

		public AudioNubble(int audioType) {
			this.audioType = audioType;
		}

	}

	public class AudioMetaDataDefinition {
		public String filepath;
		public String soundname;
		public String group; // used to group music songs into collections (e.g. menu, world-00, world-01 etc.)
		public boolean reload;
	}

	public class AudioMetaData {
		public AudioMetaDataDefinition[] AudioMetaDefinitions;

	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String META_FILE_LOCATION = "/res/audio/meta.json";

	public static final int AUDIO_SOURCE_TYPE_SOUNDFX = 0;
	public static final int AUDIO_SOURCE_TYPE_MUSIC = 1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	/** A pool of {@link AudioSource}s created for other objects (and can be reused). */
	private List<AudioSource> mAudioSources;
	private final Map<String, AudioDataBase> mAudioDataBuffers;
	private AudioListener mAudioListener;
	private long mContext;
	private long mDevice;
	private int mNumberAssignedSources;
	private boolean mOpenALInitialized;
	private int mMaxMonoSourceCount;
	private int mMaxStereoSourceCount;
	private boolean mACL10Supported;
	private boolean mACL11Supported;
	private List<String> mAudioDevices;
	private String mDefaultAudioDevice;
	private List<AudioFireAndForgetManager> mAudioFireAndForgetManagers;
	private MusicManager mMusicManager;
	private AudioConfig mAudioConfig; // TODO: Let's fuck this off
	private AudioNubble mSoundFxNubble;
	private AudioNubble mMusicNubble;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public AudioNubble musicNubble() {
		return mMusicNubble;
	}

	public AudioNubble sfxNubble() {
		return mSoundFxNubble;
	}

	public AudioConfig audioConfig() {
		return mAudioConfig;
	}

	public long device() {
		return mDevice;
	}

	public int maxMonoAudioSources() {
		return mMaxMonoSourceCount;
	}

	public int maxStereoAudioSources() {
		return mMaxStereoSourceCount;
	}

	public String defaultAudioDevice() {
		return mDefaultAudioDevice;
	}

	// TODO: Option to set current audio device

	public List<String> audioDevices() {
		return mAudioDevices;
	}

	public MusicManager musicManager() {
		return mMusicManager;
	}

	/** Returns the maxiumum numbers of sources supported by the OpenAL context. */
	public int maxSources() {
		return mMaxMonoSourceCount;
	}

	/** Returns true if OpenAL has be initialized (device and context created). */
	public boolean isInitialized() {
		return mOpenALInitialized;
	}

	public AudioDataBase getAudioDataBufferByName(String bufferName) {
		return mAudioDataBuffers.get(bufferName);
	}

	public AudioListener listener() {
		return mAudioListener;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public AudioManager(AudioConfig audioConfig) {
		mAudioConfig = audioConfig;

		mAudioSources = new ArrayList<>();
		mAudioListener = new AudioListener();
		mAudioDataBuffers = new HashMap<>();
		mAudioFireAndForgetManagers = new ArrayList<>();

		mSoundFxNubble = new AudioNubble(AUDIO_SOURCE_TYPE_SOUNDFX);
		mMusicNubble = new AudioNubble(AUDIO_SOURCE_TYPE_MUSIC);

		mMusicManager = new MusicManager(this);

		mContext = NULL;
		mDevice = NULL;

		mAudioConfig.loadConfig();
		updateSettings();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void updateSettings() {
		final var sfxVolume = mAudioConfig.settings().sfxVolume();
		final var musicVolume = mAudioConfig.settings().musicVolume();

		{ // SoundFx
			final var previousSfxEnabled = mSoundFxNubble.enabled;
			final var previousSfxVolume = mSoundFxNubble.nubbleNormalized();

			// Take the incoming audio settings for sfx
			mSoundFxNubble.enabled = mAudioConfig.settings().sfxEnabled();
			mSoundFxNubble.nubbleNormalized(sfxVolume);

			// Check for toggled sfx
			if (mSoundFxNubble.enabled != previousSfxEnabled)
				updateVolumeOfAllSources(mSoundFxNubble);

			if (previousSfxEnabled && !mSoundFxNubble.enabled)
				updateKillOfSources(mSoundFxNubble); // sfx disabled

			// Check for change of volume
			if (mSoundFxNubble.enabled && previousSfxVolume != mSoundFxNubble.nubbleNormalized())
				updateVolumeOfAllSources(mSoundFxNubble);

		}

		{ // Music
			final var previousMusicEnabled = mMusicNubble.enabled;
			final var previousVolume = mMusicNubble.nubbleNormalized();

			mMusicNubble.enabled = mAudioConfig.settings().musicEnabled();
			mMusicNubble.nubbleNormalized(musicVolume);

			if (mMusicNubble.enabled != previousMusicEnabled) {
				updateVolumeOfAllSources(mMusicNubble);
				mMusicManager.isMusicEnabled(mMusicNubble.enabled);
			}

			if (mMusicNubble.enabled && previousVolume != mMusicNubble.nubbleNormalized())
				updateVolumeOfAllSources(mMusicNubble);

		}

		mMusicManager.isMusicEnabled(mAudioConfig.settings().musicEnabled());
	}

	private void updateKillOfSources(AudioNubble audioNubble) {
		final int lAudioSourceType = audioNubble.audioType;

		final int lNumberOfAudioSources = mAudioSources.size();
		for (int i = 0; i < lNumberOfAudioSources; i++) {
			final var lAudioSource = mAudioSources.get(i);
			if (lAudioSource.audioSourceType() == lAudioSourceType) {
				lAudioSource.stop();
			}
		}
	}

	private void updateVolumeOfAllSources(AudioNubble audioNubble) {
		final int lAudioSourceType = audioNubble.audioType;

		final int lNumberOfAudioSources = mAudioSources.size();
		for (int i = 0; i < lNumberOfAudioSources; i++) {
			final var lAudioSource = mAudioSources.get(i);
			if (lAudioSource.audioSourceType() == lAudioSourceType) {
				lAudioSource.updateGain();
			}
		}
	}

	private void catchInternalException() {
		int err = AL10.alGetError();
		if (err == AL10.AL_NO_ERROR)
			return;

		String message = "Open AL error: ";
		switch (err) {
		case AL10.AL_INVALID_NAME:
			message += "AL10.AL_INVALID_NAME";
			break;
		case AL10.AL_INVALID_ENUM:
			message += "AL10.AL_INVALID_ENUM";
			break;
		case AL10.AL_INVALID_VALUE:
			message += "AL10.AL_INVALID_VALUE";
			break;
		case AL10.AL_INVALID_OPERATION:
			message += "AL10.AL_INVALID_OPERATION";
			break;

		default:
			message += "unknown error";
			break;
		}

		throw new OpenAlException(message);
	}

	class OpenAlException extends RuntimeException {
		private static final long serialVersionUID = 1748311371106593869L;

		OpenAlException(String message) {
			super(message);
		}
	}

	public void loadResources(ResourceManager resourceManager) {
		if (mOpenALInitialized) {
			Debug.debugManager().logger().i(getClass().getSimpleName(), "AudioManager already initialized.");
			return;
		}

		mDevice = alcOpenDevice((ByteBuffer) null);
		if (mDevice == NULL)
			throw new IllegalStateException("Failed to open the default device.");

		ALCCapabilities deviceCaps = ALC.createCapabilities(mDevice);

		mACL10Supported = deviceCaps.OpenALC10;
		mACL11Supported = deviceCaps.OpenALC11;

		Debug.debugManager().logger().i(getClass().getSimpleName(), "OpenALC10: " + mACL10Supported);
		Debug.debugManager().logger().i(getClass().getSimpleName(), "OpenALC11: " + mACL11Supported);
		Debug.debugManager().logger().i(getClass().getSimpleName(), "caps.ALC_EXT_EFX = " + deviceCaps.ALC_EXT_EFX);

		// Check the caps of the sound devie
		if (deviceCaps.OpenALC11) {
			mAudioDevices = ALUtil.getStringList(NULL, ALC_ALL_DEVICES_SPECIFIER);
			if (mAudioDevices == null) {
				// checkALCError(NULL);
			} else {
				for (int i = 0; i < mAudioDevices.size(); i++) {
					Debug.debugManager().logger().i(getClass().getSimpleName(), i + ": " + mAudioDevices.get(i));
				}
			}
		}

		// Chose which sound device to use
		mDefaultAudioDevice = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
		Debug.debugManager().logger().i(getClass().getSimpleName(), "Default device: " + mDefaultAudioDevice);
		// Assert true: defaultDeviceName != null

		mContext = alcCreateContext(mDevice, (IntBuffer) null);
		alcSetThreadContext(mContext);
		AL.createCapabilities(deviceCaps);

		mMaxMonoSourceCount = alcGetInteger(mDevice, ALC_MONO_SOURCES);
		mMaxStereoSourceCount = alcGetInteger(mDevice, ALC_STEREO_SOURCES);

		Debug.debugManager().logger().i(getClass().getSimpleName(), "ALC_FREQUENCY: " + alcGetInteger(mDevice, ALC_FREQUENCY) + "Hz");
		Debug.debugManager().logger().i(getClass().getSimpleName(), "ALC_REFRESH: " + alcGetInteger(mDevice, ALC_REFRESH) + "Hz");
		Debug.debugManager().logger().i(getClass().getSimpleName(), "ALC_SYNC: " + (alcGetInteger(mDevice, ALC_SYNC) == ALC_TRUE));
		Debug.debugManager().logger().i(getClass().getSimpleName(), "ALC_MONO_SOURCES: " + mMaxMonoSourceCount);
		Debug.debugManager().logger().i(getClass().getSimpleName(), "ALC_STEREO_SOURCES: " + mMaxStereoSourceCount);

		if (mMaxMonoSourceCount == 0) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "AudioManager not initialized correctly. Unable to assign AudioSources!");
		}

		mAudioListener.setPosition(0, 0, 0);
		mAudioListener.setVelocity(0, 0, 0);

		mOpenALInitialized = true;

		musicManager().loadALContent(resourceManager);

		loadAudioFilesFromMetafile(META_FILE_LOCATION);

		catchInternalException();
	}

	public void unloadResources() {
		musicManager().unloadALContent();

		for (final var lAudioData : mAudioDataBuffers.values()) {
			lAudioData.unloadAudioData();
		}

		mAudioDataBuffers.clear();

		for (final var lAudioSource : mAudioSources) {
			lAudioSource.dispose();
		}

		alcMakeContextCurrent(NULL);
		alcDestroyContext(mContext);
		alcCloseDevice(mDevice);

		mOpenALInitialized = false;
	}

	public void unloadAudioBuffer(String bufferName) {
		final var audioDataBuffer = mAudioDataBuffers.get(bufferName);

		if (audioDataBuffer == null)
			return;

		audioDataBuffer.unloadAudioData();

		mAudioDataBuffers.remove(bufferName);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	/** Returns the {@link AudioDataBase} with the given name. */
	public AudioDataBase getSound(String bufferName) {
		if (bufferName == null || bufferName.length() == 0)
			return null;

		if (mAudioDataBuffers.containsKey(bufferName))
			return mAudioDataBuffers.get(bufferName);

		return null;
	}

	public AudioNubble getAudioSourceNubbleBasedOnType(int audioSourceType) {
		if (audioSourceType == AUDIO_SOURCE_TYPE_MUSIC)
			return mMusicNubble;

		return mSoundFxNubble;
	}

	/** Returns an OpenAL {@link AudioSource} object which can be used to play an OpenAL AudioBuffer. */
	public AudioSource getAudioSource(int ownerHash, int audioSourceType) {
		final int lNumberSourcesInPool = mAudioSources.size();

		for (int i = 0; i < lNumberSourcesInPool; i++) {
			if (mAudioSources.get(i).isFree()) {
				final var lAudioSource = mAudioSources.get(i);

				if (lAudioSource.assign(ownerHash, getAudioSourceNubbleBasedOnType(audioSourceType))) {
					return lAudioSource;
				}
			}
		}

		final AudioSource lNewAudioSource = increaseAudioSourcePool(8);
		if (lNewAudioSource == null)
			return null;

		lNewAudioSource.assign(ownerHash, getAudioSourceNubbleBasedOnType(audioSourceType));
		return lNewAudioSource;
	}

	private AudioSource increaseAudioSourcePool(int enlargeByAmount) {
		final int lNumberFreeSourceSpaces = mMaxMonoSourceCount - mNumberAssignedSources;
		if (lNumberFreeSourceSpaces <= 0)
			return null;

		enlargeByAmount = Math.min(enlargeByAmount, lNumberFreeSourceSpaces);

		for (int i = 0; i < enlargeByAmount - 1; i++) {
			createNewAudioSource();
		}

		return createNewAudioSource();
	}

	private AudioSource createNewAudioSource() {
		final var audioSource = new AudioSource();
		final var sourceId = audioSource.sourceId();
		AL10.alSourcei(sourceId, AL10.AL_SOURCE_RELATIVE, AL10.AL_FALSE);
		AL10.alSourcef(sourceId, AL10.AL_GAIN, 1f);
		AL10.alSourcef(sourceId, AL10.AL_MAX_GAIN, 1f);// MathHelper.scaleToRange(mAudioConfig.soundFxVolume(), 0f, 1f, 0f, 100f));
		AL10.alSourcef(sourceId, AL10.AL_PITCH, 1f);
		AL10.alSource3f(sourceId, AL10.AL_POSITION, 0, 0, 0);
		mAudioSources.add(audioSource);

		return audioSource;
	}

	// --------------------------------------
	// Loading Methods
	// --------------------------------------

	public void loadAudioFilesFromMetafile(String metaFileLocation) {
		Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("Loading audio from meta-file %s", metaFileLocation));

		final Gson GSON = new GsonBuilder().create();

		String lMetaFileContentsString = null;
		AudioMetaData lAudioMetaObject = null;

		lMetaFileContentsString = FileUtils.loadString(metaFileLocation);
		lAudioMetaObject = GSON.fromJson(lMetaFileContentsString, AudioMetaData.class);

		if (lAudioMetaObject == null || lAudioMetaObject.AudioMetaDefinitions == null || lAudioMetaObject.AudioMetaDefinitions.length == 0) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "There was an error reading the audio meta file");
			return;
		}

		final int lNumberOfAudioFileDefinitions = lAudioMetaObject.AudioMetaDefinitions.length;
		for (int i = 0; i < lNumberOfAudioFileDefinitions; i++) {
			final var lAudioDataDefinition = lAudioMetaObject.AudioMetaDefinitions[i];

			final var lSoundName = lAudioDataDefinition.soundname;
			final var lFilepath = lAudioDataDefinition.filepath;
			final var lReload = lAudioDataDefinition.reload;

			loadAudioFile(lSoundName, lFilepath, lReload);
		}
	}

	public AudioDataBase loadAudioFile(String soundName, String filepath, boolean reload) {
		if (!mOpenALInitialized) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Cannot load AudioData files until the AudioManager has been loaded");
			return null;
		}

		if (soundName == null || soundName.length() == 0)
			return null;

		if (!reload && mAudioDataBuffers.containsKey(soundName))
			return mAudioDataBuffers.get(soundName);

		final var lSoundName = soundName;
		final var lSoundData = loadAudioFile(lSoundName, filepath);

		if (lSoundData != null) {
			if (reload) {
				Debug.debugManager().logger().i(getClass().getSimpleName(), "Re-Loaded AudioData file '" + filepath + "' as " + lSoundName);
			} else {
				Debug.debugManager().logger().i(getClass().getSimpleName(), "Loaded AudioData file '" + filepath + "' as " + lSoundName);
			}

			mAudioDataBuffers.put(lSoundName, lSoundData);
		}

		return lSoundData;
	}

	private AudioDataBase loadAudioFile(String name, String filepath) {
		if (filepath == null || filepath.length() == 0)
			return null;

		InputStream lInputStream = null;
		if (filepath.charAt(0) == '/') {
			lInputStream = loadAudioDataFromResource(filepath);
		} else {
			lInputStream = loadAudioDataFromFile(filepath);
		}

		if (lInputStream == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Couldn't open the audio file: " + filepath);
			return null;
		}

		final var lFileExtension = FileUtils.getFileExtension(filepath);
		switch (lFileExtension) {
		case ".wav":
			final var lNewWavData = new WaveAudioData();
			lNewWavData.loadAudioFromInputStream(name, lInputStream);
			return lNewWavData;

		case ".ogg":
			final var lNewOggAudioData = new OGGAudioData();
			lNewOggAudioData.loadAudioFromInputStream(name, lInputStream);
			return lNewOggAudioData;

		default:
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Failed to recognize the audio file extension.");
			return null;
		}
	}

	/**
	 * Allows us to registered audio data loaded elsewhere.
	 */
	public void registerAudioData(String name, AudioDataBase audioData) {
		if (audioData != null && audioData.isLoaded()) {
			mAudioDataBuffers.put(name, audioData);
		}
	}

	private InputStream loadAudioDataFromResource(String resourcename) {
		final var lInputStream = FileUtils.class.getResourceAsStream(resourcename);
		if (lInputStream == null)
			return null;

		return new BufferedInputStream(lInputStream);
	}

	private InputStream loadAudioDataFromFile(String filename) {
		final var lNewFile = new File(filename);

		if (!lNewFile.exists())
			return null;

		try {
			return new BufferedInputStream(new FileInputStream(lNewFile));
		} catch (FileNotFoundException e) {
			Debug.debugManager().logger().e(AudioManager.class.getSimpleName(), "Couldn't load audio data file '" + lNewFile + "' - file doesn't exist!");
		}

		return null;
	}

	// --------------------------------------
	// Factory Methods
	// --------------------------------------

	public AudioFireAndForgetManager getFireAndForgetManager(int numberSources) {
		final var lNewFireAndForgetManager = getFreeAudioFireAndForgetManager();
		lNewFireAndForgetManager.acquireAudioSources(numberSources);

		return lNewFireAndForgetManager;
	}

	public void releaseFireAndForgetManager(AudioFireAndForgetManager audioFireAndForgetManager) {
		if (mAudioFireAndForgetManagers.contains(audioFireAndForgetManager)) {
			mAudioFireAndForgetManagers.remove(audioFireAndForgetManager);
		}
	}

	private AudioFireAndForgetManager getFreeAudioFireAndForgetManager() {
		final int lNumberOfmAudioFireAndForgetManagers = mAudioFireAndForgetManagers.size();

		for (int i = 0; i < lNumberOfmAudioFireAndForgetManagers; i++) {
			if (!mAudioFireAndForgetManagers.get(i).isInUse()) {
				return mAudioFireAndForgetManagers.get(i);
			}
		}

		return createAudioFireAndForgetManager();
	}

	private AudioFireAndForgetManager createAudioFireAndForgetManager() {
		final var lNewFireAndForgetManagear = new AudioFireAndForgetManager(this);
		mAudioFireAndForgetManagers.add(lNewFireAndForgetManagear);

		return lNewFireAndForgetManagear;
	}

	public void applySettings(AudioSettings audioSettings) {
		 mAudioConfig.applySettings(audioSettings);
		 updateSettings();
		
	}
}
