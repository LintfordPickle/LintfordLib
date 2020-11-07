package net.lintford.library;

import net.lintford.library.core.debug.Debug.DebugLogLevel;
import net.lintford.library.core.storage.AppStorage;
import net.lintford.library.options.DisplayManager;

/**
 * The {@link GameInfo} interface defines the properties for an application, as well as the default settings to use by the {@link DisplayManager} when creating an OpenGL window for the first time (i.e. when no
 * DisplayConfig.INI exists).
 */
public interface GameInfo {

	public static final int DEF_BASE_GAME_RESOLUTION_W = 800;
	public static final int DEF_BASE_GAME_RESOLUTION_H = 600;

	public static final int MIN_BASE_GAME_RESOLUTION_W = 800;
	public static final int MIN_BASE_GAME_RESOLUTION_H = 600;

	/* Specifies the debug logging level. Default is off (no logs are generated). */
	public default DebugLogLevel debugLogLevel() {
		return DebugLogLevel.off;
	}

	/** Returns the application name. */
	public default String applicationName() {
		return "unnamed";
	}

	/** Returns the window title */
	public default String windowTitle() {
		return "unamed";
	}

	/** Returns the directory to store configuration files. null is returned if not set. */
	public default String configFileLocation() {
		return AppStorage.getGameDataDirectory();

	}

	public default boolean defaultFullScreen() {
		return false;
	}

	public default int defaultSoundFXVolume() {
		return 100;
	}

	public default int defaultMusicVolume() {
		return 100;
	}

	public default boolean stretchGameResolution() {
		return false;
	}

	public default int baseGameResolutionWidth() {
		return DEF_BASE_GAME_RESOLUTION_W;
	}

	public default int baseGameResolutionHeight() {
		return DEF_BASE_GAME_RESOLUTION_H;
	}

	public default int minimumWindowWidth() {
		return MIN_BASE_GAME_RESOLUTION_W;
	}

	public default int minimumWindowHeight() {
		return MIN_BASE_GAME_RESOLUTION_H;
	}

	/** Returns true if the OpenGL window is resizable or not. */
	public default boolean windowResizeable() {
		return false;
	}

	/** Returns true if the OpenGL window can be toggled fullscreen. */
	public default boolean windowCanBeFullscreen() {
		return true;
	}

	/**
	 * If return value is true, the game will use a constant size backbuffer to render the game, which will be stretched to fit the window.
	 */
	public default boolean stretchGameViewportToWindow() {
		return false;
	}

	/**
	 * If return value is true, the {@link DisplayManager} will take the aspect ratio into account when stretching the back buffer to fit the window.
	 */
	public default boolean maintainAspectRatio() {
		return true;
	}

}
