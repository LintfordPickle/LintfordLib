package net.lintford.library;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.options.DisplayConfig;

/** The {@link GameInfo} interface defines the properties used by {@link LintfordCore} when creating an OpenGL window. */
public interface GameInfo {

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
		return null;

	}

	/** Returns the startup width of the OpenGL window to be created. */
	public default int windowWidth() {
		return 800;
	}

	/** Returns the startup height of the OpenGL window to be created. */
	public default int windowHeight() {
		return 600;
	}

	/** Returns true if the OpenGL window is resizable or not. */
	public default boolean windowResizeable() {
		return false;
	}

	/** Returns true if the OpenGL window can be toggled fullscreen. */
	public default boolean windowCanBeFullscreen() {
		return false;
	}

	/**
	 * If return value is true, the game will use a constant size backbuffer to render the game, which will be stretched to fit the window.
	 */
	public default boolean stretchGameViewportToWindow() {
		return false;
	}

	/**
	 * If return value is true, the {@link DisplayConfig} will take the aspect ratio into account when stretching the back buffer to fit the window.
	 */
	public default boolean maintainAspectRatio() {
		return true;
	}

}
