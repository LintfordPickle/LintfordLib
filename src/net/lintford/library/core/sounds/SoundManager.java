package net.lintford.library.core.sounds;

import java.util.HashMap;
import java.util.Map;

public class SoundManager {

	// =============================================
	// Variables
	// =============================================

	private static SoundManager mSoundManager;
	private Map<String, Sound> mSounds;

	// =============================================
	// Properties
	// =============================================

	public static SoundManager soundManager() {
		if (mSoundManager == null) {
			mSoundManager = new SoundManager();
		}

		return mSoundManager;
	}

	// =============================================
	// Constructor
	// =============================================

	public SoundManager() {

		mSounds = new HashMap<>();

	}

	// =============================================
	// Core-Methods
	// =============================================

	public void loadSound(String pSoundName, String pFileName) {

		if (!mSounds.containsKey(pSoundName)) {

			Sound lNewSound = Sound.loadFromResource(pFileName);
			mSounds.put(pSoundName, lNewSound);

		}

	}

	public void unloadSound(String pSoundName) {
		if (mSounds.containsKey(pSoundName)) {
			mSounds.get(pSoundName).unload();
		}
	}

	public void playSound(String pSoundName) {
		if (mSounds.containsKey(pSoundName)) {
			mSounds.get(pSoundName).play();
		}
	}

}
