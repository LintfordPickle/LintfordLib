package net.lintford.library.core.storage;

import java.io.File;

import net.lintford.library.ConstantsApp;
import net.lintford.library.core.debug.Debug;

public class AppStorage {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String FILE_SEPERATOR = System.getProperty("file.separator");
	public static final String LINE_SEPERATOR = System.getProperty("line.separator");

	public static boolean useLocalDirectory = true;

	// --------------------------------------
	// Methods
	// --------------------------------------

	public static String createGameDataDirectory(String pApplicationName) {
		String lSaveFolder = null;
		String os = System.getProperty("os.name").toLowerCase();

		if (useLocalDirectory) {
			lSaveFolder = System.getProperty("user.dir") + FILE_SEPERATOR + "config" + FILE_SEPERATOR;

		} else {
			if (os.startsWith("win")) {
				lSaveFolder = System.getenv("AppData") + FILE_SEPERATOR + pApplicationName + FILE_SEPERATOR;

			} else if (os.startsWith("linux") || os.startsWith("mac") || os.startsWith("darwin")) {
				lSaveFolder = System.getProperty("user.home") + FILE_SEPERATOR + "." + pApplicationName + FILE_SEPERATOR;

			}

		}

		File directory = new File(String.valueOf(lSaveFolder));
		if (!directory.exists()) {
			directory.mkdir();

		}

		Debug.debugManager().logger().i("Storage", "Creating game data directory at: " + lSaveFolder);
		return lSaveFolder;
	}

	/**
	 * Returns a platform dependant folder which can be used for saving application data. The data folder will be created as Roaming\pApplicationName
	 */
	public static String getGameDataDirectory(String pApplicationName) {
		String lSaveFolder = null;
		String os = System.getProperty("os.name").toLowerCase();

		if (useLocalDirectory) {
			lSaveFolder = System.getProperty("user.dir") + FILE_SEPERATOR + "config" + FILE_SEPERATOR;

		} else {
			if (os.startsWith("win")) {
				lSaveFolder = System.getenv("AppData") + FILE_SEPERATOR + pApplicationName + FILE_SEPERATOR;

			} else if (os.startsWith("linux") || os.startsWith("mac") || os.startsWith("darwin")) {
				lSaveFolder = System.getProperty("user.home") + FILE_SEPERATOR + "." + pApplicationName + FILE_SEPERATOR;

			}

		}

		return lSaveFolder;
	}

	/**
	 * Returns a platform dependant folder which can be used for saving application data. Will use the APPLICATION_NAME from the ConstantsTable, if one has been defined.
	 */
	public static String getGameDataDirectory() {
		final String lApplicationName = ConstantsApp.getStringValueDef("APPLICATION_NAME", "LintfordLib");

		return getGameDataDirectory(lApplicationName);

	}

}
