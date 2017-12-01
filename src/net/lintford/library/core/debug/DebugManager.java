package net.lintford.library.core.debug;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.debug.DebugLogger.LogMessage;
import net.lintford.library.core.graphics.ResourceManager;

public class DebugManager {

	// --------------------------------------
	// Class Members
	// --------------------------------------

	public static DebugManager DEBUG_MANAGER = debugManager();

	static DebugManager debugManager() {
		if (DebugManager.DEBUG_MANAGER == null) {
			DebugManager.DEBUG_MANAGER = new DebugManager();

		}

		return DEBUG_MANAGER;

	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int LOG_LEVEL_OFF = 0;
	public static final int LOG_LEVEL_VERBOSE = 1;
	public static final int LOG_LEVEL_INFO = 2;
	public static final int LOG_LEVEL_WARNING = 3;
	public static final int LOG_LEVEL_ERROR = 4;

	public static final int LOG_LEVEL_USER = 5;
	public static final int LOG_LEVEL_SYSTEM = 6;

	// --------------------------------------
	// Enums
	// --------------------------------------

	/** A convenience class to constrain the value given in a {@link LogMessage}'s log level. */
	public enum DebugLogLevel {
		verbose(LOG_LEVEL_VERBOSE), info(LOG_LEVEL_INFO), warning(LOG_LEVEL_WARNING), error(LOG_LEVEL_ERROR), system(LOG_LEVEL_SYSTEM), user(LOG_LEVEL_USER), off(LOG_LEVEL_OFF);

		public final int logLevel;

		DebugLogLevel(int pV) {
			logLevel = pV;
		};

	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	private DebugLogger mDebugLogger;
	private DebugConsole mDebugConsole;
	private DebugDrawers mDebugDrawers;

	private boolean mIsGLLoaded;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public DebugConsole console() {
		return mDebugConsole;

	}

	public DebugDrawers drawers() {
		return mDebugDrawers;

	}

	public DebugLogger logger() {
		return mDebugLogger;

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	private DebugManager() {
		mDebugLogger = new DebugLogger();
		mDebugDrawers = new DebugDrawers();
		mDebugConsole = new DebugConsole();

		addDebugConsoleCommands();

		mDebugLogger.i("Help", "Type 'help' in the console to see a list of commands");

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(final ResourceManager pResourceManager) {
		mDebugConsole.loadGLContent(pResourceManager);
		mDebugDrawers.loadGLContent(pResourceManager);
		mIsGLLoaded = true;

	}

	public void unloadGLContent() {
		mDebugConsole.unloadGLContent();
		mDebugDrawers.unloadGLContent();

		mIsGLLoaded = false;

	}

	public void handleInput(LintfordCore pCore) {
		mDebugConsole.handleInput(pCore);

	}

	public void update(LintfordCore pCore) {
		mDebugConsole.update(pCore);

	}

	public void draw(LintfordCore pCore) {
		if (!mIsGLLoaded)
			return;

		mDebugConsole.draw(pCore);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void startDebug() {
		startDebug(DebugLogLevel.verbose);

	}

	public void startDebug(DebugLogLevel pLoggingLevel) {
		if (pLoggingLevel == null)
			pLoggingLevel = DebugLogLevel.error;

		mDebugLogger.i(getClass().getSimpleName(), "Starting Debug with logging level " + pLoggingLevel.toString());

		mDebugLogger.setLoggingLevel(pLoggingLevel);

	}

	public void addDebugConsoleCommands() {
		ConsoleCommand lSaveLogCommand = new ConsoleCommand("DebugLogger", "save_log", "writes the current console log to a file on the hard disk (info.log)") {
			@Override
			public boolean doCommand() {
				return mDebugLogger.writeDebugLogToFile();

			}
		};

		ConsoleCommand lListConsoleCommands = new ConsoleCommand("System", "help", "Lists all the console commands available") {

			@Override
			public boolean doCommand() {
				mDebugConsole.listConsoleCommands();
				return true;
			}

		};

		mDebugConsole.addConsoleCommand(lListConsoleCommands);
		mDebugConsole.addConsoleCommand(lSaveLogCommand);

	}

}
