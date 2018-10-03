package net.lintford.library.options;

import org.lwjgl.system.MemoryUtil;

import net.lintford.library.ConstantsTable;
import net.lintford.library.GameInfo;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.storage.AppStorage;

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

	public MasterConfig(final GameInfo pGameInfo) {
		mGameInfo = pGameInfo;

		// Make sure that a game save directory exists for this application...
		AppStorage.createGameDataDirectory(ConstantsTable.getStringValueDef("APPLICATION_NAME", "LintfordLib"));

		final String lDisplayConfigFilename = AppStorage.getGameDataDirectory() + VIDEO_CONFIG_FILENAME;
		mDisplayConfig = new DisplayManager(pGameInfo, lDisplayConfigFilename);

		final String lAudioConfigFilename = AppStorage.getGameDataDirectory() + AUDIO_CONFIG_FILENAME;
		mAudioConfig = new AudioConfig(pGameInfo, lAudioConfigFilename);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void handleInput(LintfordCore pCore) {

	}

	public void update(LintfordCore pCore) {
		mDisplayConfig.update(pCore);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public long onCreateWindow() {

		long lWindowID = mDisplayConfig.createWindow(mGameInfo);

		if (lWindowID == MemoryUtil.NULL) {
			throw new RuntimeException("Failed to get correct GLFWWindow handle");

		}

		return lWindowID;

	}

	public void loadConfigFiles(configuration c) {
		switch (c) {
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

	public void saveConfigFiles(configuration c) {
		switch (c) {
		case audioConfig:
			break;
		case videoConfig:
			break;
		case all:
		default:
			saveConfigFiles(configuration.videoConfig);
			saveConfigFiles(configuration.audioConfig);
			break;
		}
	}

}
