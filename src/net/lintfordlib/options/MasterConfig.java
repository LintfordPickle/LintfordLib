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

	private static final String VIDEO_CONFIG_FILENAME = "video.ini";
	private static final String AUDIO_CONFIG_FILENAME = "audio.ini";
	private static final String RESOURCES_CONFIG_FILENAME = "resources.ini";

	public enum configuration {
		audioConfig, videoConfig, inputConfig, resourcePaths, all,
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	// TODO: InputConfig for key and mouse mapping
	private DisplayManager mDisplayConfig;
	private AudioConfig mAudioConfig;
	private ResourcePathsConfig mResourcePathsConfig;

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

	public ResourcePathsConfig resourcePaths() {
		return mResourcePathsConfig;
	}

	public GameInfo gameInfor() {
		return mGameInfo;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MasterConfig(final GameInfo gameInfo) {
		mGameInfo = gameInfo;

		// Make sure that a game config and data directories exist for this application...
		AppStorage.createGameDirectories(ConstantsApp.getStringValueDef(ConstantsApp.CONSTANT_APP_NAME_TAG, "LintfordLib"));

		final String lDisplayConfigFilename = AppStorage.getGameConfigDirectory() + VIDEO_CONFIG_FILENAME;
		Debug.debugManager().logger().i(getClass().getSimpleName(), "Loading video settings from '" + lDisplayConfigFilename + "'");
		mDisplayConfig = new DisplayManager(gameInfo, lDisplayConfigFilename);

		final String lAudioConfigFilename = AppStorage.getGameConfigDirectory() + AUDIO_CONFIG_FILENAME;
		Debug.debugManager().logger().i(getClass().getSimpleName(), "Loading audio settings from '" + lAudioConfigFilename + "'");
		mAudioConfig = new AudioConfig(gameInfo, lAudioConfigFilename);

		final String lResourcePathConfigFilename = AppStorage.getGameConfigDirectory() + RESOURCES_CONFIG_FILENAME;
		Debug.debugManager().logger().i(getClass().getSimpleName(), "Loading resource path config from '" + lResourcePathConfigFilename + "'");
		mResourcePathsConfig = new ResourcePathsConfig(lResourcePathConfigFilename);
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

		case resourcePaths:
			mResourcePathsConfig.loadConfig();
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
		case inputConfig:
			// TODO: save InputConfig (key bindings, senstivity etc.)
			break;
		case resourcePaths:
			resourcePaths().saveConfig();
			break;
		case all:
		default:
			saveConfigFiles(configuration.videoConfig);
			saveConfigFiles(configuration.audioConfig);
			break;
		}
	}
}
