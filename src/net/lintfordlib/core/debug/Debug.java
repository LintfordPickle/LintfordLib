package net.lintfordlib.core.debug;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.ResourceManager;
import net.lintfordlib.core.debug.stats.DebugStats;

public class Debug {

	// --------------------------------------
	// Class Members
	// --------------------------------------

	private static Debug manager;
	private static boolean mDebugManagerCreated = false;

	public static Debug debugManager() {
		if (!mDebugManagerCreated)
			return debugManager(DebugLogLevel.off);

		return manager;
	}

	public static Debug debugManager(DebugLogLevel debugLogLevel) {
		if (Debug.manager == null) {
			Debug.manager = new Debug(debugLogLevel);
			mDebugManagerCreated = true;
		}

		return debugManager();
	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	/**
	 * Debug message used to log an error to the console if a feature was removed for testing
	 */
	public static final void notifyReleaseFeatureDisabled(String message) {
		debugManager().logger().e("FEATURE DISABLED", message);
	}

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

	private boolean mDebugManagerEnabled = false;
	private DebugLogLevel mCurrentLoggingLevel;
	private DebugLogger mDebugLogger;
	private DebugConsole mDebugConsole;
	private DebugDrawers mDebugDrawers;
	private DebugStats mDebugStats;
	private boolean mResourcesLoaded;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean debugModeEnabled() {
		return mCurrentLoggingLevel.logLevel > DebugLogLevel.off.logLevel;
	}

	/** Returns the current logging level. */
	public DebugLogLevel getLogLevel() {
		return mCurrentLoggingLevel;
	}

	public boolean debugManagerEnabled() {
		return mDebugManagerEnabled;
	}

	public DebugConsole console() {
		return mDebugConsole;

	}

	public DebugDrawers drawers() {
		return mDebugDrawers;

	}

	public DebugLogger logger() {
		return mDebugLogger;

	}

	public DebugStats stats() {
		return mDebugStats;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	private Debug(DebugLogLevel debugLogLevel) {
		setDebugMode(debugLogLevel);

		mDebugLogger = new DebugLogger(this);
		mDebugDrawers = new DebugDrawers(this);
		mDebugConsole = new DebugConsole(this);
		mDebugStats = new DebugStats(this);

		addDebugConsoleCommands();

		mDebugLogger.i("Help", "Type 'help' in the console to see a list of commands");
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadResources(final ResourceManager resourceManager) {
		mDebugConsole.loadResources(resourceManager);
		mDebugDrawers.loadResources(resourceManager);
		mDebugStats.loadResources(resourceManager);

		mResourcesLoaded = true;
	}

	public void unloadResources() {
		mDebugConsole.unloadResources();
		mDebugDrawers.unloadResources();
		mDebugStats.unloadResources();

		mResourcesLoaded = false;
	}

	public void handleInput(LintfordCore core) {
		mDebugConsole.handleInput(core);
		mDebugStats.handleInput(core);
	}

	public void preUpdate(LintfordCore core) {
		mDebugStats.preUpdate(core);
	}

	public void update(LintfordCore core) {
		float lWindowWidth = -core.config().display().windowWidth() / 2;
		float lPosY = -core.config().display().windowHeight() / 2;
		if (mDebugConsole.isOpen()) {
			mDebugConsole.setPosition(lWindowWidth, lPosY);
			lPosY += mDebugConsole.height();

		}

		mDebugConsole.update(core);
		mDebugStats.update(core);
	}

	public void draw(LintfordCore core) {
		if (!mResourcesLoaded)
			return;

		mDebugConsole.draw(core);
		mDebugStats.draw(core);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void setDebugMode(DebugLogLevel loggingLevel) {
		if (loggingLevel == null)
			loggingLevel = DebugLogLevel.off;

		if (loggingLevel == DebugLogLevel.off) {
			mCurrentLoggingLevel = DebugLogLevel.off;
			mDebugManagerEnabled = false;

		} else {
			mDebugManagerEnabled = true;
			mCurrentLoggingLevel = loggingLevel;
		}
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
