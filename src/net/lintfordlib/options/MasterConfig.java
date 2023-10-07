package net.lintfordlib.options;

import org.lwjgl.system.MemoryUtil;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.GameInfo;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.storage.AppStorage;

public class MasterConfig {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final String VIDEO_CONFIG_FILENAME = "Video.ini";
	private static final String AUDIO_CONFIG_FILENAME = "Audio.ini";

	public enum configuration {
		audioConfig, videoConfig, all,
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	// TODO: InputConfig for key and mouse mapping
	private DisplayManager mDisplayConfig;
	private AudioConfig mAudioConfig;

	private final GameInfo mGameInfo;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public AudioConfig audio() {
		return mAudioConfig;
	}

	public DisplayManager display() {
		return mDisplayConfig;
	}

	public GameInfo gameInfor() {
		return mGameInfo;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MasterConfig(final GameInfo gameInfo) {
		mGameInfo = gameInfo;

		// Make sure that a game save directory exists for this application...
		AppStorage.createGameDataDirectory(ConstantsApp.getStringValueDef(ConstantsApp.CONSTANT_APP_NAME_TAG, "LintfordLib"));

		final String lDisplayConfigFilename = AppStorage.getGameDataDirectory() + VIDEO_CONFIG_FILENAME;
		Debug.debugManager().logger().i(getClass().getSimpleName(), "Loading video settings from '" + lDisplayConfigFilename + "'");
		mDisplayConfig = new DisplayManager(gameInfo, lDisplayConfigFilename);

		final String lAudioConfigFilename = AppStorage.getGameDataDirectory() + AUDIO_CONFIG_FILENAME;
		Debug.debugManager().logger().i(getClass().getSimpleName(), "Loading audio settings from '" + lAudioConfigFilename + "'");
		mAudioConfig = new AudioConfig(gameInfo, lAudioConfigFilename);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void handleInput(LintfordCore core) {

	}

	public void update(LintfordCore core) {
		mDisplayConfig.update(core);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public long onCreateWindow() {
		final long lWindowID = mDisplayConfig.createWindow(mGameInfo);

		if (lWindowID == MemoryUtil.NULL) {
			throw new RuntimeException("Failed to get correct GLFWWindow handle");
		}

		return lWindowID;
	}

	public void loadConfigFiles(configuration config) {
		switch (config) {
		case audioConfig:
			mAudioConfig.loadConfig();
			break;

		case videoConfig:
			mDisplayConfig.loadConfig();
			break;

		case all:
		default:
			loadConfigFiles(configuration.videoConfig);
			loadConfigFiles(configuration.audioConfig);
			break;
		}
	}

	public void saveConfigFiles(configuration config) {
		switch (config) {
		case audioConfig:
			audio().saveConfig();
			break;
		case videoConfig:
			display().saveConfig();
			break;
		case all:
		default:
			saveConfigFiles(configuration.videoConfig);
			saveConfigFiles(configuration.audioConfig);
			break;
		}
	}
}
