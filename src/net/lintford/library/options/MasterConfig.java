package net.lintford.library.options;

import org.lwjgl.system.MemoryUtil;

import net.lintford.library.GameInfo;
import net.lintford.library.core.LintfordCore;

public class MasterConfig {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final String SOUND_CONFIG_FILENAME = "SoundConfig.ini";
	private static final String MUSIC_CONFIG_FILENAME = "MusicConfig.ini";
	private static final String DISPLAY_CONFIG_FILENAME = "DisplayConfig.ini";
	private static final String GAME_CONFIG_FILENAME = "GameConfig.ini";

	public enum configuration {
		soundConfig, musicConfig, displayConfig, gameConfig, all,
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	// TODO: InputConfig for key and mouse mapping
	private static final GameConfig mGameConfig = new GameConfig(GAME_CONFIG_FILENAME);
	private static final SoundConfig mSoundConfig = new SoundConfig(SOUND_CONFIG_FILENAME);
	private static final MusicConfig mMusicConfig = new MusicConfig(MUSIC_CONFIG_FILENAME);
	private static final DisplayConfig DISPLAY_CONFIG = new DisplayConfig(DISPLAY_CONFIG_FILENAME);

	// --------------------------------------
	// Properties
	// --------------------------------------

	public GameConfig game() {
		return mGameConfig;
	}

	public SoundConfig sound() {
		return mSoundConfig;
	}

	public MusicConfig music() {
		return mMusicConfig;
	}

	public DisplayConfig display() {
		return DISPLAY_CONFIG;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MasterConfig() {

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void handleInput(LintfordCore pCore) {

	}

	public void update(LintfordCore pCore) {
		DISPLAY_CONFIG.update(pCore);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public long onCreateWindow(GameInfo pGameInfo) {
		long lWindowID = DISPLAY_CONFIG.onCreateWindow(pGameInfo);

		if (lWindowID == MemoryUtil.NULL) {
			throw new RuntimeException("Failed to get correct GLFWWindow handle");
		}

		return lWindowID;

	}

	public void loadConfigFiles(configuration c) {
		switch (c) {
		case musicConfig:
			mMusicConfig.loadConfig();
			break;
		case soundConfig:
			mSoundConfig.loadConfig();
			break;
		case gameConfig:
			mGameConfig.loadConfig();
			break;
		case displayConfig:
			DISPLAY_CONFIG.loadConfig();
			break;
		case all:
		default:
			loadConfigFiles(configuration.displayConfig);
			loadConfigFiles(configuration.soundConfig);
			loadConfigFiles(configuration.musicConfig);
			loadConfigFiles(configuration.gameConfig);
			break;
		}
	}

	public void saveConfigFiles(configuration c) {
		switch (c) {
		case musicConfig:
			mMusicConfig.saveConfig();
			break;
		case soundConfig:
			mSoundConfig.saveConfig();
			break;
		case gameConfig:
			mGameConfig.saveConfig();
			break;
		case displayConfig:
			DISPLAY_CONFIG.saveConfig();
			break;
		case all:
		default:
			saveConfigFiles(configuration.displayConfig);
			saveConfigFiles(configuration.soundConfig);
			saveConfigFiles(configuration.musicConfig);
			saveConfigFiles(configuration.gameConfig);
			break;
		}
	}

}
