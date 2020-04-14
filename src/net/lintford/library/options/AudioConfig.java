package net.lintford.library.options;

import net.lintford.library.GameInfo;
import net.lintford.library.options.reader.IniFile;

public class AudioConfig extends IniFile {

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

	public void preferredAudioDeviceName(String pPreferredAudioDeviceName) {
		mPreferredOpenAlDevice = pPreferredAudioDeviceName;
	}

	public boolean masterEnabled() {
		return mMasterEnabled;
	}

	public void masterEnabled(boolean pNewMasterEnabled) {
		mMasterEnabled = pNewMasterEnabled;
	}

	public boolean musicEnabled() {
		return mMusicEnabled;
	}

	public void musicEnabled(boolean pNewMusicEnabled) {
		mMusicEnabled = pNewMusicEnabled;
	}

	public boolean soundFxEnabled() {
		return mSoundFxEnabled;
	}

	public void soundFxEnabled(boolean pNewSoundFxEnabled) {
		mSoundFxEnabled = pNewSoundFxEnabled;
	}

	public float masterVolume() {
		return mMasterVolume;
	}

	public void masterVolume(float pNewMasterVolume) {
		mMasterVolume = pNewMasterVolume;
	}

	public float soundFxVolume() {
		return mSoundFxVolume;
	}

	public void soundFxVolume(float pNewSoundFxVolume) {
		mSoundFxVolume = pNewSoundFxVolume;
	}

	public float musicVolume() {
		return mMusicVolume;
	}

	public void musicVolume(float pNewMusicVolume) {
		mMusicVolume = pNewMusicVolume;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public AudioConfig(GameInfo pGameInfo, String pConfigFilename) {
		super(pConfigFilename);

		loadConfig();

		// if no file previously existed, the underlying config is empty, so we need to set some defaults
		if (isEmpty()) {

		}

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	// --------------------------------------
	// Methods
	// --------------------------------------

	public static final String SECTION_NAME_SETTINGS = "Audio Settings";

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
