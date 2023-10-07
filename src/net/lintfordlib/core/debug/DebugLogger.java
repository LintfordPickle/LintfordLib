package net.lintfordlib.core.debug;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.core.debug.Debug.DebugLogLevel;
import net.lintfordlib.core.messaging.Message;
import net.lintfordlib.core.time.DateHelper;

public class DebugLogger {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("HH.mm.ss.SSS", Locale.US);

	public static boolean DEBUG_LOG_THREAD_NAMES = true;
	public static boolean DEBUG_LOG_DEBUG_TO_FILE = true;
	public static final int LOG_BUFFER_LINE_COUNT = 1000;

	public static final String DEBUG_LOG_FILENAME = "debug";
	public static final String LOG_FILE_EXTENSION = ".log";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private final Debug mDebugManager;
	private boolean mMirrorLogToConsole;
	private List<Message> mLogLinePool;
	private List<Message> mLogLines;
	private BufferedOutputStream mDebugLogBufferedOutputStream;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/** Returns all the messages currently in the log. */
	public List<Message> logLines() {
		return mLogLines;
	}

	public void mirrorLogToConsole(boolean mirrorLogToConsole) {
		mMirrorLogToConsole = mirrorLogToConsole;
	}

	public boolean mirrorLogToConsole() {
		return mMirrorLogToConsole;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	DebugLogger(final Debug debugManager) {
		mDebugManager = debugManager;

		if (!mDebugManager.debugManagerEnabled())
			return;

		mLogLines = new ArrayList<>(LOG_BUFFER_LINE_COUNT);
		mLogLinePool = new ArrayList<>(LOG_BUFFER_LINE_COUNT);

		for (int i = 0; i < LOG_BUFFER_LINE_COUNT; i++) {
			mLogLinePool.add(new Message());
		}

		openDebugLogOutputStream();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void clearLogLines() {
		mLogLines.clear();
	}

	private boolean openDebugLogOutputStream() {
		if (mDebugLogBufferedOutputStream != null)
			return false;

		try {
			// If unique log files names are specified, then append the date and time to the log filename.
			String lLogFilename = null;
			if (ConstantsApp.getBooleanValueDef("DEBUG_UNIQUE_LOG_FILES", false)) {
				final var lDateTime = DateHelper.getDateAsStringFileFriendly(new Date());
				lLogFilename = DEBUG_LOG_FILENAME + "_" + lDateTime + LOG_FILE_EXTENSION;
			} else {
				lLogFilename = DEBUG_LOG_FILENAME + LOG_FILE_EXTENSION;
			}

			i(getClass().getSimpleName(), "Creating new debug log file: " + lLogFilename);

			// Create new files for the log (this is only applicable if the file currently exists, which is only applicable if DEBUG_UNIQUE_LOG_FILES is false).
			mDebugLogBufferedOutputStream = new BufferedOutputStream(new FileOutputStream(lLogFilename, false), 24);
			return true;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}

	private void log(DebugLogLevel logLevel, String tag, String message) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (tag.equals("") || message.equals("")) {
			return;
		}

		if (logLevel.logLevel >= mDebugManager.getLogLevel().logLevel) {
			Message lLogMessage = null;
			if (mLogLinePool.size() > 0) {
				// Remove from the pool until empty
				lLogMessage = mLogLinePool.remove(0);

			} else {
				// Non free, get the first from the linked list
				lLogMessage = mLogLines.remove(0);
			}

			if (lLogMessage == null) {
				System.err.println("DebugLogger: Unable to write to Debug log");
				return;
			}

			if (DEBUG_LOG_THREAD_NAMES) {
				final var lThreadName = Thread.currentThread().getName();
				message = "[" + lThreadName + "] " + message;
			}

			var lTimeStamp = SIMPLE_DATE_FORMAT.format(new Date());
			lLogMessage.setMessage(tag, message, lTimeStamp, logLevel.logLevel);

			if (DEBUG_LOG_DEBUG_TO_FILE) {
				writeDebugMessageToFile(lLogMessage.tag(), lLogMessage.timestamp(), lLogMessage.message());
			}

			mLogLines.add(lLogMessage);

			if (mMirrorLogToConsole)
				System.out.printf("[%s] %s: %s\n", padRight(lLogMessage.timestamp(), 12), padRight(lLogMessage.tag(), 25), lLogMessage.message());
		}
	}

	/** Adds a new EROR level message to the log. */
	public void e(String tag, String message) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (message == null)
			return;

		synchronized (this) {
			log(DebugLogLevel.error, tag, message);
		}
	}

	/** Adds a new WARNING level message to the log. */
	public void w(String tag, String message) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		synchronized (this) {
			log(DebugLogLevel.warning, tag, message);
		}
	}

	/** Adds a new INFO level message to the log. */
	public void i(String tag, String message) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		synchronized (this) {
			log(DebugLogLevel.info, tag, message);
		}
	}

	/** Adds a new VERBOSE level message to the log. */
	public void v(String tag, String message) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		log(DebugLogLevel.verbose, tag, message);
	}

	/** Adds a new SYSTEM level message to the log. */
	public void s(String tag, String message) {
		if (!mDebugManager.debugManagerEnabled())
			return;
		synchronized (this) {
			log(DebugLogLevel.system, tag, message);
		}
	}

	/** Adds a new USER level message to the log. */
	public void u(String tag, String message) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		synchronized (this) {
			log(DebugLogLevel.user, tag, message);
		}
	}

	public void printException(String tag, Exception exception) {
		printException(tag, exception, true);
	}

	public void printException(String tag, Exception exception, boolean includeStackTrace) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		e(tag, exception.getMessage());
		if (includeStackTrace) {
			exception.printStackTrace(System.err);
		}
	}

	public void printStacktrace(String tag) {
		var st = Thread.currentThread().getStackTrace();
		for (final var ste : st) {
			log(DebugLogLevel.info, tag, ste.toString());
		}
	}

	/** Appends the given message into a file at the given location. */
	public boolean writeDebugMessageToFile(String tag, String timestamp, String message) {
		if (!mDebugManager.debugManagerEnabled())
			return false;

		if (mDebugLogBufferedOutputStream == null)
			return false;

		try {
			mDebugLogBufferedOutputStream.write(timestamp.getBytes());
			mDebugLogBufferedOutputStream.write(": ".getBytes());
			mDebugLogBufferedOutputStream.write(tag.getBytes());
			mDebugLogBufferedOutputStream.write(": ".getBytes());
			mDebugLogBufferedOutputStream.write(message.getBytes());
			mDebugLogBufferedOutputStream.write("\n".getBytes());

			return true;

		} catch (IOException e) {
			e.printStackTrace();

			return false;
		}
	}

	/** Creates a new log file at the given location, and writes the current contents of the logger. */
	boolean writeDebugLogToFile() {
		if (mDebugLogBufferedOutputStream == null)
			return false;

		try {
			final int lMessageCount = mLogLines.size();
			for (int i = 0; i < lMessageCount; i++) {
				final var lMessage = mLogLines.get(i);
				mDebugLogBufferedOutputStream.write(lMessage.tag().getBytes());
				mDebugLogBufferedOutputStream.write(": ".getBytes());
				mDebugLogBufferedOutputStream.write(lMessage.message().getBytes());
				mDebugLogBufferedOutputStream.write("\n".getBytes());
			}

			return true;

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static String padRight(String string, int amount) {
		return String.format("%1$-" + amount + "s", string);
	}

}