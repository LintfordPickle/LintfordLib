package net.lintfordlib.options;

public class AudioSettings {

	// --------------------------------------
	// Constants / Enums
	// --------------------------------------

	static final AudioSettings createBasicTemplate() {
		final var standardSettings = new AudioSettings();
		standardSettings.mSfxVolume = 100f;
		standardSettings.mMusicVolume = 100f;
		standardSettings.mMusicEnabled = true;
		standardSettings.mSfxEnabled = true;

		standardSettings.preferredAudioDeviceName(null);

		return standardSettings;
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	private float mSfxVolume;
	private float mMusicVolume;
	private boolean mMusicEnabled;
	private boolean mSfxEnabled;

	private String mPreferredOpenAlDevice;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean sfxEnabled() {
		return mSfxEnabled;
	}

	public void sfxEnabled(boolean newSfxEnabled) {
		mSfxEnabled = newSfxEnabled;
	}

	public boolean musicEnabled() {
		return mMusicEnabled;
	}

	public void musicEnabled(boolean newMusicEnabled) {
		mMusicEnabled = newMusicEnabled;
	}

	public float sfxVolume() {
		return mSfxVolume;
	}

	public void sfxVolume(float newSfxVolume) {
		mSfxVolume = newSfxVolume;
	}

	public float musicVolume() {
		return mMusicVolume;
	}

	public void musicVolume(float newMusicVolume) {
		mMusicVolume = newMusicVolume;
	}

	public String preferredAudioDeviceName() {
		return mPreferredOpenAlDevice;
	}

	public void preferredAudioDeviceName(String preferredAudioDeviceName) {
		if (mPreferredOpenAlDevice == preferredAudioDeviceName)
			return;

		mPreferredOpenAlDevice = preferredAudioDeviceName;
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public AudioSettings() {

	}

	public AudioSettings(AudioSettings audioSettingsToCopy) {
		this.copy(audioSettingsToCopy);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void copy(AudioSettings audioSettingsToCopy) {
		mMusicEnabled = audioSettingsToCopy.musicEnabled();
		mMusicVolume = audioSettingsToCopy.musicVolume();

		mSfxEnabled = audioSettingsToCopy.sfxEnabled();
		mSfxVolume = audioSettingsToCopy.sfxVolume();

		mPreferredOpenAlDevice = audioSettingsToCopy.preferredAudioDeviceName();
	}

	public boolean isDifferent(AudioSettings audioSettingsToCheckAgainst) {
		if (audioSettingsToCheckAgainst == null)
			return true;

		// @formatter:off
		return mMusicEnabled != audioSettingsToCheckAgainst.musicEnabled() 
				|| mMusicVolume != audioSettingsToCheckAgainst.musicVolume() 
				|| mSfxEnabled != audioSettingsToCheckAgainst.sfxEnabled() 
				|| mSfxVolume != audioSettingsToCheckAgainst.sfxVolume()
				|| mPreferredOpenAlDevice != audioSettingsToCheckAgainst.preferredAudioDeviceName();
		// @formatter:on
	}
}
