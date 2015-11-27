package net.ld.library.core.config;

public class SoundConfig extends BaseConfig {

	// =============================================
	// Variables
	// =============================================

	private float mSoundVolume;
	private boolean mSoundMuted;
	private float mMusicVolume;
	private boolean mMusicMuted;

	// =============================================
	// Properties
	// =============================================

	public float soundVolume() {
		return mSoundVolume;
	}

	public void soundVolume(float pNewValue) {
		mSoundVolume = pNewValue;
	}

	public boolean soundMuted() {
		return mSoundMuted;
	}

	public void soundMuted(boolean pNewValue) {
		mSoundMuted = pNewValue;
	}

	public float musicVolume() {
		return mMusicVolume;
	}

	public void musicVolume(float pNewValue) {
		mMusicVolume = pNewValue;
	}

	public boolean musicMuted() {
		return mMusicMuted;
	}

	public void musicMuted(boolean pNewValue) {
		mMusicMuted = pNewValue;
	}

	// =============================================
	// Constructor
	// =============================================

	public SoundConfig(String pConfigFilename) {
		super(pConfigFilename);
	}

}
