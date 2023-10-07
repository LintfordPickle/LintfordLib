package net.lintfordlib.options;

import net.lintfordlib.GameInfo;
import net.lintfordlib.options.reader.IniFile;

public class AudioConfig extends IniFile {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String SECTION_NAME_SETTINGS = "Audio Settings";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private float mMasterVolume;
	private boolean mMasterEnabled;
	private float mMusicVolume;
	private boolean mMusicEnabled;
	private float mSoundFxVolume;
	private boolean mSoundFxEnabled;

	private String mPreferredOpenAlDevice;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public String preferredAudioDeviceName() {
		return mPreferredOpenAlDevice;
	}

	public void preferredAudioDeviceName(String preferredAudioDeviceName) {
		mPreferredOpenAlDevice = preferredAudioDeviceName;
	}

	public boolean masterEnabled() {
		return mMasterEnabled;
	}

	public void masterEnabled(boolean nwMasterEnabled) {
		mMasterEnabled = nwMasterEnabled;
	}

	public boolean musicEnabled() {
		return mMusicEnabled;
	}

	public void musicEnabled(boolean newMusicEnabled) {
		mMusicEnabled = newMusicEnabled;
	}

	public boolean soundFxEnabled() {
		return mSoundFxEnabled;
	}

	public void soundFxEnabled(boolean newSoundFxEnabled) {
		mSoundFxEnabled = newSoundFxEnabled;
	}

	public float masterVolume() {
		return mMasterVolume;
	}

	public void masterVolume(float newMasterVolume) {
		mMasterVolume = newMasterVolume;
	}

	public float soundFxVolume() {
		return mSoundFxVolume;
	}

	public void soundFxVolume(float newSoundFxVolume) {
		mSoundFxVolume = newSoundFxVolume;
	}

	public float musicVolume() {
		return mMusicVolume;
	}

	public void musicVolume(float newMusicVolume) {
		mMusicVolume = newMusicVolume;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public AudioConfig(GameInfo gameInfo, String configFilename) {
		super(configFilename);

		loadConfig();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void loadConfig() {
		super.loadConfig();

		// if no file previously existed, the underlying config is empty, so we need to set some defaults
		if (isEmpty()) {
			// create default values from the GameInfo
			mMasterVolume = 1;
			mMusicVolume = 1;
			mSoundFxVolume = 1;

			mMasterEnabled = true;
			mMusicEnabled = true;
			mSoundFxEnabled = true;

			saveConfig();
		} else {
			// Get the values we need
			mMasterVolume = getFloat(SECTION_NAME_SETTINGS, "MasterVolume", 1);
			mMusicVolume = getFloat(SECTION_NAME_SETTINGS, "MusicVolume", 1);
			mSoundFxVolume = getFloat(SECTION_NAME_SETTINGS, "SoundFxVolume", 1);

			mMasterEnabled = getBoolean(SECTION_NAME_SETTINGS, "MasterEnabled", true);
			mMusicEnabled = getBoolean(SECTION_NAME_SETTINGS, "MusicEnabled", true);
			mSoundFxEnabled = getBoolean(SECTION_NAME_SETTINGS, "SoundFxEnabled", true);

			// TODO: Read preferred device name

			saveConfig();
		}
	}

	@Override
	public void saveConfig() {
		clearEntries();

		// Update the entries in the map
		setValue(SECTION_NAME_SETTINGS, "MasterVolume", mMasterVolume);
		setValue(SECTION_NAME_SETTINGS, "MusicVolume", mMusicVolume);
		setValue(SECTION_NAME_SETTINGS, "SoundFxVolume", mSoundFxVolume);

		setValue(SECTION_NAME_SETTINGS, "MasterEnabled", mMasterEnabled);
		setValue(SECTION_NAME_SETTINGS, "MusicEnabled", mMusicEnabled);
		setValue(SECTION_NAME_SETTINGS, "SoundFxEnabled", mSoundFxEnabled);

		// save the entries to file
		super.saveConfig();
	}
}
