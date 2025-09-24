package net.lintfordlib.options;

import net.lintfordlib.GameInfo;
import net.lintfordlib.options.reader.IniFile;

public class AudioConfig extends IniFile {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String SECTION_NAME_SETTINGS = "Audio Settings";

	static final AudioSettings createBasicTemplate() {
		final var standardSettings = new AudioSettings();
		standardSettings.sfxVolume(1);
		standardSettings.musicVolume(1);

		standardSettings.sfxEnabled(true);
		standardSettings.musicEnabled(true);

		return standardSettings;
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	private AudioSettings audioSettings;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public AudioSettings settings() {
		return audioSettings;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public AudioConfig(GameInfo gameInfo, String configFilename) {
		super(configFilename);

		audioSettings = new AudioSettings();
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
			settings().copy(createBasicTemplate());
			saveConfig();
		} else {
			settings().musicVolume(getFloat(SECTION_NAME_SETTINGS, "MusicVolume", 1));
			settings().sfxVolume(getFloat(SECTION_NAME_SETTINGS, "SoundFxVolume", 1));

			settings().musicEnabled(getBoolean(SECTION_NAME_SETTINGS, "MusicEnabled", true));
			settings().sfxEnabled(getBoolean(SECTION_NAME_SETTINGS, "SoundFxEnabled", true));

			// TODO: Preferred device name

			saveConfig();
		}
	}

	@Override
	public void saveConfig() {
		clearEntries();

		setValue(SECTION_NAME_SETTINGS, "MusicVolume", settings().musicVolume());
		setValue(SECTION_NAME_SETTINGS, "SoundFxVolume", settings().sfxVolume());

		setValue(SECTION_NAME_SETTINGS, "MusicEnabled", settings().musicEnabled());
		setValue(SECTION_NAME_SETTINGS, "SoundFxEnabled", settings().sfxEnabled());

		super.saveConfig();
	}
}
