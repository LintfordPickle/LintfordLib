package net.lintford.library.core.debug;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import net.lintford.library.ConstantsTable;
import net.lintford.library.core.debug.DebugManager.DebugLogLevel;
import net.lintford.library.core.time.DateHelper;

public class DebugLogger {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final boolean LOGGER_ENABLED = true;
	public static boolean DEBUG_LOG_DEBUG_TO_FILE = true;
	public static final int LOG_BUFFER_LINE_COUNT = 1000;

	public static final String DEBUG_LOG_FILENAME = "debug";
	public static final String LOG_FILE_EXTENSION = ".log";

	final class LogMessage {

		// --------------------------------------
		// Variables
		// --------------------------------------

		String timestamp;
		String tag;
		String message;
		int type;

		// --------------------------------------
		// Constructor
		// --------------------------------------

		public LogMessage() {

		}

		public void setMessage(String pTag, String pMessage, String pTimestamp, DebugLogLevel pLevel) {
			// Check the level

			tag = pTag;
			timestamp = pTimestamp;
			message = pMessage;
			type = pLevel.logLevel;
		}

	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	private List<LogMessage> mLogLines;

	private DebugLogLevel mCurrentLoggingLevel;

	BufferedOutputStream mDebugLogBufferedOutputStream;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/** Returns all the messages currently in the log. */
	public List<LogMessage> logLines() {
		return mLogLines;
	}

	/** Returns the current logging level. */
	public DebugLogLevel getLogLevel() {
		return mCurrentLoggingLevel;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	DebugLogger() {
		mLogLines = new ArrayList<>();
		mCurrentLoggingLevel = DebugLogLevel.off;

		openDebugLogOutputStream();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private boolean openDebugLogOutputStream() {
		if (mDebugLogBufferedOutputStream != null)
			return false;

		try {
			// If unique log files names are specified, then append the date and time to the log filename.
			String LOG_FILENAME = null;
			if (ConstantsTable.getBooleanValueDef("DEBUG_UNIQUE_LOG_FILES", false)) {
				String lDateTime = DateHelper.getDateAsStringFileFriendly(new Date());
				LOG_FILENAME = DEBUG_LOG_FILENAME + "_" + lDateTime + LOG_FILE_EXTENSION;

			} else {
				LOG_FILENAME = DEBUG_LOG_FILENAME + LOG_FILE_EXTENSION;

			}

			i(getClass().getSimpleName(), "Creating new debug log file: " + LOG_FILENAME);

			// Create new files for the log (this is only applicable if the file currently exists, which is only applicable if DEBUG_UNIQUE_LOG_FILES is false).
			mDebugLogBufferedOutputStream = new BufferedOutputStream(new FileOutputStream(LOG_FILENAME, false), 24);
			return true;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;

		}
	}

	/** Sets the logging level. */
	public void setLoggingLevel(final DebugLogLevel pNewLoggingLevel) {
		if (pNewLoggingLevel == null) {
			mCurrentLoggingLevel = DebugLogLevel.off;
			return;
		}

		mCurrentLoggingLevel = pNewLoggingLevel;

	}

	public void log(DebugLogLevel pLogLevel, String pTag, String pMessage) {
		if(!LOGGER_ENABLED) {
			switch(pLogLevel) {
			default:
				System.out.println(padRight(pTag, 15) + ":" + pMessage);
				
			}
			
			// return;
		}
		
		if (pLogLevel.logLevel >= mCurrentLoggingLevel.logLevel) {

			LogMessage lLogMessage = null;
			if (mLogLines.size() < LOG_BUFFER_LINE_COUNT) {
				lLogMessage = new LogMessage();
			} else {
				// Non free, get the first from the linked list
				lLogMessage = mLogLines.remove(0);
			}

			if (lLogMessage == null) {
				System.err.println("DebugLogger: Unable to write to Debug log");
				return;
			}

			String lTimeStamp = new SimpleDateFormat("HH.mm.ss.SSS", Locale.US).format(new Date());
			lLogMessage.setMessage(pTag, pMessage, lTimeStamp, pLogLevel);

			if (DEBUG_LOG_DEBUG_TO_FILE) {
				writeDebugMessageToFile(lLogMessage.tag, lLogMessage.timestamp, lLogMessage.message);

			}

			mLogLines.add(lLogMessage);

		}
	}

	/** Adds a new EROR level message to the log. */
	public void e(String pTag, String pMessage) {
		log(DebugLogLevel.error, pTag, pMessage);

	}

	/** Adds a new WARNING level message to the log. */
	public void w(String pTag, String pMessage) {
		log(DebugLogLevel.warning, pTag, pMessage);

	}

	/** Adds a new INFO level message to the log. */
	public void i(String pTag, String pMessage) {
		log(DebugLogLevel.info, pTag, pMessage);

	}

	/** Adds a new VERBOSE level message to the log. */
	public void v(String pTag, String pMessage) {
		log(DebugLogLevel.verbose, pTag, pMessage);

	}

	/** Adds a new SYSTEM level message to the log. */
	public void s(String pTag, String pMessage) {
		log(DebugLogLevel.system, pTag, pMessage);

	}

	/** Adds a new USER level message to the log. */
	public void u(String pTag, String pMessage) {
		log(DebugLogLevel.user, pTag, pMessage);

	}

	/** Appends the given message into a file at the given location. */
	public boolean writeDebugMessageToFile(String pTag, String pTimestamp, String pMessage) {
		if (mDebugLogBufferedOutputStream == null)
			return false;

		try {

			mDebugLogBufferedOutputStream.write(pTimestamp.getBytes());
			mDebugLogBufferedOutputStream.write(": ".getBytes());
			mDebugLogBufferedOutputStream.write(pTag.getBytes());
			mDebugLogBufferedOutputStream.write(": ".getBytes());
			mDebugLogBufferedOutputStream.write(pMessage.getBytes());
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

			for (LogMessage m : mLogLines) {

				mDebugLogBufferedOutputStream.write(m.tag.getBytes());
				mDebugLogBufferedOutputStream.write(": ".getBytes());
				mDebugLogBufferedOutputStream.write(m.message.getBytes());
				mDebugLogBufferedOutputStream.write("\n".getBytes());

			}

			return true;

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

	}

	public static String padRight(String s, int n) {
	     return String.format("%1$-" + n + "s", s);  
	}
	
}