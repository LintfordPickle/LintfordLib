package net.lintford.library.core.debug;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.DebugLogger.LogMessage;
import net.lintford.library.core.debug.stats.DebugStats;
import net.lintford.library.renderers.debug.DebugControllerTreeRenderer;
import net.lintford.library.renderers.debug.DebugRendererTreeRenderer;

public class Debug {

	// --------------------------------------
	// Class Members
	// --------------------------------------

	private static Debug manager;
	private static boolean mDebugManagerCreated = false;

	public static Debug debugManager() {
		// If the DebugManager hasn't been created before the first call to retrieve it,
		// create a new instance but disbale all debugging.
		if (!mDebugManagerCreated)
			return debugManager(DebugLogLevel.off);

		return manager;

	}

	public static Debug debugManager(DebugLogLevel pDebugLogLevel) {
		if (Debug.manager == null) {
			Debug.manager = new Debug(pDebugLogLevel);
			mDebugManagerCreated = true;

		}

		return debugManager();

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

	private boolean mDebugManagerEnabled = false;

	private DebugLogLevel mCurrentLoggingLevel;

	private DebugLogger mDebugLogger;
	private DebugConsole mDebugConsole;
	private DebugDrawers mDebugDrawers;
	private DebugStats mDebugStats;
	private DebugRendererTreeRenderer mDebugRendererRenderer;
	private DebugControllerTreeRenderer mDebugControllerRenderer;

	private boolean mIsGLLoaded;

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

	private Debug(DebugLogLevel pDebugLogLevel) {
		setDebugMode(pDebugLogLevel);

		// We always need to instantiate this classes
		mDebugLogger = new DebugLogger(this);
		mDebugDrawers = new DebugDrawers(this);
		mDebugConsole = new DebugConsole(this);
		mDebugStats = new DebugStats(this);
		mDebugControllerRenderer = new DebugControllerTreeRenderer(this);
		mDebugRendererRenderer = new DebugRendererTreeRenderer(this);

		addDebugConsoleCommands();

		mDebugLogger.i("Help", "Type 'help' in the console to see a list of commands");

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(final ResourceManager pResourceManager) {
		mDebugConsole.loadGLContent(pResourceManager);
		mDebugDrawers.loadGLContent(pResourceManager);
		mDebugStats.loadGLContent(pResourceManager);
		mDebugControllerRenderer.loadGLContent(pResourceManager);
		mDebugRendererRenderer.loadGLContent(pResourceManager);

		mIsGLLoaded = true;

	}

	public void unloadGLContent() {
		mDebugConsole.unloadGLContent();
		mDebugDrawers.unloadGLContent();
		mDebugStats.unloadGLContent();
		mDebugControllerRenderer.unloadGLContent();
		mDebugRendererRenderer.unloadGLContent();

		mIsGLLoaded = false;

	}

	public void handleInput(LintfordCore pCore) {
		mDebugConsole.handleInput(pCore);
		mDebugStats.handleInput(pCore);
		mDebugControllerRenderer.handleInput(pCore);
		mDebugRendererRenderer.handleInput(pCore);

	}

	public void preUpdate(LintfordCore pCore) {
		mDebugStats.preUpdate(pCore);

	}

	public void update(LintfordCore pCore) {

		// Update the relative positions of the components based on which are currently enabled
		float lWindowWidth = -pCore.config().display().windowWidth() / 2;
		float lPosY = -pCore.config().display().windowHeight() / 2;
		if (mDebugConsole.isOpen()) {
			mDebugConsole.setPosition(lWindowWidth, lPosY);
			lPosY += mDebugConsole.height();

		}

		mDebugConsole.update(pCore);
		mDebugStats.update(pCore);
		mDebugRendererRenderer.update(pCore);
		mDebugControllerRenderer.update(pCore);

	}

	public void draw(LintfordCore pCore) {
		if (!mIsGLLoaded)
			return;

		mDebugStats.draw(pCore);
		mDebugConsole.draw(pCore);
		mDebugControllerRenderer.draw(pCore);
		mDebugRendererRenderer.draw(pCore);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void setDebugMode(DebugLogLevel pLoggingLevel) {
		if (pLoggingLevel == null)
			pLoggingLevel = DebugLogLevel.off;

		if (pLoggingLevel == DebugLogLevel.off) {
			mCurrentLoggingLevel = DebugLogLevel.off;
			mDebugManagerEnabled = false;

		} else {
			mDebugManagerEnabled = true;
			mCurrentLoggingLevel = pLoggingLevel;

		}

	}

	public void addDebugConsoleCommands() {
		// TODO: Add ConsoleCommands for all the static variables within ConstantsTable
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
